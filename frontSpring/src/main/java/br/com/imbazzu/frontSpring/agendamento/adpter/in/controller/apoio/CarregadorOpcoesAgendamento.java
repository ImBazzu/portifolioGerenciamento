package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import org.springframework.ui.Model;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.DestinoView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

public class CarregadorOpcoesAgendamento {

    private final ListaApplicationPorta listas;
    private final VeiculoApplicationPorta tiposVeiculo;
    private final LocalidadeApplicationPorta localidades;

    public CarregadorOpcoesAgendamento(
            ListaApplicationPorta listas,
            VeiculoApplicationPorta tiposVeiculo,
            LocalidadeApplicationPorta localidades) {
        this.listas = listas;
        this.tiposVeiculo = tiposVeiculo;
        this.localidades = localidades;
    }

    public OpcoesAgendamento carregar() {
        var cidades = listarCidades();
        return new OpcoesAgendamento(listarListasAtivas(), listarTipos(), cidades, listarDestinos(cidades));
    }

    /** Mantém os mesmos nomes de atributo que os templates já usam. */
    public void adicionarAoModel(Model model, OpcoesAgendamento opcoes) {
        model.addAttribute("listas", opcoes.listas());
        model.addAttribute("tiposVeiculo", opcoes.tiposVeiculo());
        model.addAttribute("tiposVeiculoNaoCarro", opcoes.tiposNaoCarro());
        model.addAttribute("tiposVeiculoCarro", opcoes.tiposCarro());
        model.addAttribute("cidades", opcoes.cidades());
        model.addAttribute("destinos", opcoes.destinos());
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
