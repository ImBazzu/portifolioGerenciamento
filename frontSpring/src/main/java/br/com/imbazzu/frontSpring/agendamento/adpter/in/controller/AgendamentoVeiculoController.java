package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletResponse;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.AgendamentoRedirect;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.RespostaFlash;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

/** Veículos físicos abertos para o dia: abrir, editar e excluir. */
@Controller
@RequestMapping("/agendamentos")
public class AgendamentoVeiculoController {

    private final AgendamentoApi agendamentoApi;
    private final VeiculoApplicationPorta tiposVeiculo;
    private final RespostaFlash respostaFlash;
    private final MensagemErroApi mensagemErro;

    public AgendamentoVeiculoController(
            AgendamentoApi agendamentoApi,
            VeiculoApplicationPorta tiposVeiculo,
            RespostaFlash respostaFlash,
            MensagemErroApi mensagemErro) {
        this.agendamentoApi = agendamentoApi;
        this.tiposVeiculo = tiposVeiculo;
        this.respostaFlash = respostaFlash;
        this.mensagemErro = mensagemErro;
    }

    @PostMapping("/veiculos")
    public String abrirVeiculo(
            @RequestParam LocalDate data,
            @RequestParam UUID idTipoVeiculo,
            @RequestParam(required = false) String identificacao,
            @RequestParam(required = false) LocalTime horarioSaida,
            RedirectAttributes redirect) {
        return abrir(data, idTipoVeiculo, identificacao, horarioSaida,
                FinalidadeAberturaVeiculo.AJUSTE, null, false, redirect);
    }

    @PostMapping("/carros/veiculos")
    public String abrirCarro(
            @RequestParam LocalDate data,
            @RequestParam UUID idTipoVeiculo,
            @RequestParam(required = false) String identificacao,
            @RequestParam(defaultValue = "AJUSTE") FinalidadeAberturaVeiculo finalidadeAbertura,
            @RequestParam(required = false) UUID idListaAbertura,
            @RequestParam(required = false) LocalTime horarioSaida,
            RedirectAttributes redirect) {
        return abrir(data, idTipoVeiculo, identificacao, horarioSaida,
                finalidadeAbertura, idListaAbertura, true, redirect);
    }

    @PostMapping("/veiculos/{idVeiculo}/editar")
    public String editarVeiculo(
            @PathVariable UUID idVeiculo,
            @RequestParam LocalDate data,
            @RequestParam(required = false) String identificacao,
            @RequestParam(required = false) LocalTime horarioSaida,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Veículo atualizado.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.atualizarVeiculo(
                        idVeiculo, new VeiculoAbertoAtualizarRequestDto(identificacao, horarioSaida)));
    }

    @PostMapping("/veiculos/{idVeiculo}/confirmar-saida")
    public String confirmarSaida(
            @PathVariable UUID idVeiculo,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Saída do veículo confirmada.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.confirmarSaida(idVeiculo));
    }

    @PostMapping("/veiculos/{idVeiculo}/desfazer-confirmacao-saida")
    public String desfazerConfirmacaoSaida(
            @PathVariable UUID idVeiculo,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Confirmação de saída desfeita.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.desfazerConfirmacaoSaida(idVeiculo));
    }

    @PostMapping(value = "/veiculos/{idVeiculo}/confirmar-saida", headers = "HX-Request=true")
    public void confirmarSaidaParcial(@PathVariable UUID idVeiculo, @RequestParam LocalDate data,
            HttpServletResponse response) {
        atualizarSaidaParcial(idVeiculo, data, true, response);
    }

    @PostMapping(value = "/veiculos/{idVeiculo}/desfazer-confirmacao-saida", headers = "HX-Request=true")
    public void desfazerSaidaParcial(@PathVariable UUID idVeiculo, @RequestParam LocalDate data,
            HttpServletResponse response) {
        atualizarSaidaParcial(idVeiculo, data, false, response);
    }

    private void atualizarSaidaParcial(UUID idVeiculo, LocalDate data, boolean confirmar,
            HttpServletResponse response) {
        try {
            if (confirmar) {
                agendamentoApi.confirmarSaida(idVeiculo);
            } else {
                agendamentoApi.desfazerConfirmacaoSaida(idVeiculo);
            }
            response.setHeader("HX-Trigger", "{\"carro-atualizado\":{\"veiculo\":\""
                    + idVeiculo + "\",\"data\":\"" + data + "\"}}");
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), mensagemErro.de(e), e);
        }
    }

    @PostMapping("/veiculos/{idVeiculo}/excluir")
    public String excluirVeiculo(
            @PathVariable UUID idVeiculo,
            @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean paginaCarros,
            RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Veículo excluído. Os passageiros foram devolvidos à lista de pendentes.",
                AgendamentoRedirect.retorno(paginaCarros, data),
                () -> agendamentoApi.removerVeiculo(idVeiculo));
    }

    private String abrir(
            LocalDate data,
            UUID idTipoVeiculo,
            String identificacao,
            LocalTime horarioSaida,
            FinalidadeAberturaVeiculo finalidadeAbertura,
            UUID idListaAbertura,
            boolean carro,
            RedirectAttributes redirect) {
        String destino = AgendamentoRedirect.retorno(carro, data);
        try {
            var tipo = tiposVeiculo.buscar(idTipoVeiculo);
            if (TipoVeiculoUtil.ehCarro(tipo.nome()) != carro) {
                redirect.addFlashAttribute("erro", carro
                        ? "Selecione um tipo de veículo Carro nesta página."
                        : "Veículos do tipo Carro só podem ser abertos na página de passageiros de carros.");
                return destino;
            }
            var veiculo = agendamentoApi.abrirVeiculo(
                    data, new AbrirVeiculoRequestDto(
                            idTipoVeiculo, identificacao, horarioSaida, finalidadeAbertura, idListaAbertura));
            if (veiculo == null || veiculo.id() == null) {
                redirect.addFlashAttribute("erro",
                        "Veículo aberto, mas não foi possível identificar o veículo para inserir passageiros.");
            } else {
                redirect.addFlashAttribute("veiculoParaInsercao", veiculo.id());
                redirect.addFlashAttribute("sucesso", "Veículo aberto para a agenda do dia.");
            }
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return destino;
    }
}
