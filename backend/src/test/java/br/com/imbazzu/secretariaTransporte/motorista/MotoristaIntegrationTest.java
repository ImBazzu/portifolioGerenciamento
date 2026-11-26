package br.com.imbazzu.secretariaTransporte.motorista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.jpaAdapter.AgendamentoCommandJpaAdapter;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "api.security.token.secret=test-secret",
        "api.security.token.assinatura=test-signature",
        "api.security.origenspermitidas=http://localhost"
})
@AutoConfigureMockMvc(addFilters = false)
class MotoristaIntegrationTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");
    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private MotoristaService service;
    @Autowired private AgendamentoCommandJpaAdapter comandos;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc mvc;

    @Test
    @Transactional
    void filtraPorNomeECategoriaEAceitaMesmoMotoristaEmVeiculosDoDia() {
        var a = service.salvarCategoria(null, "Plantão A");
        var b = service.salvarCategoria(null, "Plantão B");
        var motorista = service.salvar(null, "João Silva", "(11) 98765-4321", a.id());
        service.salvar(null, "João Souza", "11987654322", b.id());
        assertEquals(1, service.buscar("joão", a.id(), 0).getTotalElements());
        assertEquals("João Silva", service.buscar("silva", a.id(), 0).getContent().getFirst().nome());
        assertEquals(0, service.buscar("silva", b.id(), 0).getTotalElements());
        service.salvar(motorista.id(), "João Silva", "11987654321", b.id());
        assertEquals(1, service.buscar("silva", b.id(), 0).getTotalElements());

        jdbc.update("INSERT INTO dias_agendamento (data, aberto_em) VALUES ('2031-01-01', NOW())");
        jdbc.update("INSERT INTO tipo_veiculos (id,nome,capacidade) VALUES (UNHEX('00000000000000000000000000000099'),'Teste motorista',4)");
        jdbc.update("INSERT INTO veiculos_agendamento (id,data,tipo_veiculo_id,nome_tipo_veiculo,capacidade,hora_saida_calculada) VALUES "
                + "(UNHEX('00000000000000000000000000000091'),'2031-01-01',UNHEX('00000000000000000000000000000099'),'Teste',4,0),"
                + "(UNHEX('00000000000000000000000000000092'),'2031-01-01',UNHEX('00000000000000000000000000000099'),'Teste',4,0)");
        var primeiro = UUID.fromString("00000000-0000-0000-0000-000000000091");
        var segundo = UUID.fromString("00000000-0000-0000-0000-000000000092");
        var data = LocalDate.of(2031, 1, 1);
        service.atribuir(primeiro, motorista.id());
        service.atribuir(segundo, motorista.id());
        assertEquals(2, service.atribuicoes(data).size());
        comandos.salvar(new VeiculoAberto(primeiro, data, UUID.fromString("00000000-0000-0000-0000-000000000099"),
                "Teste", 4, "Nova identificação", null, false));
        assertEquals(2, service.atribuicoes(data).size());
        service.atribuir(primeiro, null);
        assertEquals(segundo, service.atribuicoes(data).getFirst().veiculoId());
        assertThrows(EntidadeNaoEncontradoException.class, () -> service.atribuir(segundo, UUID.randomUUID()));
    }

    @Test
    @Transactional
    void impedeTelefoneDuplicadoMesmoComFormatacaoDiferente() {
        var categoria = service.salvarCategoria(null, "Plantão único");
        service.salvar(null, "Ana", "(11) 98765-4321", categoria.id());
        var erro = assertThrows(EntidadeDuplicadaException.class,
                () -> service.salvar(null, "Outra pessoa", "11987654321", categoria.id()));
        assertTrue(erro.getMessage().contains("telefone"));
    }

    @Test
    @Transactional
    void apiRetornaPaginaEstavelERejeitaTelefoneInvalidoSemConfundirComAutenticacao() throws Exception {
        var categoria = service.salvarCategoria(null, "Plantão teste API");
        service.salvar(null, "Ana API", "11987654321", categoria.id());
        mvc.perform(get("/motoristas").param("nome", "Ana API").param("categoria", categoria.id().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"content\"")))
                .andExpect(content().string(containsString("\"totalPages\":1")))
                .andExpect(content().string(containsString("\"number\":0")));
        mvc.perform(post("/motoristas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Ana\",\"telefone\":\"123\",\"categoriaId\":\"" + categoria.id() + "\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("telefone")));
    }
}
