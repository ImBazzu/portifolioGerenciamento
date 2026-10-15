//package br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista;
//
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaRequest;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/tipos-lista")
//@RequiredArgsConstructor
//public class TipoListaController {
//
//    private final TipoListaService service;
//
//    @GetMapping
//    public ResponseEntity<List<TipoListaResponse>> buscarTodos() {
//        return ResponseEntity.ok(service.buscarTodos());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<TipoListaResponse> buscarPorId(@PathVariable UUID id) {
//        return ResponseEntity.ok(service.buscarPorId(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<TipoListaResponse> salvar(@Valid @RequestBody TipoListaRequest dto) {
//        var salvo = service.salvar(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<TipoListaResponse> editar(@PathVariable UUID id,
//                                                    @Valid @RequestBody TipoListaRequest dto) {
//        return ResponseEntity.ok(service.editar(id, dto));
//    }
//
//    @PatchMapping("/{id}/acesso")
//    public ResponseEntity<Void> editarAcesso(@PathVariable UUID id, @RequestParam boolean ativo) {
//        service.editarAcesso(id, ativo);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
//        service.deletar(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{tipoListaId}/destinos/{destinoId}")
//    public ResponseEntity<TipoListaResponse> adicionarDestino(@PathVariable UUID tipoListaId,
//                                                              @PathVariable UUID destinoId) {
//        return ResponseEntity.ok(service.adicionarDestino(tipoListaId, destinoId));
//    }
//
//    @DeleteMapping("/{tipoListaId}/destinos/{destinoId}")
//    public ResponseEntity<TipoListaResponse> removerDestino(@PathVariable UUID tipoListaId,
//                                                            @PathVariable UUID destinoId) {
//        return ResponseEntity.ok(service.removerDestino(tipoListaId, destinoId));
//    }
//}