package br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.RegraCadastroJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.RegraSeparacaoJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaResumoProjecao;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.RegraCadastro;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.RegraSeparacao;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;

public final class TipoListaBancoMapper {

    private TipoListaBancoMapper() {
    }

    public static TipoLista paraDomain(TipoListaJpa entidade) {
        var regrasCadastro = entidade.getRegrasCadastro().stream()
                .map(regra -> RegraCadastro.recriar(
                        regra.getId(),
                        entidade.getId(),
                        regra.getIdsVeiculos(),
                        regra.getIdsCidades(),
                        regra.getPrioridades(),
                        regra.isObrigacaoAnexo(),
                        observacaoOpcional(regra.getDescricao()),
                        regra.getInicioHorario(),
                        regra.getFinalHorario()))
                .toList();
        var regrasSeparacao = entidade.getRegrasSeparacao().stream()
                .map(regra -> RegraSeparacao.recriar(
                        regra.getId(),
                        entidade.getId(),
                        regra.getIdTipoVeiculo(),
                        Duration.ofSeconds(regra.getTempoToleranciaSegundos(), regra.getTempoToleranciaNanos())))
                .toList();

        return TipoLista.recriar(
                entidade.getId(),
                new Nome(entidade.getNome()),
                observacaoOpcional(entidade.getDescricao()),
                regrasCadastro,
                regrasSeparacao,
                entidade.getDataArquivado());
    }

    public static void atualizarEntidade(TipoLista dominio, TipoListaJpa entidade) {
        entidade.setId(dominio.getId());
        entidade.setNome(dominio.getNome().valor());
        entidade.setDescricao(valorOpcional(dominio.getDescricao()));
        entidade.setDataArquivado(dominio.getDataArquivado());

        var regrasCadastroExistentes = entidade.getRegrasCadastro().stream()
                .collect(Collectors.toMap(RegraCadastroJpa::getId, Function.identity()));
        var idsRegrasCadastro = dominio.getListaRegrasCadastro().stream()
                .map(RegraCadastro::getId)
                .collect(Collectors.toSet());
        entidade.getRegrasCadastro().removeIf(regra -> !idsRegrasCadastro.contains(regra.getId()));
        for (var regra : dominio.getListaRegrasCadastro()) {
            var regraJpa = regrasCadastroExistentes.get(regra.getId());
            if (regraJpa == null) {
                regraJpa = new RegraCadastroJpa();
                regraJpa.setId(regra.getId());
                regraJpa.setLista(entidade);
                entidade.getRegrasCadastro().add(regraJpa);
            }
            substituirConteudo(regraJpa.getIdsVeiculos(), regra.getIdsVeiculos());
            substituirConteudo(regraJpa.getIdsCidades(), regra.listaCidades());
            substituirConteudo(regraJpa.getPrioridades(), regra.getPrioridades());
            regraJpa.setObrigacaoAnexo(regra.isObrigacaoAnexo());
            regraJpa.setDescricao(valorOpcional(regra.getDescricao()));
            regraJpa.setInicioHorario(regra.getInicioHorario());
            regraJpa.setFinalHorario(regra.getFinalHorario());
        }

        var regrasSeparacaoExistentes = entidade.getRegrasSeparacao().stream()
                .collect(Collectors.toMap(RegraSeparacaoJpa::getId, Function.identity()));
        var idsRegrasSeparacao = dominio.getListaRegraSeparacao().stream()
                .map(RegraSeparacao::getId)
                .collect(Collectors.toSet());
        entidade.getRegrasSeparacao().removeIf(regra -> !idsRegrasSeparacao.contains(regra.getId()));
        for (var regra : dominio.getListaRegraSeparacao()) {
            var regraJpa = regrasSeparacaoExistentes.get(regra.getId());
            if (regraJpa == null) {
                regraJpa = new RegraSeparacaoJpa();
                regraJpa.setId(regra.getId());
                regraJpa.setLista(entidade);
                entidade.getRegrasSeparacao().add(regraJpa);
            }
            regraJpa.setIdTipoVeiculo(regra.getIdTipoVeiculo());
            regraJpa.setTempoToleranciaSegundos(regra.getTempoTolerancia().getSeconds());
            regraJpa.setTempoToleranciaNanos(regra.getTempoTolerancia().getNano());
        }
    }

    public static TipoListaResumo paraResumo(TipoListaResumoProjecao projecao) {
        return new TipoListaResumo(projecao.getId(), projecao.getNome(), projecao.getDescricao(),
                projecao.getDataArquivado(), projecao.getTotalRegras(), projecao.getTotalCidades());
    }

    private static Observacao observacaoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : new Observacao(valor);
    }

    private static String valorOpcional(Observacao observacao) {
        return observacao == null ? null : observacao.valor();
    }

    private static <T> void substituirConteudo(java.util.Collection<T> destino,
            java.util.Collection<T> origem) {
        destino.clear();
        destino.addAll(origem);
    }
}
