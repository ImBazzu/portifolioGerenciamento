package br.com.imbazzu.secretariaTransporte.lista.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.application.model.RegraCadastroInput;
import br.com.imbazzu.secretariaTransporte.lista.application.model.RegraSeparacaoInput;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoListaApplication {

    private final TipoListaPorta listaPorta;

    @Transactional
    public UUID criar(String nome, String descricao) {
        return listaPorta.salvar(TipoLista.criar(new Nome(nome), observacaoOpcional(descricao)));
    }

    @Transactional
    public void editar(UUID id, String nome, String descricao) {
        var lista = buscarObrigatorio(id);
        lista.editar(new Nome(nome), observacaoOpcional(descricao));
        listaPorta.salvar(lista);
    }

    @Transactional(readOnly = true)
    public TipoLista buscar(UUID id) {
        return buscarObrigatorio(id);
    }

    @Transactional(readOnly = true)
    public ResultadoPaginado<TipoListaResumo> listar(String nome, boolean arquivado, int pagina, int tamanho) {
        if (pagina < 0) {
            throw new DadosInvalidosException("A página não pode ser negativa");
        }
        if (tamanho < 1 || tamanho > 100) {
            throw new DadosInvalidosException("O tamanho da página deve estar entre 1 e 100");
        }
        return listaPorta.listar(nome == null ? "" : nome.trim(), arquivado, pagina, tamanho);
    }

    @Transactional
    public void atualizarRegras(UUID id, List<RegraCadastroInput> regrasCadastro,
            List<RegraSeparacaoInput> regrasSeparacao) {
        var lista = buscarObrigatorio(id);
        for (var regra : lista.getListaRegrasCadastro()) {
            lista.removerRegraCadastro(regra.getId());
        }
        for (var regra : lista.getListaRegraSeparacao()) {
            lista.removerRegraSeparacao(regra.getId());
        }
        regrasCadastro.forEach(regra -> lista.criarRegraCadastro(
                regra.idsVeiculos(),
                regra.idsCidades(),
                regra.prioridades(),
                regra.inicioHorario(),
                regra.finalHorario(),
                regra.obrigacaoAnexo(),
                observacaoOpcional(regra.descricao())));
        regrasSeparacao.forEach(regra -> lista.criarRegraSeparacao(
                regra.idTipoVeiculo(),
                regra.tempoTolerancia()));
        listaPorta.salvar(lista);
    }

    @Transactional
    public void arquivar(UUID id) {
        var lista = buscarObrigatorio(id);
        lista.arquivar();
        listaPorta.salvar(lista);
    }

    @Transactional
    public void desarquivar(UUID id) {
        var lista = buscarObrigatorio(id);
        lista.desarquivar();
        listaPorta.salvar(lista);
    }

    @Transactional
    public void deletar(UUID id) {
        buscarObrigatorio(id);
        listaPorta.deletar(id);
    }

    private TipoLista buscarObrigatorio(UUID id) {
        return listaPorta.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Lista não encontrada: " + id));
    }

    private static Observacao observacaoOpcional(String descricao) {
        return descricao == null || descricao.isBlank() ? null : new Observacao(descricao);
    }
}
