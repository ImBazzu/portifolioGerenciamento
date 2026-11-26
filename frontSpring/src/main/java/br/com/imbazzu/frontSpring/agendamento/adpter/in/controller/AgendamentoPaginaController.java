package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;

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
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.motorista.MotoristaApi;

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
            Model model) {
        var dia = data == null ? LocalDate.now() : data;
        var opcoes = carregadorOpcoes.carregar();

        model.addAttribute("pagina", "agendamentos");
        model.addAttribute("data", dia);
        if (!model.containsAttribute("veiculoOrigem")) {
            model.addAttribute("veiculoOrigem", veiculoOrigem);
        }
        if (!model.containsAttribute("origemCarros")) {
            model.addAttribute("origemCarros", origemCarros);
        }
        model.addAttribute("novoPassageiro", novoPassageiro);
        carregadorOpcoes.adicionarAoModel(model, opcoes);

        restaurarSelecaoDoFormulario(model, opcoes);
        montarPassageiroVerificado(model, opcoes);

        try {
            var agenda = agendamentoApi.buscarDia(dia);
            var passageiros = Stream.concat(
                    agenda.veiculos().stream().flatMap(veiculo -> veiculo.passageiros().stream()),
                    agenda.passageirosPendentes().stream()).toList();
            var opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricos(opcoes, passageiros);
            var detalhes = montadorDetalhes.montarPorAgendamento(passageiros, opcoesComHistorico);
            var detalhesPorPessoa = passageiros.stream().collect(Collectors.toMap(
                    passageiro -> passageiro.idPessoa(), passageiro -> detalhes.get(passageiro.id()), (primeiro, outro) -> primeiro));
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
        var opcoes = carregadorOpcoes.carregar();

        model.addAttribute("pagina", "agendamentos");
        model.addAttribute("data", dia);
        carregadorOpcoes.adicionarAoModel(model, opcoes);

        try {
            adicionarAgenda(model, agendamentoApi.buscarDia(dia), opcoes);
        } catch (RestClientResponseException e) {
            tratarErroAgenda(model, e);
        }
        return "agendamento/carros";
    }

    private void adicionarAgenda(Model model, AgendaDiaResponseDto agenda, OpcoesAgendamento opcoes) {
        adicionarAgenda(model, agenda, opcoes, montadorDetalhes.montar(agenda, opcoes));
    }

    private void adicionarAgenda(Model model, AgendaDiaResponseDto agenda, OpcoesAgendamento opcoes,
            Map<UUID, PassageiroDetalheView> detalhes) {
        model.addAttribute("agenda", agenda);
        model.addAttribute("detalhesPassageiros", detalhes);
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
                    VeiculoDisponivelView.disponiveis(verificacao, agenda, acompanhantes));
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
