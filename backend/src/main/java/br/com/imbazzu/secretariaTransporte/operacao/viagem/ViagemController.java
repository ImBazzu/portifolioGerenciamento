//package br.com.imbazzu.secretariaTransporte.operacao.viagem;
//
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemPorPeriodoResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/viagem")
//@RequiredArgsConstructor
//public class ViagemController {
//
//    private final ViagemService viagemService;
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ViagemResponseDto> criar(
//            @RequestBody ViagemRequestDto dto) {
//
//        var viagem = viagemService.salvar(dto);
//
//        return ResponseEntity.ok(viagem);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ViagemResponseDto> editar(
//            @PathVariable UUID id,
//            @RequestBody ViagemRequestDto dto) {
//
//        return ResponseEntity.ok(
//                viagemService.editar(id, dto)
//        );
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<ViagemResponseDto> buscarPorId(
//            @PathVariable UUID id) {
//
//        return ResponseEntity.ok(
//                viagemService.buscarPorId(id)
//        );
//    }
//
//    @GetMapping
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<ViagemPorPeriodoResponseDto> buscarPorData(
//            @RequestParam LocalDate data) {
//
//        return ResponseEntity.ok(
//                viagemService.buscarViagemSeparadoPorHorario(data)
//        );
//    }
//
//    @PutMapping("/{idViagem}/{idPassageiro}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ViagemResponseDto> moverPassageiro(
//            @PathVariable UUID idViagem,
//            @PathVariable UUID idPassageiro) {
//
//        var viagem = viagemService.moverPassageiro(
//                idViagem,
//                idPassageiro
//        );
//
//        return ResponseEntity.ok((viagem));
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deletar(
//            @PathVariable UUID id) {
//
//        viagemService.deletarViagem(id);
//
//        return ResponseEntity.noContent().build();
//    }
//}