package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto.PessoaRequestDto;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto.PessoaResponseDto;
import br.com.imbazzu.secretariaTransporte.pessoa.application.PessoaApplication;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Pessoas", description = "Cadastro e gerenciamento de pessoas")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("pessoa")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaApplication application;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid PessoaRequestDto request) {
        var id = application.criar(request.nome(), request.observacao(), request.prioridade(),
                request.endereco(), request.cpf(), request.telefone());
        return ResponseEntity.created(URI.create("/api/pessoa/%s".formatted(id))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(application.buscar(id)));
    }

    @GetMapping("/listar")
    public ResponseEntity<ResultadoPaginado<PessoaResponseDto>> listarAtivos(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(application.listar(nome, false, pagina, tamanho).map(PessoaController::toResponse));
    }

    @GetMapping("/listar/arquivado")
    public ResponseEntity<ResultadoPaginado<PessoaResponseDto>> listarArquivados(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(application.listar(nome, true, pagina, tamanho).map(PessoaController::toResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody @Valid PessoaRequestDto request) {
        application.editar(id, request.nome(), request.observacao(), request.prioridade(),
                request.endereco(), request.cpf(), request.telefone());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable UUID id) {
        application.arquivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Void> desarquivar(@PathVariable UUID id) {
        application.desarquivar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        application.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private static PessoaResponseDto toResponse(Pessoa pessoa) {
        return new PessoaResponseDto(
                pessoa.getId(),
                pessoa.getNome().valor(),
                pessoa.getObservacao() == null ? null : pessoa.getObservacao().valor(),
                pessoa.getPrioridade(),
                pessoa.getDataArquivado(),
                pessoa.getEndereco().valor(),
                pessoa.getCpf().valor(),
                pessoa.getTelefone().valor());
    }
}
