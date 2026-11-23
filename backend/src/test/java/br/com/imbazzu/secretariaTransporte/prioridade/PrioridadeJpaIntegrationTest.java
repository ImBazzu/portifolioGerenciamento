package br.com.imbazzu.secretariaTransporte.prioridade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.jpaAdapter.AgendamentoQueryJpaAdapter;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar.PessoaCommandEditar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar.PessoaCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar.EspecificacaoCommandSalvar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar.EspecificacaoCommandSalvarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar.EspecificacaoCommandEditar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar.EspecificacaoCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.repositorio.EspecificacaoRepository;
import br.com.imbazzu.secretariaTransporte.lista.adpter.model.RegraCadastroInput;
import br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras.TipoListaCommandAtualizarRegras;
import br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras.TipoListaCommandAtualizarRegrasHandler;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "api.security.token.secret=test-secret",
        "api.security.token.assinatura=test-signature",
        "api.security.origenspermitidas=http://localhost"
})
class PrioridadeJpaIntegrationTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private JdbcTemplate jdbc;
    @Autowired private PrioridadeRepository prioridades;
    @Autowired private AgendamentoQueryJpaAdapter agenda;
    @Autowired private PessoaCommandEditarHandler editarPessoa;
    @Autowired private PrioridadeController controller;
    @Autowired private EspecificacaoCommandSalvarHandler salvarEspecificacao;
    @Autowired private EspecificacaoCommandEditarHandler editarEspecificacao;
    @Autowired private EspecificacaoRepository especificacoes;
    @Autowired private TipoListaCommandAtualizarRegrasHandler atualizarRegras;

    @Test
    void carregaPrioridadeDaPessoaEVerificaRegraPorId() {
        var prioridadeId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var prioridade = prioridades.findById(prioridadeId).orElseThrow();
        prioridade.setQuantidadeAcompanhantes(2);
        prioridades.saveAndFlush(prioridade);

        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id,buscar_na_residencia) VALUES (UNHEX('00000000000000000000000000000005'),'Pessoa','11987654321',UNHEX('00000000000000000000000000000001'),1)");
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000003'),'Lista')");
        jdbc.update("INSERT INTO regras_cadastro (id,lista_id,obrigacao_anexo) VALUES (UNHEX('00000000000000000000000000000004'),UNHEX('00000000000000000000000000000003'),0)");
        jdbc.update("INSERT INTO regra_cadastro_prioridades (regra_cadastro_id,prioridade_id) VALUES (UNHEX('00000000000000000000000000000004'),UNHEX('00000000000000000000000000000002'))");
        jdbc.update("INSERT INTO regra_cadastro_dias_semana (regra_cadastro_id,dia_semana) VALUES "
                + "(UNHEX('00000000000000000000000000000004'),'SATURDAY'),"
                + "(UNHEX('00000000000000000000000000000004'),'SUNDAY')");

        var pessoa = agenda.buscarPessoa(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        assertEquals(prioridadeId, pessoa.prioridade());
        assertTrue(pessoa.buscarNaResidencia());
        assertEquals(2, pessoa.acompanhantes());
        var regras = agenda.listarRegrasCadastro(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        assertEquals(prioridadeId, regras.getFirst().prioridades().getFirst());
        assertEquals(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), regras.getFirst().diasSemana());
    }

    @Test
    void edicaoDaPessoaPreservaDadosAntigos() {
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id,buscar_na_residencia,quantidade_acompanhantes) VALUES (UNHEX('00000000000000000000000000000006'),'Pessoa Antiga','11987654321',UNHEX('00000000000000000000000000000001'),1,2)");
        editarPessoa.handler(new PessoaCommandEditar(UUID.fromString("00000000-0000-0000-0000-000000000006"),
                "Pessoa Editada", "", UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "", null, "11987654321", true));
        assertEquals(true, jdbc.queryForObject(
                "SELECT buscar_na_residencia FROM pessoas WHERE nome='Pessoa Editada'", Boolean.class));
        assertEquals(2, jdbc.queryForObject(
                "SELECT quantidade_acompanhantes FROM pessoas WHERE nome='Pessoa Editada'", Integer.class));
    }

    @Test
    void criaEAtualizaPrioridade() {
        var criada = controller.criar(new PrioridadeController.Request("Especial", 2)).getBody();
        var editada = controller.editar(criada.id(), new PrioridadeController.Request("Especial atualizada", 1));
        assertEquals("Especial atualizada", prioridades.findById(criada.id()).orElseThrow().getNome());
        assertEquals(1, editada.quantidadeAcompanhantes());
    }

    @Test
    void contaEspecificacoesVinculadasInclusiveArquivadas() {
        var primeira = controller.criar(new PrioridadeController.Request("Contagem primeira", 2)).getBody();
        var segunda = controller.criar(new PrioridadeController.Request("Contagem segunda", 0)).getBody();
        assertEquals(0, primeira.quantidadeEspecificacoes());
        var ativa = salvarEspecificacao.handler(new EspecificacaoCommandSalvar("Contagem ativa", primeira.id(), null));
        var arquivada = salvarEspecificacao.handler(new EspecificacaoCommandSalvar("Contagem arquivada", primeira.id(), null));
        var entidade = especificacoes.findById(arquivada).orElseThrow();
        entidade.setDataArquivado(LocalDate.now());
        especificacoes.saveAndFlush(entidade);

        assertEquals(2, controller.buscar(primeira.id()).quantidadeEspecificacoes());
        assertEquals(0, controller.buscar(segunda.id()).quantidadeEspecificacoes());
        assertEquals(2, controller.listar().stream().filter(p -> p.id().equals(primeira.id()))
                .findFirst().orElseThrow().quantidadeEspecificacoes());
        assertEquals(0, controller.listar().stream().filter(p -> p.id().equals(segunda.id()))
                .findFirst().orElseThrow().quantidadeEspecificacoes());

        editarEspecificacao.handler(new EspecificacaoCommandEditar(ativa, "Contagem ativa", segunda.id(), null));
        assertEquals(1, controller.buscar(primeira.id()).quantidadeEspecificacoes());
        assertEquals(1, controller.buscar(segunda.id()).quantidadeEspecificacoes());
        assertEquals(1, controller.editar(segunda.id(),
                new PrioridadeController.Request("Contagem segunda", 0)).quantidadeEspecificacoes());
    }

    @Test
    void arquivaReativaEExcluiPrioridadeSemVinculos() {
        var id = controller.criar(new PrioridadeController.Request("Sem vínculos", 0)).getBody().id();
        controller.arquivar(id);
        assertTrue(controller.buscar(id).arquivada());
        controller.desarquivar(id);
        assertFalse(controller.buscar(id).arquivada());
        controller.excluir(id);
        assertFalse(prioridades.existsById(id));
    }

    @Test
    void naoExcluiPrioridadeComEspecificacaoNemCriaNovoVinculoArquivado() {
        var id = controller.criar(new PrioridadeController.Request("Com especificação", 0)).getBody().id();
        var especificacaoId = salvarEspecificacao.handler(new EspecificacaoCommandSalvar("Especificação vinculada", id, null));
        jdbc.update("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES (UNHEX('0000000000000000000000000000000B'),'Pessoa vinculada','11987654321',UNHEX(?))",
                especificacaoId.toString().replace("-", ""));
        var pessoaId = UUID.fromString("00000000-0000-0000-0000-00000000000b");
        assertTrue(agenda.buscarPessoa(pessoaId).prioridadeAtiva());
        controller.arquivar(id);
        assertFalse(agenda.buscarPessoa(pessoaId).prioridadeAtiva());
        editarEspecificacao.handler(new EspecificacaoCommandEditar(especificacaoId, "Especificação editada", id, null));
        assertThrows(OperacaoNaoPermitidaException.class, () -> controller.excluir(id));
        assertThrows(OperacaoNaoPermitidaException.class, () -> salvarEspecificacao.handler(
                new EspecificacaoCommandSalvar("Outra especificação", id, null)));
        assertTrue(prioridades.existsById(id));
    }

    @Test
    void naoExcluiPrioridadeVinculadaSomenteARegra() {
        var id = controller.criar(new PrioridadeController.Request("Com regra", 0)).getBody().id();
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000008'),'Lista para exclusão')");
        jdbc.update("INSERT INTO regras_cadastro (id,lista_id,obrigacao_anexo) VALUES (UNHEX('00000000000000000000000000000009'),UNHEX('00000000000000000000000000000008'),0)");
        jdbc.update("INSERT INTO regra_cadastro_prioridades (regra_cadastro_id,prioridade_id) VALUES (UNHEX('00000000000000000000000000000009'),UNHEX(?))", id.toString().replace("-", ""));
        assertThrows(OperacaoNaoPermitidaException.class, () -> controller.excluir(id));
        assertTrue(prioridades.existsById(id));
    }

    @Test
    void vinculaPrioridadeNovaAEspecificacaoERegraDaLista() {
        var criada = controller.criar(new PrioridadeController.Request("Nova prioridade", 1)).getBody();
        var especificacaoId = salvarEspecificacao.handler(
                new EspecificacaoCommandSalvar("Nova especificação", criada.id(), null));
        assertEquals(criada.id(), especificacoes.findById(especificacaoId).orElseThrow().getPrioridade());

        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000007'),'Lista Nova')");
        var lista = UUID.fromString("00000000-0000-0000-0000-000000000007");
        var idCarro = UUID.fromString(jdbc.queryForObject(
                "SELECT BIN_TO_UUID(id) FROM tipo_veiculos WHERE nome='Carro'", String.class));
        var regra = new RegraCadastroInput(List.of(idCarro), List.of(), List.of(criada.id()),
                null, null, false, null, List.of(), 1);
        atualizarRegras.handler(new TipoListaCommandAtualizarRegras(lista, List.of(regra), List.of()));
        assertEquals(criada.id(), agenda.listarRegrasCadastro(lista).getFirst().prioridades().getFirst());
        controller.arquivar(criada.id());
        atualizarRegras.handler(new TipoListaCommandAtualizarRegras(lista, List.of(regra), List.of()));
        assertEquals(criada.id(), agenda.listarRegrasCadastro(lista).getFirst().prioridades().getFirst());
        jdbc.update("INSERT INTO listas (id,nome) VALUES (UNHEX('0000000000000000000000000000000A'),'Lista sem prioridade')");
        var outraLista = UUID.fromString("00000000-0000-0000-0000-00000000000a");
        assertThrows(OperacaoNaoPermitidaException.class,
                () -> atualizarRegras.handler(new TipoListaCommandAtualizarRegras(outraLista, List.of(regra), List.of())));
    }
}
