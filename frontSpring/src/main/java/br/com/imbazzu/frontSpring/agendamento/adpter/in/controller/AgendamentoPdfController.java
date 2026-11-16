package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorDetalhesPassageiros;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf.AgendamentoPdfService;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;

/** Relatórios em PDF. */
@Controller
@RequestMapping("/agendamentos/pdf")
public class AgendamentoPdfController {

    private final AgendamentoApi agendamentoApi;
    private final CarregadorOpcoesAgendamento carregadorOpcoes;
    private final MontadorDetalhesPassageiros montadorDetalhes;
    private final MensagemErroApi mensagemErro;

    public AgendamentoPdfController(
            AgendamentoApi agendamentoApi,
            CarregadorOpcoesAgendamento carregadorOpcoes,
            MontadorDetalhesPassageiros montadorDetalhes,
            MensagemErroApi mensagemErro) {
        this.agendamentoApi = agendamentoApi;
        this.carregadorOpcoes = carregadorOpcoes;
        this.montadorDetalhes = montadorDetalhes;
        this.mensagemErro = mensagemErro;
    }

    @GetMapping("/carros")
    public ResponseEntity<byte[]> pdfGeralCarros(@RequestParam LocalDate data) {
        try {
            var opcoes = carregadorOpcoes.carregar();
            var agenda = agendamentoApi.buscarDia(data);
            var nomesPorTipo = opcoes.nomesTipoVeiculo();

            var veiculos = agenda.veiculos().stream()
                    .filter(v -> TipoVeiculoUtil.ehCarro(v.nomeTipoVeiculo()))
                    .sorted(Comparator.comparing(
                            VeiculoAbertoResponseDto::horarioSaida,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            var semVeiculo = agenda.passageirosPendentes().stream()
                    .filter(p -> TipoVeiculoUtil.ehCarro(nomesPorTipo.get(p.idTipoVeiculo())))
                    .toList();
            var noVeiculo = veiculos.stream().flatMap(v -> v.passageiros().stream()).toList();

            var detalhes = montadorDetalhes.montar(noVeiculo, semVeiculo, opcoes);
            var pdf = AgendamentoPdfService.gerar(data, "Relatório geral de carros", veiculos, semVeiculo, detalhes);
            return pdfInline(pdf, "agenda-carros-" + data + ".pdf");
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, mensagemErro.de(e), e);
        }
    }

    @GetMapping("/veiculos/{idVeiculo}")
    public ResponseEntity<byte[]> pdfVeiculo(@PathVariable UUID idVeiculo, @RequestParam LocalDate data) {
        try {
            var opcoes = carregadorOpcoes.carregar();
            var agenda = agendamentoApi.buscarDia(data);

            var veiculo = agenda.veiculos().stream()
                    .filter(v -> v.id().equals(idVeiculo))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Veículo não encontrado nesta agenda."));

            var detalhes = montadorDetalhes.montar(
                    veiculo.passageiros(), List.<AgendamentoResponseDto>of(), opcoes);
            var pdf = AgendamentoPdfService.gerarIndividual(data, veiculo, detalhes);
            return pdfInline(pdf, "veiculo-" + idVeiculo + "-" + data + ".pdf");
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, mensagemErro.de(e), e);
        }
    }

    private static ResponseEntity<byte[]> pdfInline(byte[] pdf, String nomeArquivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                .body(pdf);
    }
}
