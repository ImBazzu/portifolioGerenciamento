package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorAgendaDia;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorDetalhesPassageiros;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroVerificadoView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.VeiculoDisponivelView;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.motorista.MotoristaApi;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;

/** Renderiza as duas páginas de agendamento: /agendamentos e /agendamentos/carros. */
@Controller
@RequestMapping("/agendamentos")
public class AgendamentoPaginaController {

    private final AgendamentoApi agendamentoApi;
    private final PessoaApplicationPorta pessoas;
    private final CarregadorOpcoesAgendamento carregadorOpcoes;
    private final MontadorAgendaDia montadorAgenda;
    private final MontadorDetalhesPassageiros montadorDetalhes;
    private final MensagemErroApi mensagemErro;
    private final MotoristaApi motoristas;

    public AgendamentoPaginaController(
            AgendamentoApi agendamentoApi,
            PessoaApplicationPorta pessoas,
            CarregadorOpcoesAgendamento carregadorOpcoes,
            MontadorAgendaDia montadorAgenda,
            MontadorDetalhesPassageiros montadorDetalhes,
            MensagemErroApi mensagemErro,
            MotoristaApi motoristas) {
        this.agendamentoApi = agendamentoApi;
        this.pessoas = pessoas;
        this.carregadorOpcoes = carregadorOpcoes;
        this.montadorAgenda = montadorAgenda;
        this.montadorDetalhes = montadorDetalhes;
        this.mensagemErro = mensagemErro;
        this.motoristas = motoristas;
    }

    @GetMapping
    public String inicio(@RequestParam(required = false) LocalDate data,
            @RequestParam(required = false) UUID veiculoOrigem,
            @RequestParam(defaultValue = "false") boolean origemCarros,
            @RequestParam(defaultValue = "false") boolean novoPassageiro,
            @RequestParam(required = false) UUID idLista,
            @RequestParam(required = false) LocalTime horarioSaidaVeiculo,
            @RequestParam(defaultValue = "false") boolean informarHorarioSaida,
            Model model) {
        var dia = data == null ? LocalDate.now() : data;
        var opcoes = carregadorOpcoes.carregarParaUsuario();

        model.addAttribute("pagina", "agendamentos");
        model.addAttribute("data", dia);
        if (!model.containsAttribute("veiculoOrigem")) {
            model.addAttribute("veiculoOrigem", veiculoOrigem);
        }
        if (!model.containsAttribute("origemCarros")) {
            model.addAttribute("origemCarros", origemCarros);
        }
        model.addAttribute("novoPassageiro", novoPassageiro);
        if (idLista != null && !model.containsAttribute("idListaSelecionada")) {
            model.addAttribute("idListaSelecionada", idLista);
        }
        if (horarioSaidaVeiculo != null && !model.containsAttribute("horarioChegadaSelecionado")) {
            model.addAttribute("horarioChegadaSelecionado", horarioSaidaVeiculo);
        }
        if (informarHorarioSaida) {
            model.addAttribute("informarHorarioSaida", true);
        }
        carregadorOpcoes.adicionarAoModel(model, opcoes);

        restaurarSelecaoDoFormulario(model, opcoes);
        montarPassageiroVerificado(model, opcoes);

        try {
            var agenda = agendamentoApi.buscarDia(dia);
            if (model.getAttribute("veiculoOrigem") instanceof UUID origem) {
                var veiculo = agenda.veiculos().stream().filter(item -> item.id().equals(origem))
                        .findFirst().orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Veículo de origem não encontrado nesta data."));
                selecionarListaInsercao(model, opcoes, veiculo.idListaAbertura());
            }
            var passageiros = Stream.concat(
                    agenda.veiculos().stream().flatMap(veiculo -> veiculo.passageiros().stream()),
                    agenda.passageirosPendentes().stream()).toList();
            var opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricos(opcoes, passageiros);
            var detalhes = montadorDetalhes.montarPorAgendamento(passageiros, opcoesComHistorico);
            var detalhesPorPessoa = passageiros.stream().collect(Collectors.toMap(
                    passageiro -> passageiro.idPessoa(), passageiro -> detalhes.get(passageiro.id()), (primeiro, outro) -> primeiro));
            opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricosCancelados(
                    opcoesComHistorico, agenda.passageirosCancelados());
            adicionarAgenda(model, agenda, opcoesComHistorico, detalhesPorPessoa);
            montadorAgenda.consolidarPassageiros(model, agenda);
            model.addAttribute("detalhesAgendamentos", detalhes);
            model.addAttribute("nomesListasPassageiros", carregadorOpcoes.nomesListas(opcoes, passageiros));
            model.addAttribute("nomesTiposPassageiros", carregadorOpcoes.nomesTipos(opcoes, agenda));
        } catch (RestClientResponseException e) {
            tratarErroAgenda(model, e);
        }
        return "agendamento/inicio";
    }

    @GetMapping("/carros")
    public String passageirosCarro(@RequestParam(required = false) LocalDate data, Model model) {
        var dia = data == null ? LocalDate.now() : data;
        var opcoes = carregadorOpcoes.carregarParaUsuario();

        model.addAttribute("pagina", "agendamentos");
        model.addAttribute("paginaCarros", true);
        model.addAttribute("data", dia);
        carregadorOpcoes.adicionarAoModel(model, opcoes);

        try {
            var agenda = agendamentoApi.buscarDia(dia);
            var opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricosCancelados(opcoes,
                    agenda.passageirosCancelados());
            adicionarAgenda(model, agenda, opcoesComHistorico);
        } catch (RestClientResponseException e) {
            tratarErroAgenda(model, e);
        }
        return "agendamento/carros";
    }

    @GetMapping("/carros/veiculos/{id}/modal-agendamento")
    public String modalAgendamentoCarro(@PathVariable UUID id, @RequestParam LocalDate data, Model model) {
        var veiculo = agendamentoApi.buscarDia(data).veiculos().stream()
                .filter(item -> item.id().equals(id))
                .filter(item -> TipoVeiculoUtil.ehCarro(item.nomeTipoVeiculo()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado nesta data."));
        var opcoes = carregadorOpcoes.carregarParaUsuario();
        carregadorOpcoes.adicionarAoModel(model, opcoes);
        model.addAttribute("data", data);
        model.addAttribute("veiculoOrigem", id);
        model.addAttribute("origemCarros", true);
        model.addAttribute("novoPassageiro", true);
        selecionarListaInsercao(model, opcoes, veiculo.idListaAbertura());
        if (veiculo.idListaAbertura() != null) {
            model.addAttribute("horarioChegadaSelecionado", veiculo.horarioSaida());
            model.addAttribute("informarHorarioSaida", true);
        }
        return "agendamento/fragmentos/modal-agendamento :: modalAgendamento";
    }

    private void selecionarListaInsercao(Model model, OpcoesAgendamento opcoes, UUID listaAbertura) {
        var lista = listaAbertura != null ? listaAbertura : opcoes.listaEncaixe()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "A lista Encaixe está indisponível. Verifique se está ativa e liberada para o usuário."))
                .id();
        if (opcoes.listas().stream().noneMatch(item -> item.id().equals(lista))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A lista do veículo está indisponível. Verifique se está ativa e liberada para o usuário.");
        }
        model.addAttribute("idListaSelecionada", lista);
        model.addAttribute("listaFixaInsercao", true);
    }

    @GetMapping("/carros/veiculos/{id}/cartao")
    public String cartaoCarro(@PathVariable UUID id, @RequestParam LocalDate data, Model model) {
        var agenda = agendamentoApi.buscarDia(data);
        var veiculo = agenda.veiculos().stream()
                .filter(item -> item.id().equals(id) && TipoVeiculoUtil.ehCarro(item.nomeTipoVeiculo()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado nesta data."));
        var opcoes = carregadorOpcoes.incluirDestinosHistoricos(
                carregadorOpcoes.carregarParaUsuario(), agenda.passageirosPendentes());
        opcoes = carregadorOpcoes.incluirDestinosHistoricosCancelados(opcoes, agenda.passageirosCancelados());
        carregadorOpcoes.adicionarAoModel(model, opcoes);
        model.addAttribute("data", data);
        model.addAttribute("paginaCarros", true);
        model.addAttribute("agenda", agenda);
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("detalhesPassageiros", montadorDetalhes.montar(
                new AgendaDiaResponseDto(data, List.of(veiculo), List.of()), opcoes));
        model.addAttribute("motoristasPorVeiculo", motoristas.atribuicoes(data).stream()
                .collect(Collectors.toMap(MotoristaApi.Atribuicao::veiculoId, MotoristaApi.Atribuicao::nome)));
        return "agendamento/carros/fragmentos/carros-atribuidos :: carro";
    }

    private void adicionarAgenda(Model model, AgendaDiaResponseDto agenda, OpcoesAgendamento opcoes) {
        var opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricos(
                opcoes, agenda.passageirosPendentes());
        opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricosCancelados(
                opcoesComHistorico, agenda.passageirosCancelados());
        adicionarAgenda(model, agenda, opcoesComHistorico, montadorDetalhes.montar(agenda, opcoesComHistorico));
    }

    private void adicionarAgenda(Model model, AgendaDiaResponseDto agenda, OpcoesAgendamento opcoes,
            Map<UUID, PassageiroDetalheView> detalhes) {
        model.addAttribute("agenda", agenda);
        if (model.getAttribute("pessoaComprovante") instanceof UUID idPessoa) {
            Stream.concat(agenda.veiculos().stream().flatMap(veiculo -> veiculo.passageiros().stream()),
                    agenda.passageirosPendentes().stream())
                    .filter(passageiro -> passageiro.idPessoa().equals(idPessoa))
                    .findFirst()
                    .ifPresent(passageiro -> model.addAttribute("agendamentoComprovante", passageiro.id()));
        }
        model.addAttribute("detalhesPassageiros", detalhes);
        model.addAttribute("detalhesCancelados",
                montadorDetalhes.montarCancelados(agenda.passageirosCancelados(), opcoes));
        model.addAttribute("nomesListasCanceladas",
                carregadorOpcoes.nomesListasCanceladas(opcoes, agenda.passageirosCancelados()));
        try {
            model.addAttribute("motoristasPorVeiculo", motoristas.atribuicoes(agenda.data()).stream()
                    .collect(Collectors.toMap(MotoristaApi.Atribuicao::veiculoId, MotoristaApi.Atribuicao::nome)));
        } catch (RestClientResponseException e) {
            model.addAttribute("erroMotoristas", mensagemErro.de(e));
        }
        montadorAgenda.dividirPorTipo(model, agenda, opcoes);
        if (model.getAttribute("verificacao") instanceof VerificacaoVeiculosResponseDto verificacao) {
            int acompanhantes = model.getAttribute("acompanhantesSelecionados") instanceof Number numero
                    ? numero.intValue()
                    : 0;
            model.addAttribute("veiculosDisponiveis",
                    VeiculoDisponivelView.disponiveis(verificacao, agenda, acompanhantes,
                            model.getAttribute("veiculoOrigem") instanceof UUID id ? id : null));
        }
    }

    private void tratarErroAgenda(Model model, RestClientResponseException e) {
        if (e.getStatusCode().value() == 404) {
            model.addAttribute("agenda", null);
        } else {
            model.addAttribute("erroConsultaAgenda", true);
            model.addAttribute("erro", mensagemErro.de(e));
        }
    }

    /** Flash attributes de um redirect (pessoa/destino escolhidos) viram dados para repopular o modal. */
    private void restaurarSelecaoDoFormulario(Model model, OpcoesAgendamento opcoes) {
        if (model.asMap().get("idPessoaSelecionada") instanceof UUID idPessoa) {
            model.addAttribute("pessoaSelecionada", pessoas.buscar(idPessoa));
        }
        if (model.asMap().get("idDestinoSelecionado") instanceof UUID idDestino) {
            opcoes.destino(idDestino).ifPresent(destino -> {
                model.addAttribute("idCidadeSelecionada", destino.idCidade());
                model.addAttribute("destinoSelecionado", destino.nome());
                opcoes.cidade(destino.idCidade()).ifPresent(cidade ->
                        model.addAttribute("cidadeSelecionada", cidade.nome() + " - " + cidade.estado()));
            });
        }
    }

    private void montarPassageiroVerificado(Model model, OpcoesAgendamento opcoes) {
        if (model.getAttribute("verificacao") == null) {
            return;
        }
        if (!(model.getAttribute("pessoaSelecionada") instanceof PessoaResponseDto pessoa)
                || !(model.getAttribute("idDestinoSelecionado") instanceof UUID idDestino)
                || !(model.getAttribute("horarioChegadaSelecionado") instanceof LocalTime horarioChegada)) {
            return;
        }
        int acompanhantes = model.getAttribute("acompanhantesSelecionados") instanceof Number n ? n.intValue() : 0;
        boolean buscarNaResidencia = Boolean.TRUE.equals(model.getAttribute("buscarNaResidenciaSelecionado"));

        model.addAttribute("maxAcompanhantes", pessoa.prioridadeAcompanhantes());
        model.addAttribute("passageiroVerificado", PassageiroVerificadoView.de(
                pessoa, opcoes.destino(idDestino).orElse(null), horarioChegada, buscarNaResidencia, acompanhantes));
    }
}
