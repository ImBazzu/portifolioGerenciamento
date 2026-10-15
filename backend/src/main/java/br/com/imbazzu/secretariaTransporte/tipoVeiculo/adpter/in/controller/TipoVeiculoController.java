package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller;

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
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.arquivar.TipoVeiculoCommandArquivar;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.arquivar.TipoVeiculoCommandArquivarHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar.TipoVeiculoCommandDeletar;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar.TipoVeiculoCommandDeletarHandle;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.desarquivar.TipoVeiculoCommandDesarquivar;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.desarquivar.TipoVeiculoCommandDesarquivarHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.editar.TipoVeiculoCommandEditar;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.editar.TipoVeiculoCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.salvar.TipoVeiculoCommandSalvar;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.salvar.TipoVeiculoCommandSalvarHandle;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscar.TipoVeiculoQueryBuscar;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscar.TipoVeiculoQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscarArquivado.TipoVeiculoQueryBuscarArquivado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscarArquivado.TipoVeiculoQueryBuscarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.historico.TipoVeiculoQueryHistorico;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.historico.TipoVeiculoQueryHistoricoHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.listarArquivados.TipoVeiculoQueryListarArquivado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.listarArquivados.TipoVeiculoQueryListarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.listarAtivos.TipoVeiculoQueryListarAtivoHandler;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.listarAtivos.TipoVeiculoQueryListarAtivo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Responsável pela interação do navegador e o servidor
 */

@Tag(name = "Veículos", description = "Operações de cadastro, consulta, edição e gerenciamento de veículos")
@SecurityRequirement(name = "bearerAuth")

@RestController
@RequestMapping("veiculo")
@RequiredArgsConstructor
public class TipoVeiculoController {

    // Command
    private final TipoVeiculoCommandArquivarHandler arquivarHandler;
    private final TipoVeiculoCommandDesarquivarHandler desarquivarHandler;
    private final TipoVeiculoCommandEditarHandler editarHandler;
    private final TipoVeiculoCommandSalvarHandle salvarHandle;
    private final TipoVeiculoCommandDeletarHandle deletarHandle;

    // Query
    private final TipoVeiculoQueryBuscarHandler buscarHandler;
    private final TipoVeiculoQueryBuscarArquivadoHandler buscarArquivadoHandler;
    private final TipoVeiculoQueryListarAtivoHandler listarAtivoHandler;
    private final TipoVeiculoQueryListarArquivadoHandler listarArquivadoHandler;
    private final TipoVeiculoQueryHistoricoHandler listarHistorico;

    // Command
    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable UUID id) {

        arquivarHandler.handler(new TipoVeiculoCommandArquivar(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Void> desarquivar(@PathVariable UUID id) {
        desarquivarHandler.handler(new TipoVeiculoCommandDesarquivar(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id,
            @RequestBody @Valid TipoVeiculoRequestDto dto) {
        editarHandler.handler(new TipoVeiculoCommandEditar(id, dto.nome(), dto.capacidade()));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarHandle.handler(new TipoVeiculoCommandDeletar(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid TipoVeiculoRequestDto dto) {
        var idNovoVeiculo = salvarHandle.handler(new TipoVeiculoCommandSalvar(dto.nome(), dto.capacidade()));
        var uri = URI.create("/tipoVeiculo/%s".formatted(idNovoVeiculo));
        return ResponseEntity.created(uri).build();
    }

    // Query
    @GetMapping("/{id}/ativo")
    public ResponseEntity<TipoVeiculoResponseDto> buscarPorId(@PathVariable UUID id) {

        var result = buscarHandler.handler(new TipoVeiculoQueryBuscar(id));
        var response = new TipoVeiculoResponseDto(result.id(), result.nome(), result.capacidadde());
        return ResponseEntity.ok(response);
    }

    // Query
    @GetMapping("/{id}/arquivado")
    public ResponseEntity<TipoVeiculoArquivadoResponseDto> buscarArquivadoPorId(@PathVariable UUID id) {

        var result = buscarArquivadoHandler.handler(new TipoVeiculoQueryBuscarArquivado(id));
        var response = new TipoVeiculoArquivadoResponseDto(
                result.id(),
                result.nome(),
                result.capacidade(),
                result.dataArquivado());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<ResultadoPaginado<TipoVeiculoResponseDto>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var result = listarAtivoHandler.handler(new TipoVeiculoQueryListarAtivo(nome, pagina, tamanho));
        var response = result.map(v -> new TipoVeiculoResponseDto(
                v.id(), v.nome(), v.capacidadde()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listar/arquivado")
    public ResponseEntity<ResultadoPaginado<TipoVeiculoArquivadoResponseDto>> listarArquivado(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var result = listarArquivadoHandler.handler(new TipoVeiculoQueryListarArquivado(nome, pagina, tamanho));
        var response = result
                .map(v -> new TipoVeiculoArquivadoResponseDto(v.id(), v.nome(), v.capacidade(), v.dataArquivado()));
        System.err.println(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<ResultadoPaginado<TipoVeiculoHistoricoResponseDto>> listarHistorico(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var result = listarHistorico.handler(new TipoVeiculoQueryHistorico(id, pagina, tamanho));
        var response = result
                .map(v -> new TipoVeiculoHistoricoResponseDto(v.id(), v.data(), v.quantidadeVeiculos(),
                        v.destinos(), v.totalPassageiro()));
        return ResponseEntity.ok(response);
    }

}
