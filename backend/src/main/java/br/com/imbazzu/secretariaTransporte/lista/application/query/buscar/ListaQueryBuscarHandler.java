package br.com.imbazzu.secretariaTransporte.lista.application.query.buscar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscarResultado.IdNome;
import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscarResultado.RegraCadastroResultado;
import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscarResultado.RegraSeparacaoResultado;
import br.com.imbazzu.secretariaTransporte.lista.core.excecoes.TipoListaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe.RegraCadastroDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe.RegraSeparacaoDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPortaQuery;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListaQueryBuscarHandler {

    private final TipoListaPortaQuery portaQuery;
    private final CidadeQueryPorta cidadeQueryPorta;
    private final TipoVeiculoQueryPorta tipoVeiculoQueryPorta;

    @Transactional(readOnly = true)
    public ListaQueryBuscarResultado handler(ListaQueryBuscar query) {
        var detalhe = portaQuery.buscarPorId(query.id())
                .orElseThrow(() -> new TipoListaNaoEncontradoException(query.id()));

        // Cache por execução: o mesmo id aparece em várias regras e só deve ser consultado uma vez.
        Map<UUID, String> nomesCidades = new HashMap<>();
        Map<UUID, String> nomesVeiculos = new HashMap<>();

        var regrasCadastro = detalhe.regrasCadastro().stream()
                .map(regra -> paraResultado(regra, nomesCidades, nomesVeiculos))
                .toList();
        var regrasSeparacao = detalhe.regrasSeparacao().stream()
                .map(this::paraResultado)
                .toList();

        return new ListaQueryBuscarResultado(
                detalhe.id(),
                detalhe.nome(),
                detalhe.descricao(),
                detalhe.dataArquivado(),
                regrasCadastro,
                regrasSeparacao);
    }

    private RegraCadastroResultado paraResultado(RegraCadastroDetalhe regra,
            Map<UUID, String> nomesCidades, Map<UUID, String> nomesVeiculos) {
        return new RegraCadastroResultado(
                regra.id(),
                comNomes(regra.idsVeiculos(), nomesVeiculos, this::nomeVeiculo),
                comNomes(regra.idsCidades(), nomesCidades, this::nomeCidade),
                regra.prioridades(),
                regra.inicioHorario(),
                regra.finalHorario(),
                regra.obrigacaoAnexo(),
                regra.descricao());
    }

    private RegraSeparacaoResultado paraResultado(RegraSeparacaoDetalhe regra) {
        return new RegraSeparacaoResultado(
                regra.id(),
                regra.idTipoVeiculo(),
                regra.nome(),
                regra.tempoTolerancia(),
                regra.tempoToleranciaSegundos(),
                regra.tempoToleranciaNanos());
    }

    private List<IdNome> comNomes(List<UUID> ids, Map<UUID, String> cache, Function<UUID, String> resolvedor) {
        return ids.stream()
                .map(id -> new IdNome(id, cache.computeIfAbsent(id, resolvedor)))
                .toList();
    }

    private String nomeCidade(UUID idCidade) {
        return cidadeQueryPorta.buscar(idCidade)
                .map(CidadeQueryPorta.CidadeOutput::nome)
                .or(() -> cidadeQueryPorta.buscarArquivado(idCidade)
                        .map(CidadeQueryPorta.CidadeOutputArquivado::nome))
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Cidade não encontrada: " + idCidade));
    }

    private String nomeVeiculo(UUID idTipoVeiculo) {
        return tipoVeiculoQueryPorta.buscarAtivo(idTipoVeiculo)
                .map(veiculo -> veiculo.nome())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Tipo de veículo não encontrado: " + idTipoVeiculo));
    }
}
