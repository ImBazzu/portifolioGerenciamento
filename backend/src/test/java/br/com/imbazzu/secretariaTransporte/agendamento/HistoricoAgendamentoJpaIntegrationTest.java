package br.com.imbazzu.secretariaTransporte.agendamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.DestinoRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.AgendamentoQueryRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoQueryRepository;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.VeiculoQueryRepository;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "api.security.token.secret=test-secret",
        "api.security.token.assinatura=test-signature",
        "api.security.origenspermitidas=http://localhost"
})
class HistoricoAgendamentoJpaIntegrationTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private JdbcTemplate jdbc;
    @Autowired private DestinoRepository destinos;
    @Autowired private VeiculoQueryRepository veiculos;
    @Autowired private TipoVeiculoQueryRepository tipos;
    @Autowired private AgendamentoQueryRepository agendamentos;

    @Test
    @Transactional
    void historicosEValidacaoDeUsoConsideramAgendamentosEVeiculosDaAgenda() {
        jdbc.update("INSERT INTO cidades (id,nome,estado,tempo_viagem) VALUES (UNHEX('00000000000000000000000000000011'),'Cidade teste','SP',0)");
        jdbc.update("INSERT INTO destinos (id,nome,cidade_id) VALUES (UNHEX('00000000000000000000000000000012'),'Destino teste',UNHEX('00000000000000000000000000000011'))");
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000013'),'Lista teste')");
        jdbc.update("INSERT INTO tipo_veiculos (id,nome,capacidade) VALUES "
                + "(UNHEX('00000000000000000000000000000014'),'Tipo teste',10),"
                + "(UNHEX('00000000000000000000000000000015'),'Tipo pendente',10),"
                + "(UNHEX('00000000000000000000000000000016'),'Tipo livre',10)");
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES "
                + "(UNHEX('00000000000000000000000000000021'),'Pessoa 1','11987654321',UNHEX('00000000000000000000000000000001')),"
                + "(UNHEX('00000000000000000000000000000022'),'Pessoa 2','11987654321',UNHEX('00000000000000000000000000000001')),"
                + "(UNHEX('00000000000000000000000000000023'),'Pessoa 3','11987654321',UNHEX('00000000000000000000000000000001'))");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2030-01-01',NOW()),('2030-01-02',NOW())");
        jdbc.update("INSERT INTO veiculos_agendamento (id,data,tipo_veiculo_id,nome_tipo_veiculo,capacidade,hora_saida_calculada) VALUES "
                + "(UNHEX('00000000000000000000000000000031'),'2030-01-01',UNHEX('00000000000000000000000000000014'),'Tipo teste',10,0),"
                + "(UNHEX('00000000000000000000000000000032'),'2030-01-01',UNHEX('00000000000000000000000000000014'),'Tipo teste',10,0),"
                + "(UNHEX('00000000000000000000000000000033'),'2030-01-02',UNHEX('00000000000000000000000000000014'),'Tipo teste',10,0)");
        jdbc.update("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes,veiculo_agendamento_id) VALUES "
                + "(UNHEX('00000000000000000000000000000041'),'2030-01-01',UNHEX('00000000000000000000000000000021'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000014'),UNHEX('00000000000000000000000000000012'),0,0,0,0,'10:00',0,2,UNHEX('00000000000000000000000000000031')),"
                + "(UNHEX('00000000000000000000000000000042'),'2030-01-01',UNHEX('00000000000000000000000000000022'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000014'),UNHEX('00000000000000000000000000000012'),0,0,0,0,'10:00',0,0,UNHEX('00000000000000000000000000000031')),"
                + "(UNHEX('00000000000000000000000000000043'),'2030-01-02',UNHEX('00000000000000000000000000000023'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000015'),UNHEX('00000000000000000000000000000012'),0,0,0,0,'10:00',0,0,NULL)");

        var destino = UUID.fromString("00000000-0000-0000-0000-000000000012");
        var cidade = UUID.fromString("00000000-0000-0000-0000-000000000011");
        var tipo = UUID.fromString("00000000-0000-0000-0000-000000000014");
        var pagina = PageRequest.of(0, 1);
        assertEquals(3, destinos.buscarPorId(destino).orElseThrow().getQtdPassageiros());
        assertEquals(3, destinos.listarPorCidade(cidade, "", pagina).getContent().getFirst().getQtdPassageiros());
        var historicoDestino = destinos.buscarHistorico(destino, pagina);
        assertEquals(2, historicoDestino.getTotalElements());
        assertEquals(LocalDate.of(2030, 1, 2), historicoDestino.getContent().getFirst().getData());
        assertEquals(2, destinos.buscarHistorico(destino, PageRequest.of(1, 1)).getContent().getFirst().getQuantidade());

        var resumo = veiculos.buscarResumo(tipo, pagina);
        assertEquals(2, resumo.getTotalElements());
        assertEquals(1L, resumo.getContent().getFirst().getQtdVeiculo());
        assertEquals(0L, resumo.getContent().getFirst().getTotalPassageiros());
        var primeiroDia = veiculos.buscarResumo(tipo, PageRequest.of(1, 1)).getContent().getFirst();
        assertEquals(2L, primeiroDia.getQtdVeiculo());
        assertEquals(4L, primeiroDia.getTotalPassageiros());
        var cidades = veiculos.buscarDestinosPorDatas(tipo, List.of(LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 2)));
        assertEquals(1, cidades.size());
        assertEquals("Cidade teste", cidades.getFirst().getCidade());
        assertTrue(tipos.existeVeiculoUsandoTipoVeiculo(tipo));
        assertTrue(tipos.existeVeiculoUsandoTipoVeiculo(UUID.fromString("00000000-0000-0000-0000-000000000015")));
        assertFalse(tipos.existeVeiculoUsandoTipoVeiculo(UUID.fromString("00000000-0000-0000-0000-000000000016")));

        jdbc.update("UPDATE destinos SET data_arquivado='2030-01-03' WHERE id=UNHEX('00000000000000000000000000000012')");
        assertEquals(3, destinos.buscarPorIdArquivado(destino).orElseThrow().getQtdViagens());
        assertEquals(3, destinos.listarPorCidadeArquivado(cidade, "", pagina).getContent().getFirst().getQtdViagens());

        jdbc.update("UPDATE agendamentos SET tempo_viagem_segundos=7200, tempo_viagem_nanos=500000000, buscar_na_residencia=1 WHERE id=UNHEX('00000000000000000000000000000041')");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2030-01-03',NOW())");
        jdbc.update("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes) "
                + "SELECT UNHEX('00000000000000000000000000000044'),'2030-01-03',pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes "
                + "FROM agendamentos WHERE id=UNHEX('00000000000000000000000000000041')");
        var pessoa = UUID.fromString("00000000-0000-0000-0000-000000000021");
        var historicoPessoa = agendamentos.buscarHistoricoViagensPorPessoa(pessoa, pagina);
        assertEquals(2, historicoPessoa.getTotalElements());
        assertEquals(2, historicoPessoa.getTotalPages());
        var viagem = historicoPessoa.getContent().getFirst();
        assertEquals(LocalDate.of(2030, 1, 3), viagem.getData());
        assertEquals(LocalTime.of(7, 59, 59, 500_000_000), viagem.getHorarioSaidaNecessario());
        assertEquals("Destino teste", viagem.getDestinoNome());
        assertEquals("Tipo teste", viagem.getTipoVeiculoNome());
        assertTrue(viagem.isBuscarNaResidencia());
        assertEquals(2, viagem.getAcompanhantes());
        assertEquals(LocalDate.of(2030, 1, 1), agendamentos.buscarHistoricoViagensPorPessoa(pessoa, PageRequest.of(1, 1)).getContent().getFirst().getData());
        assertEquals(2, agendamentos.buscarHistoricoViagensPorPessoaComBusca(pessoa, "Destino", pagina).getTotalElements());
        assertEquals(0, agendamentos.buscarHistoricoViagensPorPessoaComBusca(pessoa, "inexistente", pagina).getTotalElements());
    }
}
