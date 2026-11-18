package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.AgendamentoRedirect;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorAgendaDia;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.RespostaFlash;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroVerificadoView;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

/** Ciclo de vida do passageiro agendado: verificar, criar, editar, atribuir a veículo e excluir. */
@Controller
@RequestMapping("/agendamentos")
public class AgendamentoPassageiroController {

    // AJUSTE para o caminho real do seu modal-agendamento.html (o th:fragment "resultadoVerificacao" está nele).
    private static final String VIEW_RESULTADO_VERIFICACAO =
            "agendamento/fragmentos/modal-agendamento :: resultadoVerificacao";

    private final AgendamentoApi agendamentoApi;
    private final PessoaApplicationPorta pessoas;
    private final VeiculoApplicationPorta tiposVeiculo;
    private final CarregadorOpcoesAgendamento carregadorOpcoes;
    private final MontadorAgendaDia montadorAgenda;
    private final RespostaFlash respostaFlash;
    private final MensagemErroApi mensagemErro;

    public AgendamentoPassageiroController(
            AgendamentoApi agendamentoApi,
            PessoaApplicationPorta pessoas,
            VeiculoApplicationPorta tiposVeiculo,
            CarregadorOpcoesAgendamento carregadorOpcoes,
            MontadorAgendaDia montadorAgenda,
            RespostaFlash respostaFlash,
            MensagemErroApi mensagemErro) {
        this.agendamentoApi = agendamentoApi;
        this.pessoas = pessoas;
        this.tiposVeiculo = tiposVeiculo;
        this.carregadorOpcoes = carregadorOpcoes;
        this.montadorAgenda = montadorAgenda;
        this.respostaFlash = respostaFlash;
        this.mensagemErro = mensagemErro;
    }

    @PostMapping("/verificar-veiculos")
    public String verificarVeiculos(
            @RequestParam LocalDate data,
            @RequestParam UUID idPessoa,
            @RequestParam UUID idLista,
            @RequestParam UUID idDestino,
            @RequestParam LocalTime horarioChegada,
            @RequestParam(defaultValue = "0") int acompanhantes,
            @RequestParam boolean buscarNaResidencia,
            @RequestParam(required = false) UUID idTipoVeiculoSelecionado,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            Model model,
            RedirectAttributes redirect) {

        VerificacaoVeiculosResponseDto resposta = null;
        String erro = null;
        try {
            resposta = agendamentoApi.verificarVeiculos(data, new VerificarVeiculosRequestDto(
                    idPessoa, idLista, idDestino, horarioChegada, acompanhantes));
        } catch (RestClientResponseException e) {
            erro = mensagemErro.de(e);
        }

        if ("true".equalsIgnoreCase(hxRequest)) {
            model.addAttribute("data", data);
            model.addAttribute("idPessoaSelecionada", idPessoa);
            model.addAttribute("idListaSelecionada", idLista);
            model.addAttribute("idDestinoSelecionado", idDestino);
            model.addAttribute("horarioChegadaSelecionado", horarioChegada);
            model.addAttribute("acompanhantesSelecionados", acompanhantes);
            model.addAttribute("buscarNaResidenciaSelecionado", buscarNaResidencia);
            model.addAttribute("idTipoVeiculoSelecionado", idTipoVeiculoSelecionado);
            model.addAttribute("verificacao", resposta);
            model.addAttribute("erroVerificacao", erro);

            var opcoes = carregadorOpcoes.carregar();
            carregadorOpcoes.adicionarAoModel(model, opcoes);
            try {
                var agenda = agendamentoApi.buscarDia(data);
                model.addAttribute("agenda", agenda);
                montadorAgenda.dividirPorTipo(model, agenda, opcoes);

                var pessoa = pessoas.buscar(idPessoa);
                model.addAttribute("passageiroVerificado", PassageiroVerificadoView.de(
                        pessoa, opcoes.destino(idDestino).orElse(null),
                        horarioChegada, buscarNaResidencia, acompanhantes));
            } catch (RestClientResponseException e) {
                model.addAttribute("erroVerificacao",
                        "Não foi possível carregar os dados para confirmar: %s".formatted(mensagemErro.de(e)));
            }
            return VIEW_RESULTADO_VERIFICACAO;
        }

        // Fallback sem htmx: guarda o estado e volta para a página.
        manterDadosAgendamento(
                redirect, idPessoa, idLista, idDestino, horarioChegada, acompanhantes, buscarNaResidencia);
        if (idTipoVeiculoSelecionado != null) {
            redirect.addFlashAttribute("idTipoVeiculoSelecionado", idTipoVeiculoSelecionado);
        }
        if (resposta != null) {
            redirect.addFlashAttribute("verificacao", resposta);
        }
        if (erro != null) {
            redirect.addFlashAttribute("erro", erro);
        }
        return AgendamentoRedirect.dia(data);
    }

    @PostMapping("/passageiros")
    public String criar(
            @RequestParam LocalDate data,
            @RequestParam UUID idPessoa,
            @RequestParam UUID idLista,
            @RequestParam UUID idTipoVeiculo,
            @RequestParam UUID idDestino,
            @RequestParam LocalTime horarioChegada,
            @RequestParam(defaultValue = "false") boolean buscarNaResidencia,
            @RequestParam(defaultValue = "0") int acompanhantes,
            @RequestParam(required = false) UUID idVeiculo,
            RedirectAttributes redirect) {
        try {
            agendamentoApi.criar(data, new AgendamentoRequestDto(
                    idPessoa, idLista, idTipoVeiculo, idDestino,
                    horarioChegada, buscarNaResidencia, acompanhantes, idVeiculo));
            redirect.addFlashAttribute("sucesso", idVeiculo == null
                    ? "Passageiro de carro agendado para separação."
                    : "Passageiro agendado e atribuído ao veículo físico.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
            manterDadosAgendamento(
                    redirect, idPessoa, idLista, idDestino, horarioChegada, acompanhantes, buscarNaResidencia);
            redirect.addFlashAttribute("idTipoVeiculoSelecionado", idTipoVeiculo);
            redirect.addFlashAttribute("idVeiculoSelecionado", idVeiculo);
            try {
                redirect.addFlashAttribute("verificacao", agendamentoApi.verificarVeiculos(
                        data, new VerificarVeiculosRequestDto(
                                idPessoa, idLista, idDestino, horarioChegada, acompanhantes)));
            } catch (RestClientResponseException erroVerificacao) {
                redirect.addFlashAttribute("erroVerificacao",
                        "Não foi possível atualizar os tipos permitidos: %s"
                                .formatted(mensagemErro.de(erroVerificacao)));
            }
        }
        return AgendamentoRedirect.dia(data);
    }

 @PostMapping("/{idAgendamento}/editar")
    public String editar(
            @PathVariable UUID idAgendamento,
            @RequestParam LocalDate data,
            @RequestParam UUID idTipoVeiculo,
            @RequestParam(required = false) UUID idDestino,
            @RequestParam LocalTime horarioChegada,
            @RequestParam(defaultValue = "false") boolean buscarNaResidencia,
            @RequestParam(defaultValue = "0") int acompanhantes,
            @RequestParam(required = false) UUID idVeiculo,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        try {
            // TRATAMENTO DE SEGURANÇA: Se o idDestino vier nulo, busca o agendamento atual e mantém o destino antigo
            UUID destinoFinal = idDestino;
            if (destinoFinal == null) {
                var agendamentoAtual = agendamentoApi.buscarPorId(idAgendamento); // Ajuste para o método que busca o agendamento atual na sua API
                destinoFinal = agendamentoAtual.idDestino();
            }

            agendamentoApi.atualizar(idAgendamento, new AgendamentoAtualizarRequestDto(
                    idTipoVeiculo, destinoFinal, horarioChegada, buscarNaResidencia, acompanhantes, idVeiculo));
            
            redirect.addFlashAttribute("sucesso", "Agendamento atualizado.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return AgendamentoRedirect.retorno(paginaCarros, data);
    }

    // OBS: /atribuir-direto e /{id}/veiculo fazem a mesma coisa. Veja qual template usa cada um e elimine um.
    @PostMapping("/atribuir-direto")
    public String atribuirDireto(
            @RequestParam UUID idAgendamento,
            @RequestParam UUID idVeiculo,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Passageiro inserido no veículo com sucesso.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.atribuir(idAgendamento, idVeiculo));
    }

    @PostMapping("/{idAgendamento}/veiculo")
    public String atribuirVeiculo(
            @PathVariable UUID idAgendamento,
            @RequestParam UUID idVeiculo,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Passageiro atribuído ao veículo.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.atribuir(idAgendamento, idVeiculo));
    }

    @PostMapping("/{idAgendamento}/remover-veiculo")
    public String removerVeiculo(
            @PathVariable UUID idAgendamento,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Passageiro removido do veículo.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.removerDoVeiculo(idAgendamento));
    }

    @PostMapping("/{idAgendamento}/excluir")
    public String excluir(
            @PathVariable UUID idAgendamento,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Agendamento excluído.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.remover(idAgendamento));
    }

    private void manterDadosAgendamento(
            RedirectAttributes redirect,
            UUID idPessoa,
            UUID idLista,
            UUID idDestino,
            LocalTime horarioChegada,
            int acompanhantes,
            boolean buscarNaResidencia) {
        redirect.addFlashAttribute("idPessoaSelecionada", idPessoa);
        redirect.addFlashAttribute("idListaSelecionada", idLista);
        redirect.addFlashAttribute("idDestinoSelecionado", idDestino);
        redirect.addFlashAttribute("horarioChegadaSelecionado", horarioChegada);
        redirect.addFlashAttribute("acompanhantesSelecionados", acompanhantes);
        redirect.addFlashAttribute("buscarNaResidenciaSelecionado", buscarNaResidencia);
    }
}
