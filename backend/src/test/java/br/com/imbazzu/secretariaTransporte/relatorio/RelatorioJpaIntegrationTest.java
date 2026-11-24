package br.com.imbazzu.secretariaTransporte.relatorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.RelatorioJpaRepository;
import br.com.imbazzu.secretariaTransporte.relatorio.core.domain.FiltroRelatorio;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "api.security.token.secret=test-secret",
        "api.security.token.assinatura=test-signature",
        "api.security.origenspermitidas=http://localhost"
})
@AutoConfigureMockMvc(addFilters = false)
class RelatorioJpaIntegrationTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private JdbcTemplate jdbc;
    @Autowired private RelatorioJpaRepository repositorio;
    @Autowired private MockMvc mvc;

    @Test
    @Transactional
    void consultaDadosExistentesEFiltroDePeriodo() throws Exception {
        jdbc.update("INSERT INTO cidades (id,nome,estado,tempo_viagem) VALUES (UNHEX('00000000000000000000000000000011'),'Cidade','SP',0)");
        jdbc.update("INSERT INTO destinos (id,nome,cidade_id) VALUES (UNHEX('00000000000000000000000000000012'),'Destino',UNHEX('00000000000000000000000000000011'))");
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000013'),'Lista')");
        jdbc.update("INSERT INTO tipo_veiculos (id,nome,capacidade) VALUES (UNHEX('00000000000000000000000000000014'),'Tipo',10)");
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES (UNHEX('00000000000000000000000000000021'),'Pessoa','11987654321',UNHEX('00000000000000000000000000000001'))");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2030-01-01',NOW()),('2030-01-02',NOW())");
        jdbc.update("INSERT INTO veiculos_agendamento (id,data,tipo_veiculo_id,nome_tipo_veiculo,capacidade,hora_saida_calculada) VALUES (UNHEX('00000000000000000000000000000031'),'2030-01-01',UNHEX('00000000000000000000000000000014'),'Tipo',10,0)");
        jdbc.update("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes,veiculo_agendamento_id) VALUES "
                + "(UNHEX('00000000000000000000000000000041'),'2030-01-01',UNHEX('00000000000000000000000000000021'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000014'),UNHEX('00000000000000000000000000000012'),0,0,0,0,'10:00',0,0,UNHEX('00000000000000000000000000000031')),"
                + "(UNHEX('00000000000000000000000000000042'),'2030-01-02',UNHEX('00000000000000000000000000000021'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000014'),UNHEX('00000000000000000000000000000012'),0,0,0,0,'10:00',0,0,NULL)");

        var filtro = new FiltroRelatorio(LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 1), null, null, null, null, 0, 20);
        var agendamentos = repositorio.obterEstatisticasAgendamentos(filtro);
        assertEquals(1L, agendamentos.getTotalAgendamentos());
        assertEquals(1L, agendamentos.getAgendamentosPorData().getFirst().getQuantidade());
        assertEquals("Destino", agendamentos.getAgendamentosPorDestino().getFirst().getDestino());
        assertNull(agendamentos.getAgendamentosRealizados());
        var pessoas = repositorio.obterEstatisticasPessoas(filtro);
        assertEquals(1L, pessoas.getPessoasAtivas());
        assertEquals(1L, pessoas.getPessoasMaisFrequentes().getFirst().getTotalAgendamentos());
        assertEquals(2L, repositorio.obterEstatisticasVeiculos(filtro).getVeiculosAtivos());
        var operacional = repositorio.obterEstatisticasOperacionais(filtro);
        assertEquals(1L, operacional.getAgendamentosAlocados());
        assertEquals(0L, operacional.getAgendamentosSemVeiculo());

        var todos = new FiltroRelatorio(null, null, null, null, null, null, 0, 20);
        assertEquals(2L, repositorio.obterEstatisticasAgendamentos(todos).getTotalAgendamentos());
        assertEquals(1L, repositorio.obterEstatisticasOperacionais(todos).getAgendamentosSemVeiculo());

        mvc.perform(get("/api/relatorios/agendamentos").contextPath("/api")
                .param("dataInicio", "2030-01-01").param("dataFim", "2030-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAgendamentos").value(1))
                .andExpect(jsonPath("$.agendamentosPorData[0].data").value("2030-01-01"));
        mvc.perform(get("/api/relatorios/agendamentos").contextPath("/api")
                .param("dataInicio", "2030-01-02").param("dataFim", "2030-01-01"))
                .andExpect(status().isBadRequest());

        jdbc.update("UPDATE destinos SET data_arquivado='2030-02-01' WHERE id=UNHEX('00000000000000000000000000000012')");
        jdbc.update("UPDATE listas SET data_arquivado='2030-02-01' WHERE id=UNHEX('00000000000000000000000000000013')");
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000019'),'Lista sem agendamentos')");
        mvc.perform(get("/api/relatorios/passageiros-opcoes").contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listas[*].nome", org.hamcrest.Matchers.hasItems("Lista", "Lista sem agendamentos")))
                .andExpect(jsonPath("$.cidades[*].nome", org.hamcrest.Matchers.hasItem("Cidade")))
                .andExpect(jsonPath("$.destinos[0].nome").value("Destino"))
                .andExpect(jsonPath("$.destinos[0].idCidade").value("00000000-0000-0000-0000-000000000011"))
                .andExpect(jsonPath("$.tiposVeiculo[*].nome", org.hamcrest.Matchers.hasItem("Tipo")));
        mvc.perform(get("/api/relatorios/passageiros-periodo").contextPath("/api")
                .param("dataInicio", "2030-01-01").param("dataFim", "2030-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Pessoa"))
                .andExpect(jsonPath("$[0].data").value("2030-01-01"))
                .andExpect(jsonPath("$[0].lista").value("Lista"))
                .andExpect(jsonPath("$[1].cidade").value("Cidade"))
                .andExpect(jsonPath("$[1].destino").value("Destino"));
        mvc.perform(get("/api/relatorios/passageiros-periodo").contextPath("/api")
                .param("dataInicio", "2030-01-02").param("dataFim", "2030-01-01"))
                .andExpect(status().isBadRequest());

        jdbc.update("UPDATE veiculos_agendamento SET hora_saida='06:30:00' WHERE id=UNHEX('00000000000000000000000000000031')");
        jdbc.update("INSERT INTO veiculos_agendamento (id,data,tipo_veiculo_id,nome_tipo_veiculo,capacidade,hora_saida_calculada) VALUES "
                + "(UNHEX('00000000000000000000000000000032'),'2030-01-02',UNHEX('00000000000000000000000000000014'),'Tipo',10,1)");
        jdbc.update("UPDATE agendamentos SET veiculo_agendamento_id=UNHEX('00000000000000000000000000000032'), tempo_viagem_segundos=3600 "
                + "WHERE id=UNHEX('00000000000000000000000000000042')");
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES "
                + "(UNHEX('00000000000000000000000000000022'),'Pessoa 2','11987654321',UNHEX('00000000000000000000000000000001'))");
        jdbc.update("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes,veiculo_agendamento_id) "
                + "SELECT UNHEX('00000000000000000000000000000043'),data,UNHEX('00000000000000000000000000000022'),lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "7200,0,0,0,'09:00',0,0,veiculo_agendamento_id FROM agendamentos WHERE id=UNHEX('00000000000000000000000000000042')");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2030-01-03',NOW())");
        jdbc.update("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes) "
                + "SELECT UNHEX('00000000000000000000000000000044'),'2030-01-03',pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "3600,0,0,0,'10:00',0,0 FROM agendamentos WHERE id=UNHEX('00000000000000000000000000000042')");
        mvc.perform(get("/api/relatorios/passageiros-periodo").contextPath("/api")
                .param("dataInicio", "2030-01-01").param("dataFim", "2030-01-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].horarioSaida").value("06:30:00"))
                .andExpect(jsonPath("$[1].horarioSaida").value("07:00:00"))
                .andExpect(jsonPath("$[2].horarioSaida").doesNotExist())
                .andExpect(jsonPath("$[3].horarioSaida").value("07:00:00"));
    }
}
