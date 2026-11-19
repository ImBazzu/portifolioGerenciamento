package br.com.imbazzu.secretariaTransporte.relatorio.adpter.in.controller;

import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.*;
import br.com.imbazzu.secretariaTransporte.relatorio.core.domain.FiltroRelatorio;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {
    
    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/agendamentos")
    public ResponseEntity<EstatisticasAgendamentosDTO> obterEstatisticasAgendamentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) List<Long> idsPessoa,
            @RequestParam(required = false) List<Long> idsVeiculo,
            @RequestParam(required = false) List<Long> idsDestino,
            @RequestParam(required = false) List<Long> idsEspecificacao,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanho) {
        
        validar(dataInicio, dataFim, pagina, tamanho, idsPessoa, idsVeiculo, idsDestino, idsEspecificacao);
        FiltroRelatorio filtro = new FiltroRelatorio(dataInicio, dataFim, idsPessoa, idsVeiculo, idsDestino, idsEspecificacao, pagina, tamanho);
        return ResponseEntity.ok(relatorioService.obterEstatisticasAgendamentos(filtro));
    }

    @GetMapping("/pessoas")
    public ResponseEntity<EstatisticasPessoasDTO> obterEstatisticasPessoas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) List<Long> idsPessoa,
            @RequestParam(required = false) List<Long> idsEspecificacao,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanho) {
        
        validar(dataInicio, dataFim, pagina, tamanho, idsPessoa, idsEspecificacao);
        FiltroRelatorio filtro = new FiltroRelatorio(dataInicio, dataFim, idsPessoa, null, null, idsEspecificacao, pagina, tamanho);
        return ResponseEntity.ok(relatorioService.obterEstatisticasPessoas(filtro));
    }

    @GetMapping("/veiculos")
    public ResponseEntity<EstatisticasVeiculosDTO> obterEstatisticasVeiculos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) List<Long> idsVeiculo,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanho) {
        
        validar(dataInicio, dataFim, pagina, tamanho, idsVeiculo);
        FiltroRelatorio filtro = new FiltroRelatorio(dataInicio, dataFim, null, idsVeiculo, null, null, pagina, tamanho);
        return ResponseEntity.ok(relatorioService.obterEstatisticasVeiculos(filtro));
    }

    @GetMapping("/operacional")
    public ResponseEntity<EstatisticasOperacionaisDTO> obterEstatisticasOperacionais(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) List<Long> idsDestino,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanho) {
        
        validar(dataInicio, dataFim, pagina, tamanho, idsDestino);
        FiltroRelatorio filtro = new FiltroRelatorio(dataInicio, dataFim, null, null, idsDestino, null, pagina, tamanho);
        return ResponseEntity.ok(relatorioService.obterEstatisticasOperacionais(filtro));
    }

    @SafeVarargs
    private final void validar(LocalDate inicio, LocalDate fim, int pagina, int tamanho, List<Long>... ids) {
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inicial posterior à data final");
        }
        if (pagina != 0 || tamanho != 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginação não disponível para estes relatórios");
        }
        for (List<Long> filtro : ids) {
            if (filtro != null && !filtro.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtro por identificador ainda não disponível");
            }
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> erroDeConsulta(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).body(Map.of("mensagem", erro.getReason()));
    }
}
