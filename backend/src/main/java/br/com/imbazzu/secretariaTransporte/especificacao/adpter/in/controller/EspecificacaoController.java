package br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller;

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
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller.dto.EspecificacaoRequestDto;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.in.controller.dto.EspecificacaoResponseDto;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.arquivar.EspecificacaoCommandArquivar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.arquivar.EspecificacaoCommandArquivarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.deletar.EspecificacaoCommandDeletar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.deletar.EspecificacaoCommandDeletarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.desarquivar.EspecificacaoCommandDesarquivar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.desarquivar.EspecificacaoCommandDesarquivarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar.EspecificacaoCommandEditar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar.EspecificacaoCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar.EspecificacaoCommandSalvar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar.EspecificacaoCommandSalvarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.buscar.EspecificacaoQueryBuscar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.buscar.EspecificacaoQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.listar.EspecificacaoQueryListar;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.listar.EspecificacaoQueryListarHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.listarArquivado.EspecificacaoQueryListarArquivado;
import br.com.imbazzu.secretariaTransporte.especificacao.application.query.listarArquivado.EspecificacaoQueryListarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import br.com.imbazzu.secretariaTransporte.prioridade.PrioridadeRepository;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("especificacao")
@RequiredArgsConstructor
public class EspecificacaoController {

    private final EspecificacaoCommandSalvarHandler salvarHandler;
    private final EspecificacaoCommandEditarHandler editarHandler;
    private final EspecificacaoCommandArquivarHandler arquivarHandler;
    private final EspecificacaoCommandDesarquivarHandler desarquivarHandler;
    private final EspecificacaoCommandDeletarHandler deletarHandler;

    private final EspecificacaoQueryBuscarHandler buscarHandler;
    private final EspecificacaoQueryListarHandler listarHandler;
    private final EspecificacaoQueryListarArquivadoHandler listarArquivadoHandler;
    private final PrioridadeRepository prioridades;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid EspecificacaoRequestDto request) {
        var id = salvarHandler.handler(new EspecificacaoCommandSalvar(
                request.nome(), request.prioridade(), request.observacao()));

        return ResponseEntity.created(URI.create("/api/especificacao/%s".formatted(id))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecificacaoResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(buscarHandler.handler(new EspecificacaoQueryBuscar(id))));
    }

    @GetMapping("/{id}/arquivado")
    public ResponseEntity<EspecificacaoResponseDto> buscarArquivada(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(buscarHandler.buscarArquivada(new EspecificacaoQueryBuscar(id))));
    }

    @GetMapping("/listar")
    public ResponseEntity<ResultadoPaginado<EspecificacaoResponseDto>> listarAtivos(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = listarHandler.handler(new EspecificacaoQueryListar(nome, pagina, tamanho));
        return ResponseEntity.ok(resultado.map(this::toResponse));
    }

    @GetMapping("/listar/arquivado")
    public ResponseEntity<ResultadoPaginado<EspecificacaoResponseDto>> listarArquivados(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = listarArquivadoHandler.handler(
                new EspecificacaoQueryListarArquivado(nome, pagina, tamanho));
        return ResponseEntity.ok(resultado.map(this::toResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody @Valid EspecificacaoRequestDto request) {
        editarHandler.handler(new EspecificacaoCommandEditar(id, request.nome(), request.prioridade(), request.observacao()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable UUID id) {
        arquivarHandler.handler(new EspecificacaoCommandArquivar(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Void> desarquivar(@PathVariable UUID id) {
        desarquivarHandler.handler(new EspecificacaoCommandDesarquivar(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarHandler.handler(new EspecificacaoCommandDeletar(id));
        return ResponseEntity.noContent().build();
    }

    private EspecificacaoResponseDto toResponse(Especificacao especificacao) {
        var prioridade = prioridades.findById(especificacao.getPrioridade())
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Prioridade não encontrada: " + especificacao.getPrioridade()));
        return new EspecificacaoResponseDto(
                especificacao.getId(),
                especificacao.getNome(),
                especificacao.getPrioridade(),
                prioridade.getNome(),
                especificacao.getObservacao(),
                especificacao.getDataArquivado());
    }
}
