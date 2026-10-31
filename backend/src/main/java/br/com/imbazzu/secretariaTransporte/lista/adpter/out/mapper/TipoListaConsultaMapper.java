package br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper;

import java.time.Duration;
import java.util.List;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.RegraCadastroProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.RegraSeparacaoProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaDetalheProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaResumoProjecao;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe.RegraCadastroDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe.RegraSeparacaoDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;

/**
 * Mapper do lado Query: projeção -> modelo de leitura.
 * (O TipoListaBancoMapper continua só com o fluxo Command: entidade <-> domínio.)
 */
public final class TipoListaConsultaMapper {

    private TipoListaConsultaMapper() {
    }

    public static TipoListaResumo paraResumo(TipoListaResumoProjecao projecao) {
        return new TipoListaResumo(projecao.getId(), projecao.getNome(), projecao.getDescricao(),
                projecao.getDataArquivado(), projecao.getTotalRegras(), projecao.getTotalCidades());
    }

    public static TipoListaDetalhe paraDetalhe(TipoListaDetalheProjecao projecao) {
        var regrasCadastro = projecao.getRegrasCadastro().stream()
                .map(TipoListaConsultaMapper::paraRegraCadastro)
                .toList();
        var regrasSeparacao = projecao.getRegrasSeparacao().stream()
                .map(TipoListaConsultaMapper::paraRegraSeparacao)
                .toList();
        return new TipoListaDetalhe(projecao.getId(), projecao.getNome(), projecao.getDescricao(),
                projecao.getDataArquivado(), regrasCadastro, regrasSeparacao);
    }

    private static RegraCadastroDetalhe paraRegraCadastro(RegraCadastroProjecao regra) {
        // List.copyOf desacopla das coleções lazy do Hibernate: o modelo sai da transação já "pronto".
        return new RegraCadastroDetalhe(
                regra.getId(),
                List.copyOf(regra.getIdsVeiculos()),
                List.copyOf(regra.getIdsCidades()),
                List.copyOf(regra.getPrioridades()),
                regra.getInicioHorario(),
                regra.getFinalHorario(),
                regra.isObrigacaoAnexo(),
                regra.getDescricao());
    }

    private static RegraSeparacaoDetalhe paraRegraSeparacao(RegraSeparacaoProjecao regra) {
        return new RegraSeparacaoDetalhe(
                regra.getId(),
                regra.getIdTipoVeiculo(),
                Duration.ofSeconds(regra.getTempoToleranciaSegundos(), regra.getTempoToleranciaNanos()));
    }
}
