package br.com.imbazzu.secretariaTransporte.seguranca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.application.GerenciarUsuariosService;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.UsuarioRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AtualizarTokenUseCase;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "api.security.token.secret=test-secret",
        "api.security.token.assinatura=test-signature",
        "api.security.origenspermitidas=http://localhost"
})
@AutoConfigureMockMvc
class SegurancaIntegrationTest {
    @Container private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");
    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private MockMvc mvc;
    @Autowired private GerenciarUsuariosService service;
    @Autowired private UsuarioRepositorioPorta usuarios;
    @Autowired private TokenPorta tokens;
    @Autowired private AtualizarTokenUseCase atualizarToken;
    @Autowired private JdbcTemplate jdbc;

    @Test
    @Transactional
    void aplicaPerfisCumulativosNaApiERelePerfilAtualDoBanco() throws Exception {
        var agendamento = service.criar("agendamento teste", "senha123", "AGENDAMENTO");
        var motorista = service.criar("motorista teste", "senha123", "MOTORISTA");
        var gestao = service.criar("gestao teste", "senha123", "GESTAO");
        var admin = usuarios.buscarPorNome("admin").orElseThrow();
        String a = token(agendamento.id());
        String m = token(motorista.id());
        String g = token(gestao.id());
        String adm = tokens.gerarTokenAcesso(admin);
        mvc.perform(get("/health")).andExpect(status().is2xxSuccessful());
        mvc.perform(get("/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/registrarAdm").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"novo admin\",\"senha\":\"senha123\"}")).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"motorista teste\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"tokenAcesso\"")));
        mvc.perform(get("/cidade").header("Authorization", "Bearer " + a)).andExpect(status().is2xxSuccessful());
        mvc.perform(post("/destino").header("Authorization", "Bearer " + a)
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        mvc.perform(put("/destino/{id}", UUID.randomUUID()).header("Authorization", "Bearer " + a)
                .param("nome", "Destino")).andExpect(status().isForbidden());
        mvc.perform(get("/motoristas").header("Authorization", "Bearer " + a)).andExpect(status().isForbidden());
        mvc.perform(get("/motoristas").header("Authorization", "Bearer " + m)).andExpect(status().isOk());
        mvc.perform(get("/relatorios/investigacao/pessoas").header("Authorization", "Bearer " + a)).andExpect(status().isForbidden());
        mvc.perform(get("/relatorios/investigacao/motoristas").header("Authorization", "Bearer " + m)).andExpect(status().isForbidden());
        mvc.perform(get("/relatorios/investigacao/pessoas").header("Authorization", "Bearer " + g)).andExpect(status().isOk());
        mvc.perform(get("/pessoa/listar").header("Authorization", "Bearer " + m)).andExpect(status().is2xxSuccessful());
        mvc.perform(patch("/pessoa/{id}/validar", UUID.randomUUID()).header("Authorization", "Bearer " + m))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/pessoa/{id}/validar", UUID.randomUUID()).header("Authorization", "Bearer " + g))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/pessoa/{id}/retirar-validacao", UUID.randomUUID()).header("Authorization", "Bearer " + a))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/pessoa/{id}/retirar-validacao", UUID.randomUUID()).header("Authorization", "Bearer " + m))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/pessoa/{id}/retirar-validacao", UUID.randomUUID()).header("Authorization", "Bearer " + g))
                .andExpect(status().isNotFound());
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + g)).andExpect(status().isForbidden());
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + adm)).andExpect(status().isOk());
        mvc.perform(post("/usuarios").header("Authorization", "Bearer " + a).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"novo usuario\",\"senha\":\"senha123\",\"perfil\":\"ADMIN\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/usuarios").header("Authorization", "Bearer " + adm).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"novo usuario\",\"senha\":\"senha123\",\"perfil\":\"MOTORISTA\"}"))
                .andExpect(status().isOk());
        mvc.perform(put("/usuarios/{id}/perfil", motorista.id()).header("Authorization", "Bearer " + adm)
                .contentType(MediaType.APPLICATION_JSON).content("{\"perfil\":\"AGENDAMENTO\"}"))
                .andExpect(status().isOk());
        mvc.perform(get("/motoristas").header("Authorization", "Bearer " + m)).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void gestaoConfiguraEConsultaListasDoUsuario() throws Exception {
        var operador = service.criar("operador listas", "senha123", "AGENDAMENTO");
        var gestor = service.criar("gestor listas", "senha123", "GESTAO");
        var lista = UUID.randomUUID();
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UUID_TO_BIN(?),?)", lista.toString(), "Emergência teste");
        String g = token(gestor.id());
        String a = token(operador.id());

        mvc.perform(get("/usuarios/listas-agendamento").header("Authorization", "Bearer " + g))
                .andExpect(status().isOk()).andExpect(content().string(containsString(operador.id().toString())));
        mvc.perform(put("/usuarios/{id}/listas-agendamento", operador.id())
                        .header("Authorization", "Bearer " + a).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restrita\":true,\"idsListas\":[\"" + lista + "\"]}"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/usuarios/{id}/listas-agendamento", operador.id())
                        .header("Authorization", "Bearer " + g).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restrita\":true,\"idsListas\":[\"" + lista + "\"]}"))
                .andExpect(status().isOk());
        mvc.perform(get("/usuarios/eu/listas-agendamento").header("Authorization", "Bearer " + a))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"restrita\":true")))
                .andExpect(content().string(containsString(lista.toString())));
    }

    @Test
    @Transactional
    void abreCarrosDeEmergenciaEAltaComSaidaFixa() throws Exception {
        var operador = service.criar("operador carros especiais", "senha123", "AGENDAMENTO");
        String acesso = token(operador.id());
        var lista = UUID.randomUUID();
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UUID_TO_BIN(?),?)", lista.toString(), "Emergência teste");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2032-01-03',NOW())");
        var tipo = jdbc.queryForObject("SELECT BIN_TO_UUID(id) FROM tipo_veiculos WHERE nome='Carro' LIMIT 1", String.class);
        for (String finalidade : new String[] {"EMERGENCIA", "ALTA"}) {
            mvc.perform(post("/agendamentos/dias/2032-01-03/veiculos")
                            .header("Authorization", "Bearer " + acesso).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"idTipoVeiculo\":\"" + tipo + "\",\"horarioSaida\":\"20:00\","
                                    + "\"finalidadeAbertura\":\"" + finalidade + "\",\"idListaAbertura\":\"" + lista + "\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(content().string(containsString("\"finalidadeAbertura\":\"" + finalidade + "\"")))
                    .andExpect(content().string(containsString("\"horarioSaida\":\"20:00:00\"")));
        }

        mvc.perform(get("/agendamentos/dias/2032-01-03").header("Authorization", "Bearer " + acesso))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"finalidadeAbertura\":\"ALTA\"")))
                .andExpect(content().string(containsString("\"finalidadeAbertura\":\"EMERGENCIA\"")));
    }

    @Test
    @Transactional
    void confirmaDesmarcaEConfirmaNovamenteSaidaDoVeiculo() throws Exception {
        var operador = service.criar("operador saida", "senha123", "AGENDAMENTO");
        var acesso = token(operador.id());
        var id = UUID.randomUUID().toString();
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2032-01-05',NOW())");
        jdbc.update("""
                INSERT INTO veiculos_agendamento
                (id,data,tipo_veiculo_id,nome_tipo_veiculo,capacidade,hora_saida,hora_saida_calculada)
                SELECT UUID_TO_BIN(?),'2032-01-05',id,nome,capacidade,'08:00',0
                FROM tipo_veiculos WHERE nome='Carro'
                """, id);

        mvc.perform(post("/agendamentos/veiculos/{id}/confirmar-saida", id)
                .header("Authorization", "Bearer " + acesso)).andExpect(status().isNoContent());
        assertEquals(1, jdbc.queryForObject("""
                SELECT COUNT(*) FROM veiculos_agendamento WHERE id=UUID_TO_BIN(?)
                AND saida_confirmada_em IS NOT NULL AND saida_confirmada_por_id=UUID_TO_BIN(?)
                """, Integer.class, id, operador.id().toString()));
        mvc.perform(delete("/agendamentos/veiculos/{id}/confirmar-saida", id)
                .header("Authorization", "Bearer " + acesso)).andExpect(status().isNoContent());
        assertEquals(1, jdbc.queryForObject("""
                SELECT COUNT(*) FROM veiculos_agendamento WHERE id=UUID_TO_BIN(?)
                AND saida_confirmada_em IS NULL AND saida_confirmada_por_id IS NULL
                """, Integer.class, id));
        mvc.perform(delete("/agendamentos/veiculos/{id}/confirmar-saida", id)
                .header("Authorization", "Bearer " + acesso)).andExpect(status().isConflict());
        mvc.perform(post("/agendamentos/veiculos/{id}/confirmar-saida", id)
                .header("Authorization", "Bearer " + acesso)).andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void migracaoCriaListasEspeciaisComRegrasIniciais() throws Exception {
        var operador = service.criar("operador listas especiais", "senha123", "AGENDAMENTO");
        var acesso = token(operador.id());
        var tipo = jdbc.queryForObject("SELECT BIN_TO_UUID(id) FROM tipo_veiculos WHERE nome='Carro'", String.class);
        jdbc.update("INSERT INTO cidades (id,nome,estado,tempo_viagem) VALUES (UNHEX('000000000000000000000000000000b1'),'Cidade especial','SP',0)");
        jdbc.update("INSERT INTO destinos (id,nome,cidade_id) VALUES (UNHEX('000000000000000000000000000000b2'),'Destino especial',UNHEX('000000000000000000000000000000b1'))");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2032-01-04',NOW())");
        for (String finalidade : new String[] {"ALTA", "EMERGENCIA"}) {
            var id = jdbc.queryForObject("SELECT BIN_TO_UUID(id) FROM listas WHERE finalidade_especial = ?",
                    String.class, finalidade);
            assertEquals(1, jdbc.queryForObject(
                    "SELECT COUNT(*) FROM regras_cadastro WHERE lista_id=UUID_TO_BIN(?)", Integer.class, id));
            assertTrue(jdbc.queryForObject("""
                    SELECT COUNT(*) FROM regra_cadastro_prioridades p
                    JOIN regras_cadastro r ON r.id=p.regra_cadastro_id WHERE r.lista_id=UUID_TO_BIN(?)
                    """, Integer.class, id) > 0);
            assertTrue(jdbc.queryForObject("""
                    SELECT COUNT(*) FROM regra_cadastro_veiculos v
                    JOIN regras_cadastro r ON r.id=v.regra_cadastro_id
                    JOIN tipo_veiculos t ON t.id=v.veiculo_id
                    WHERE r.lista_id=UUID_TO_BIN(?) AND t.nome='Carro'
                    """, Integer.class, id) > 0);
            assertEquals(1, jdbc.queryForObject(
                    "SELECT COUNT(*) FROM regras_separacao WHERE lista_id=UUID_TO_BIN(?)", Integer.class, id));
            mvc.perform(get("/lista/{id}", id).header("Authorization", "Bearer " + acesso))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"finalidadeEspecial\":\"" + finalidade + "\"")));
            var pessoa = UUID.randomUUID().toString();
            jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES (UUID_TO_BIN(?),?,?,UNHEX('00000000000000000000000000000001'))",
                    pessoa, "Pessoa " + finalidade, "11987654321");
            mvc.perform(post("/agendamentos/dias/2032-01-04/verificar-veiculos")
                            .header("Authorization", "Bearer " + acesso).contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"idPessoa":"%s","idLista":"%s","idDestino":"00000000-0000-0000-0000-0000000000b2",
                                     "horarioChegada":"20:00","informarHorarioSaida":true,"acompanhantes":0}
                                    """.formatted(pessoa, id)))
                    .andExpect(status().isOk()).andExpect(content().string(containsString(tipo)));
            mvc.perform(post("/agendamentos/dias/2032-01-04/passageiros")
                            .header("Authorization", "Bearer " + acesso).contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"idPessoa":"%s","idLista":"%s","idTipoVeiculo":"%s",
                                     "idDestino":"00000000-0000-0000-0000-0000000000b2",
                                     "horarioChegada":"20:00","informarHorarioSaida":true,"buscarNaResidencia":false,"acompanhantes":0}
                                    """.formatted(pessoa, id, tipo)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    @Transactional
    void protegeDadosValidadosEFiltraPorVerificacao() throws Exception {
        var operador = service.criar("operador validacao", "senha123", "MOTORISTA");
        var gestao = service.criar("gestor validacao", "senha123", "GESTAO");
        String m = token(operador.id());
        String g = token(gestao.id());
        String pessoaJson = """
                {"nome":"Pessoa verificada","especificacaoId":"00000000-0000-0000-0000-000000000001",
                 "telefone":"11987654321","buscarNaResidencia":false}
                """;
        var location = mvc.perform(post("/pessoa").header("Authorization", "Bearer " + m)
                .contentType(MediaType.APPLICATION_JSON).content(pessoaJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        UUID id = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
        mvc.perform(patch("/pessoa/{id}/validar", id).header("Authorization", "Bearer " + g))
                .andExpect(status().isNoContent());
        mvc.perform(get("/pessoa/listar").param("validada", "true").param("nome", "Pessoa verificada")
                .header("Authorization", "Bearer " + m)).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"validada\":true")));
        mvc.perform(get("/pessoa/listar").param("validada", "false").param("nome", "Pessoa verificada")
                .header("Authorization", "Bearer " + m)).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElementos\":0")));
        mvc.perform(put("/pessoa/{id}", id).header("Authorization", "Bearer " + m)
                .contentType(MediaType.APPLICATION_JSON).content(pessoaJson))
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(put("/pessoa/{id}", id).header("Authorization", "Bearer " + g)
                .contentType(MediaType.APPLICATION_JSON).content(pessoaJson))
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(patch("/pessoa/{id}/retirar-validacao", id).header("Authorization", "Bearer " + m))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/pessoa/{id}/retirar-validacao", id).header("Authorization", "Bearer " + g))
                .andExpect(status().isNoContent());
        mvc.perform(put("/pessoa/{id}", id).header("Authorization", "Bearer " + m)
                .contentType(MediaType.APPLICATION_JSON).content(pessoaJson)).andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void trocaDeSenhaRevogaTokensAntigosEMantemIdentidadeAoRenomear() throws Exception {
        var criado = service.criar("nome inicial", "senha123", "AGENDAMENTO");
        var original = usuarios.buscarPorId(criado.id()).orElseThrow();
        String acesso = tokens.gerarTokenAcesso(original);
        String refresh = tokens.gerarTokenAtualizacao(original);
        var atual = service.atualizarProprio(criado.id(), "nome atualizado", "senha123", "senha nova 123");
        assertEquals(criado.id(), tokens.extrairDadosDoTokenAcesso(atual.tokenAcesso()).id());
        assertEquals("nome atualizado", usuarios.buscarPorId(criado.id()).orElseThrow().getNome().valor());
        mvc.perform(get("/usuarios/eu").header("Authorization", "Bearer " + acesso)).andExpect(status().isUnauthorized());
        mvc.perform(get("/usuarios/eu").header("Authorization", "Bearer " + atual.tokenAcesso())).andExpect(status().isOk());
        mvc.perform(post("/auth/atualizarToken").contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh + "\"}")).andExpect(status().isUnauthorized());
        assertEquals(criado.id(), tokens.extrairDadosDoTokenAcesso(
                atualizarToken.atualizarToken(atual.tokenAtualizar()).tokenAcesso()).id());
    }

    @Test
    @Transactional
    void preservaUltimoAdministrador() {
        var admin = usuarios.buscarPorNome("admin").orElseThrow();
        assertThrows(RegraDeNegocioException.class, () -> service.alterarPerfil(admin.getId(), "GESTAO"));
    }

    @Test
    @Transactional
    void arquivamentoRevogaTokensPreservaVinculosEProtegeAdmin() throws Exception {
        var admin = usuarios.buscarPorNome("admin").orElseThrow();
        var criado = service.criar("usuario arquivavel", "senha123", "AGENDAMENTO");
        var usuario = usuarios.buscarPorId(criado.id()).orElseThrow();
        String acesso = tokens.gerarTokenAcesso(usuario);
        String refresh = tokens.gerarTokenAtualizacao(usuario);
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id,autor_id) "
                + "VALUES (UNHEX('000000000000000000000000000000c1'),'Pessoa com autor','11987654321',"
                + "UNHEX('00000000000000000000000000000001'),UUID_TO_BIN(?))", criado.id().toString());
        assertThrows(RegraDeNegocioException.class, () -> service.excluir(criado.id(), admin.getId()));
        assertThrows(RegraDeNegocioException.class, () -> service.alterarArquivamento(admin.getId(), criado.id(), true));
        service.alterarArquivamento(criado.id(), admin.getId(), true);
        assertEquals(1, service.listar("usuario arquivavel", true, 0).getTotalElements());
        mvc.perform(get("/usuarios/eu").header("Authorization", "Bearer " + acesso)).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"usuario arquivavel\",\"senha\":\"senha123\"}"))
                .andExpect(status().isUnauthorized());
        service.alterarArquivamento(criado.id(), admin.getId(), false);
        mvc.perform(get("/usuarios/eu").header("Authorization", "Bearer " + acesso)).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/atualizarToken").contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh + "\"}")).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"usuario arquivavel\",\"senha\":\"senha123\"}")).andExpect(status().isOk());
    }

    @Test
    @Transactional
    void permiteExcluirUsuarioSemVinculos() {
        var admin = usuarios.buscarPorNome("admin").orElseThrow();
        var criado = service.criar("usuario sem vinculo", "senha123", "AGENDAMENTO");
        service.excluir(criado.id(), admin.getId());
        assertTrue(usuarios.buscarPorId(criado.id()).isEmpty());
    }

    @Test
    @Transactional
    void identificaAutoresNosCadastrosEPreservaAutoriaNasAlteracoes() throws Exception {
        var primeiro = service.criar("autor primeiro", "senha123", "AGENDAMENTO");
        var segundo = service.criar("autor segundo", "senha123", "AGENDAMENTO");
        String a = token(primeiro.id());
        String b = token(segundo.id());
        String pessoaJson = """
                {"nome":"Pessoa teste","especificacaoId":"00000000-0000-0000-0000-000000000001",
                 "telefone":"11987654321","buscarNaResidencia":false}
                """;
        var pessoaLocation = mvc.perform(post("/pessoa").header("Authorization", "Bearer " + a)
                .contentType(MediaType.APPLICATION_JSON).content(pessoaJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        UUID pessoaId = UUID.fromString(pessoaLocation.substring(pessoaLocation.lastIndexOf('/') + 1));
        mvc.perform(put("/pessoa/{id}", pessoaId).header("Authorization", "Bearer " + b)
                .contentType(MediaType.APPLICATION_JSON).content(pessoaJson)).andExpect(status().isNoContent());
        mvc.perform(get("/pessoa/{id}", pessoaId).header("Authorization", "Bearer " + b))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"autorNome\":\"autor primeiro\"")));

        jdbc.update("INSERT INTO cidades (id,nome,estado,tempo_viagem) VALUES (UNHEX('000000000000000000000000000000a1'),'Cidade autoria','SP',0)");
        jdbc.update("INSERT INTO destinos (id,nome,cidade_id) VALUES (UNHEX('000000000000000000000000000000a2'),'Destino autoria',UNHEX('000000000000000000000000000000a1'))");
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('000000000000000000000000000000a3'),'Lista autoria')");
        jdbc.update("INSERT INTO tipo_veiculos (id,nome,capacidade) VALUES (UNHEX('000000000000000000000000000000a4'),'Van autoria',8)");
        jdbc.update("INSERT INTO regras_cadastro (id,lista_id,obrigacao_anexo) VALUES (UNHEX('000000000000000000000000000000a5'),UNHEX('000000000000000000000000000000a3'),0)");
        jdbc.update("INSERT INTO regra_cadastro_prioridades (regra_cadastro_id,prioridade_id) SELECT UNHEX('000000000000000000000000000000a5'),prioridade_id FROM especificacoes WHERE id=UNHEX('00000000000000000000000000000001')");
        jdbc.update("INSERT INTO regra_cadastro_veiculos (regra_cadastro_id,veiculo_id) VALUES (UNHEX('000000000000000000000000000000a5'),UNHEX('000000000000000000000000000000a4'))");
        jdbc.update("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2031-01-01',NOW())");
        jdbc.update("INSERT INTO veiculos_agendamento (id,data,tipo_veiculo_id,nome_tipo_veiculo,capacidade,hora_saida,hora_saida_calculada) VALUES "
                + "(UNHEX('000000000000000000000000000000a6'),'2031-01-01',UNHEX('000000000000000000000000000000a4'),'Van autoria',8,'08:00',0)");
        String agendaJson = """
                {"idPessoa":"%s","idLista":"00000000-0000-0000-0000-0000000000a3",
                 "idTipoVeiculo":"00000000-0000-0000-0000-0000000000a4",
                 "idDestino":"00000000-0000-0000-0000-0000000000a2","horarioChegada":"10:00",
                 "buscarNaResidencia":false,"acompanhantes":0,
                 "idVeiculo":"00000000-0000-0000-0000-0000000000a6"}
                """.formatted(pessoaId);
        var agendaLocation = mvc.perform(post("/agendamentos/dias/2031-01-01/passageiros")
                .header("Authorization", "Bearer " + b).contentType(MediaType.APPLICATION_JSON).content(agendaJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        UUID agendaId = UUID.fromString(agendaLocation.substring(agendaLocation.lastIndexOf('/') + 1));
        mvc.perform(get("/agendamentos/dias/2031-01-01").header("Authorization", "Bearer " + a))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"autorNome\":\"autor segundo\"")));
        mvc.perform(put("/agendamentos/{id}", agendaId).header("Authorization", "Bearer " + a)
                .contentType(MediaType.APPLICATION_JSON).content("""
                {"idTipoVeiculo":"00000000-0000-0000-0000-0000000000a4",
                 "idDestino":"00000000-0000-0000-0000-0000000000a2","horarioChegada":"10:00",
                 "buscarNaResidencia":false,"acompanhantes":0,"idVeiculo":"00000000-0000-0000-0000-0000000000a6"}
                """)).andExpect(status().isNoContent());
        service.atualizarProprio(segundo.id(), "autor renomeado", "senha123", null);
        mvc.perform(get("/agendamentos/{id}", agendaId).header("Authorization", "Bearer " + a))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"autorNome\":\"autor renomeado\"")));
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES "
                + "(UNHEX('000000000000000000000000000000a7'),'Pessoa antiga','11987654321',UNHEX('00000000000000000000000000000001'))");
        mvc.perform(get("/pessoa/00000000-0000-0000-0000-0000000000a7").header("Authorization", "Bearer " + a))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"autorNome\":null")));
    }

    private String token(UUID id) {
        return tokens.gerarTokenAcesso(usuarios.buscarPorId(id).orElseThrow());
    }
}
