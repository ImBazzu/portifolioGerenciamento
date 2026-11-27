package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntFunction;

import org.springframework.ui.Model;
import org.springframework.web.client.RestClientResponseException;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.DestinoView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoCanceladoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontSpring.usuario.UsuarioApi;

public class CarregadorOpcoesAgendamento {

    private final ListaApplicationPorta listas;
    private final VeiculoApplicationPorta tiposVeiculo;
    private final LocalidadeApplicationPorta localidades;
    private final UsuarioApi usuarios;

    public CarregadorOpcoesAgendamento(
            ListaApplicationPorta listas,
            VeiculoApplicationPorta tiposVeiculo,
            LocalidadeApplicationPorta localidades,
            UsuarioApi usuarios) {
        this.listas = listas;
        this.tiposVeiculo = tiposVeiculo;
        this.localidades = localidades;
        this.usuarios = usuarios;
    }

    public OpcoesAgendamento carregar() {
        var cidades = listarCidades();
        return new OpcoesAgendamento(listarListasAtivas(), listarTipos(), cidades, listarDestinos(cidades));
    }

    public OpcoesAgendamento carregarParaUsuario() {
        var opcoes = carregar();
        var visibilidade = usuarios.minhasListasAgendamento();
        if (!visibilidade.restrita()) {
            return opcoes;
        }
        var permitidas = Set.copyOf(visibilidade.idsListas());
        var listasVisiveis = opcoes.listas().stream()
                .filter(lista -> permitidas.contains(lista.id()))
                .toList();
        return new OpcoesAgendamento(listasVisiveis, opcoes.tiposVeiculo(), opcoes.cidades(), opcoes.destinos());
    }

    /** Mantém os mesmos nomes de atributo que os templates já usam. */
    public void adicionarAoModel(Model model, OpcoesAgendamento opcoes) {
        model.addAttribute("listas", opcoes.listas());
        model.addAttribute("tiposVeiculo", opcoes.tiposVeiculo());
        model.addAttribute("tiposVeiculoNaoCarro", opcoes.tiposNaoCarro());
        model.addAttribute("tiposVeiculoCarro", opcoes.tiposCarro());
        model.addAttribute("carroQuatroLugares", opcoes.carroQuatroLugares().orElse(null));
        model.addAttribute("cidades", opcoes.cidades());
        model.addAttribute("destinos", opcoes.destinos());
    }

    public Map<UUID, String> nomesListas(OpcoesAgendamento opcoes, List<AgendamentoResponseDto> passageiros) {
        Map<UUID, String> nomes = new HashMap<>();
        opcoes.listas().forEach(lista -> nomes.put(lista.id(), lista.nome()));
        passageiros.forEach(passageiro -> {
            UUID id = passageiro.idLista();
            nomes.computeIfAbsent(id, this::buscarNomeLista);
        });
        return nomes;
    }

    public Map<UUID, String> nomesListasCanceladas(
            OpcoesAgendamento opcoes, List<AgendamentoCanceladoResponseDto> passageiros) {
        Map<UUID, String> nomes = new HashMap<>();
        opcoes.listas().forEach(lista -> nomes.put(lista.id(), lista.nome()));
        passageiros.forEach(passageiro -> nomes.computeIfAbsent(passageiro.idLista(), this::buscarNomeLista));
        return nomes;
    }

    private String buscarNomeLista(UUID id) {
        try {
            return listas.buscarAtiva(id).nome();
        } catch (RestClientResponseException erroAtiva) {
            if (erroAtiva.getStatusCode().value() != 404) {
                throw erroAtiva;
            }
            return listas.buscarArquivada(id).nome();
        }
    }

    public Map<UUID, String> nomesTipos(OpcoesAgendamento opcoes, AgendaDiaResponseDto agenda) {
        Map<UUID, String> nomes = new HashMap<>(opcoes.nomesTipoVeiculo());
        agenda.veiculos().forEach(veiculo -> nomes.putIfAbsent(veiculo.idTipoVeiculo(), veiculo.nomeTipoVeiculo()));
        agenda.passageirosPendentes().forEach(passageiro -> nomes.computeIfAbsent(passageiro.idTipoVeiculo(),
                id -> tiposVeiculo.buscarArquivado(id).nome()));
        return nomes;
    }

    public OpcoesAgendamento incluirDestinosHistoricos(
            OpcoesAgendamento opcoes, List<AgendamentoResponseDto> passageiros) {
        return incluirDestinosHistoricos(opcoes,
                passageiros.stream().map(AgendamentoResponseDto::idDestino).collect(java.util.stream.Collectors.toSet()));
    }

    public OpcoesAgendamento incluirDestinosHistoricosCancelados(
            OpcoesAgendamento opcoes, List<AgendamentoCanceladoResponseDto> passageiros) {
        return incluirDestinosHistoricos(opcoes,
                passageiros.stream().map(AgendamentoCanceladoResponseDto::idDestino)
                        .collect(java.util.stream.Collectors.toSet()));
    }

    private OpcoesAgendamento incluirDestinosHistoricos(OpcoesAgendamento opcoes, Set<UUID> destinosSolicitados) {
        Set<UUID> faltantes = destinosSolicitados.stream()
                .filter(id -> opcoes.destino(id).isEmpty())
                .collect(java.util.stream.Collectors.toSet());
        if (faltantes.isEmpty()) return opcoes;

        var destinos = new ArrayList<>(opcoes.destinos());
        for (var cidade : opcoes.cidades()) {
            adicionarDestinosHistoricos(destinos, faltantes, cidade.id(), cidade.nome(), true);
            if (faltantes.isEmpty()) break;
        }
        if (!faltantes.isEmpty()) {
            var cidadesArquivadas = todasAsPaginas(pagina -> localidades.listarCidadesArquivadas("", pagina));
            for (var cidade : cidadesArquivadas) {
                adicionarDestinosHistoricos(destinos, faltantes, cidade.id(), cidade.nome(), false);
                if (!faltantes.isEmpty()) {
                    adicionarDestinosHistoricos(destinos, faltantes, cidade.id(), cidade.nome(), true);
                }
                if (faltantes.isEmpty()) break;
            }
        }
        return new OpcoesAgendamento(opcoes.listas(), opcoes.tiposVeiculo(), opcoes.cidades(), destinos);
    }

    private void adicionarDestinosHistoricos(List<DestinoView> destinos, Set<UUID> faltantes,
            UUID idCidade, String nomeCidade, boolean arquivados) {
        var resultados = todasAsPaginas(pagina -> arquivados
                ? localidades.listarDestinosArquivados(idCidade, "", pagina)
                : localidades.listarDestinos(idCidade, "", pagina));
        resultados.forEach(destino -> {
            if (faltantes.remove(destino.id())) {
                destinos.add(new DestinoView(destino.id(), idCidade, nomeCidade, destino.nome()));
            }
        });
    }

    private List<ListaResponseDto> listarListasAtivas() {
        return todasAsPaginas(pagina -> listas.listarAtivas("", pagina)).stream()
                .filter(lista -> !lista.arquivado())
                .map(lista -> listas.buscarAtiva(lista.id()))
                .toList();
    }

    private List<TipoVeiculoResponseDto> listarTipos() {
        return todasAsPaginas(pagina -> tiposVeiculo.listar("", pagina));
    }

    private List<CidadeResponseDto> listarCidades() {
        return todasAsPaginas(pagina -> localidades.listarCidades("", pagina));
    }

    // Uma chamada à API por cidade. É o ponto mais caro desta classe.
    private List<DestinoView> listarDestinos(List<CidadeResponseDto> cidades) {
        return cidades.stream()
                .flatMap(cidade -> todasAsPaginas(pagina -> localidades.listarDestinos(cidade.id(), "", pagina))
                        .stream()
                        .map(destino -> new DestinoView(destino.id(), cidade.id(), cidade.nome(), destino.nome())))
                .toList();
    }

    private static <T> List<T> todasAsPaginas(IntFunction<ResultadoPaginado<T>> busca) {
        var primeira = busca.apply(0);
        var todos = new ArrayList<>(primeira.conteudo());
        for (int pagina = 1; pagina < primeira.totalPaginas(); pagina++) {
            todos.addAll(busca.apply(pagina).conteudo());
        }
        return todos;
    }
}
