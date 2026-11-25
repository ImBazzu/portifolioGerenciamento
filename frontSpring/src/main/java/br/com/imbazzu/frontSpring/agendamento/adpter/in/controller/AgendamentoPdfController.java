package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.CarregadorOpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MontadorDetalhesPassageiros;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroRelatorioView;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf.AgendamentoPdfService;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;

/** Relatórios em PDF. */
@Controller
@RequestMapping("/agendamentos/pdf")
public class AgendamentoPdfController {

    private static final String PDFS_PASSAGEIROS = AgendamentoPdfController.class.getName() + ".pdfsPassageiros";
    private static final Duration VALIDADE_PDF = Duration.ofHours(1);
    private static final int MAX_PDFS_POR_SESSAO = 10;

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

    @PostMapping("/passageiros")
    public ResponseEntity<Void> pdfPassageiros(
            @RequestParam LocalDate data, @RequestParam("ids") List<UUID> ids, HttpServletRequest request) {
        if (ids.isEmpty() || new HashSet<>(ids).size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione passageiros sem duplicatas.");
        }
        try {
            var opcoes = carregadorOpcoes.carregar();
            var agenda = agendamentoApi.buscarDia(data);
            var passageiros = Stream.concat(
                    agenda.veiculos().stream().flatMap(veiculo -> veiculo.passageiros().stream()),
                    agenda.passageirosPendentes().stream())
                    .collect(Collectors.toMap(AgendamentoResponseDto::id, Function.identity()));
            if (!passageiros.keySet().containsAll(ids)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A agenda mudou. Atualize a página e gere o relatório novamente.");
            }
            var selecionados = ids.stream().map(passageiros::get).toList();
            var opcoesComHistorico = carregadorOpcoes.incluirDestinosHistoricos(opcoes, selecionados);
            var detalhes = montadorDetalhes.montarPorAgendamento(selecionados, opcoesComHistorico);
            var nomesListas = carregadorOpcoes.nomesListas(opcoes, selecionados);
            var veiculos = agenda.veiculos().stream()
                    .collect(Collectors.toMap(VeiculoAbertoResponseDto::id, Function.identity()));
            var linhas = selecionados.stream().map(passageiro -> {
                var veiculo = passageiro.idVeiculoAberto() == null ? null : veiculos.get(passageiro.idVeiculoAberto());
                if (passageiro.idVeiculoAberto() != null && veiculo == null) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "O veículo do passageiro não foi encontrado nesta agenda.");
                }
                return new PassageiroRelatorioView(
                        passageiro, detalhes.get(passageiro.id()), nomesListas.get(passageiro.idLista()), veiculo);
            }).toList();
            var pdf = AgendamentoPdfService.gerarPassageiros(data, linhas);
            String nomeArquivo = "passageiros-" + data + ".pdf";
            UUID id = UUID.randomUUID();
            HttpSession sessao = request.getSession(true);
            synchronized (sessao) {
                var armazenados = pdfsDaSessao(sessao);
                armazenados.entrySet().removeIf(entry -> expirado(entry.getValue()));
                while (armazenados.size() >= MAX_PDFS_POR_SESSAO) {
                    armazenados.remove(armazenados.keySet().iterator().next());
                }
                armazenados.put(id, new PdfArmazenado(pdf, nomeArquivo, Instant.now()));
                sessao.setAttribute(PDFS_PASSAGEIROS, armazenados);
            }
            URI endereco = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/agendamentos/pdf/passageiros/{id}/{nomeArquivo}")
                    .buildAndExpand(id, nomeArquivo).toUri();
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .location(endereco)
                    .build();
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, mensagemErro.de(e), e);
        }
    }

    @GetMapping("/passageiros/{id}/{nomeArquivo}")
    public ResponseEntity<byte[]> visualizarPdfPassageiros(
            @PathVariable UUID id, @PathVariable String nomeArquivo, HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF não encontrado ou expirado.");
        }
        PdfArmazenado pdf;
        synchronized (sessao) {
            var armazenados = pdfsDaSessao(sessao);
            pdf = armazenados.get(id);
            if (pdf == null || expirado(pdf) || !pdf.nomeArquivo().equals(nomeArquivo)) {
                if (pdf != null && expirado(pdf)) {
                    armazenados.remove(id);
                    sessao.setAttribute(PDFS_PASSAGEIROS, armazenados);
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF não encontrado ou expirado.");
            }
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdf.nomeArquivo() + "\"")
                .body(pdf.conteudo());
    }

    private static PdfsDaSessao pdfsDaSessao(HttpSession sessao) {
        Object atributo = sessao.getAttribute(PDFS_PASSAGEIROS);
        return atributo instanceof PdfsDaSessao armazenados ? armazenados : new PdfsDaSessao();
    }

    private static boolean expirado(PdfArmazenado pdf) {
        return !pdf.criadoEm().plus(VALIDADE_PDF).isAfter(Instant.now());
    }

    private static final class PdfsDaSessao extends LinkedHashMap<UUID, PdfArmazenado> {
        private static final long serialVersionUID = 1L;
    }

    private record PdfArmazenado(byte[] conteudo, String nomeArquivo, Instant criadoEm) implements Serializable {
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
