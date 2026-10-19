package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller;
//package br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao;
//
//
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.ConjuntoRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.ConjuntoResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.RegraViagemRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.RegraViagemResponseDto;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Set;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/regras")
//@RequiredArgsConstructor
//public class RegraViagemController {
//
//    private final RegraViagemService service;
//
//    // -------------------------------------------------------------------------
//    // RegraViagem
//    // -------------------------------------------------------------------------
//
//    /**
//     * POST /regras/inicializar
//     * Inicializa a regra global. Deve ser chamado uma única vez.
//     */
//    @PostMapping("/inicializar")
//    public ResponseEntity<RegraViagemResponseDto> inicializar(
//            @RequestBody @Valid RegraViagemRequestDto dto) {
//        return ResponseEntity.ok(service.inicializar(dto));
//    }
//
//    /**
//     * GET /regras
//     * Retorna a configuração atual.
//     */
//    @GetMapping
//    public ResponseEntity<RegraViagemResponseDto> buscar() {
//        return ResponseEntity.ok(service.buscarPorId());
//    }
//
//    /**
//     * PUT /regras
//     * Atualiza capacidade máxima e tolerância de horário.
//     */
//    @PutMapping
//    public ResponseEntity<RegraViagemResponseDto> atualizar(
//            @RequestBody @Valid RegraViagemRequestDto dto) {
//        return ResponseEntity.ok(service.atualizar(dto));
//    }
//
//    // -------------------------------------------------------------------------
//    // Conjuntos de cidades
//    // -------------------------------------------------------------------------
//
//    /**
//     * POST /regras/conjuntos
//     * Cria um novo conjunto de cidades.
//     */
//    @PostMapping("/conjuntos")
//    public ResponseEntity<RegraViagemResponseDto> adicionarConjunto(
//            @RequestBody @Valid ConjuntoRequestDto dto) {
//        return ResponseEntity.ok(service.adicionarConjunto(dto));
//    }
//
//    /**
//     * DELETE /regras/conjuntos/{idConjunto}
//     * Remove um conjunto de cidades.
//     */
//    @DeleteMapping("/conjuntos/{idConjunto}")
//    public ResponseEntity<Void> removerConjunto(@PathVariable UUID idConjunto) {
//        service.removerConjunto(idConjunto);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * POST /regras/conjuntos/{idConjunto}/cidades
//     * Adiciona uma cidade a um conjunto existente.
//     */
//    @PostMapping("/conjuntos/{idConjunto}/cidades")
//    public ResponseEntity<ConjuntoResponseDto> adicionarCidade(
//            @PathVariable UUID idConjunto,
//            @RequestBody @Valid Set<UUID> dto) {
//        return ResponseEntity.ok(service.adicionarCidade(idConjunto, dto));
//    }
//
//    /**
//     * DELETE /regras/conjuntos/{idConjunto}/cidades/{idCidade}
//     * Remove uma cidade de um conjunto.
//     */
//    @DeleteMapping("/conjuntos/{idConjunto}/cidades/{idCidade}")
//    public ResponseEntity<ConjuntoResponseDto> removerCidade(
//            @PathVariable UUID idConjunto,
//            @PathVariable UUID idCidade) {
//        return ResponseEntity.ok(service.removerCidade(idConjunto, idCidade));
//    }
//}
