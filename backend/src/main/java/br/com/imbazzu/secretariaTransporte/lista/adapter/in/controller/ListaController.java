package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller;
//package br.com.imbazzu.secretariaTransporte.operacao.lista;
//
//import br.com.imbazzu.secretariaTransporte.operacao.lista.dto.ListaRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.dto.ListaResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.dto.PassageiroRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.dto.PassageiroResponseDto;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/listas")
//@RequiredArgsConstructor
//public class ListaController {
//
//    private final ListaService service;
//
//    // -------------------------------------------------------------------------
//    // ListaDoDia
//    // -------------------------------------------------------------------------
//
//    /**
//     * POST /listas
//     * Cadastra uma nova lista.
//     */
//    @PostMapping
//    public ResponseEntity<ListaResponseDto> salvar(
//            @RequestBody @Valid ListaRequestDto dto) {
//
//        var response = service.salvar(dto);
//        return ResponseEntity.ok().body(response);
//    }
//
//    /**
//     * GET /listas?data=yyyy-MM-dd
//     * Busca todas as listas de uma data.
//     */
//    @GetMapping
//    public ResponseEntity<List<ListaResponseDto>> buscarPorData(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
//
//        return ResponseEntity.ok(service.buscarListaPorData(data));
//    }
//
//    /**
//     * PUT /listas/{id}
//     * Edita os dados de uma lista existente.
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<ListaResponseDto> editar(
//            @PathVariable UUID id,
//            @RequestBody @Valid ListaRequestDto dto) {
//
//        return ResponseEntity.ok(service.editar(id, dto));
//    }
//
//    /**
//     * DELETE /listas/{id}
//     * Remove uma lista.
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
//        service.deletarLista(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // -------------------------------------------------------------------------
//    // Passageiros da lista
//    // -------------------------------------------------------------------------
//
//    /**
//     * POST /listas/{id}/passageiros
//     * Adiciona um passageiro a uma lista.
//     */
//    @PostMapping("/{id}/passageiros")
//    public ResponseEntity<ListaResponseDto> adicionarPassageiro(
//            @PathVariable UUID id,
//            @RequestBody @Valid PassageiroRequestDto dto) {
//
//        var response = service.adicionarPassageiro(id, dto);
//        return ResponseEntity.ok().body(response);
//    }
//
//    /**
//     * GET /listas/{id}/passageiros
//     * ListaDoDia todos os passageiros de uma lista.
//     */
//    @GetMapping("/{id}/passageiros")
//    public ResponseEntity<List<PassageiroResponseDto>> buscarPassageiros(@PathVariable UUID id) {
//        return ResponseEntity.ok(service.buscarTodosPassageiros(id));
//    }
//
//    /**
//     * GET /listas/{id}/passageiros/sem-carro
//     * ListaDoDia passageiros da lista do tipo CARRO que ainda não têm viagem atribuída.
//     */
//    @GetMapping("/{id}/passageiros/sem-carro")
//    public ResponseEntity<List<PassageiroResponseDto>> buscarPassageirosSemCarro(@PathVariable UUID id) {
//        return ResponseEntity.ok(service.buscarTodosPassageirosSemCarro(id));
//    }
//
//    /**
//     * DELETE /listas/{idLista}/passageiros/{idPassageiro}
//     * Remove um passageiro de uma lista.
//     */
//    @DeleteMapping("/{idLista}/passageiros/{idPassageiro}")
//    public ResponseEntity<Void> deletarPassageiro(
//            @PathVariable UUID idLista,
//            @PathVariable UUID idPassageiro) {
//
//        service.deletarPassageiro(idLista, idPassageiro);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * PATCH /listas/passageiros/{idPassageiro}/mover?idListaDestino={id}
//     * Move um passageiro para outra lista da mesma data.
//     */
//    @PatchMapping("/passageiros/{idPassageiro}/mover")
//    public ResponseEntity<ListaResponseDto> moverPassageiro(
//            @PathVariable UUID idPassageiro,
//            @RequestParam UUID idListaDestino) {
//
//        return ResponseEntity.ok(service.moverPassageiro(idPassageiro, idListaDestino));
//    }
//}