package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller;

import java.net.URI;
import java.time.Duration;
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
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.RegraCadastroRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.RegraSeparacaoRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaRegrasRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaResponseDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaResumoResponseDto;
import br.com.imbazzu.secretariaTransporte.lista.application.command.arquivar.TipoListaCommandArquivar;
import br.com.imbazzu.secretariaTransporte.lista.application.command.arquivar.TipoListaCommandArquivarHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras.TipoListaCommandAtualizarRegras;
import br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras.TipoListaCommandAtualizarRegrasHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.command.deletar.TipoListaCommandDeletar;
import br.com.imbazzu.secretariaTransporte.lista.application.command.deletar.TipoListaCommandDeletarHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.command.desarquivar.TipoListaCommandDesarquivar;
import br.com.imbazzu.secretariaTransporte.lista.application.command.desarquivar.TipoListaCommandDesarquivarHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.command.editar.TipoListaCommandEditar;
import br.com.imbazzu.secretariaTransporte.lista.application.command.editar.TipoListaCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.command.salvar.TipoListaCommandSalvar;
import br.com.imbazzu.secretariaTransporte.lista.application.command.salvar.TipoListaCommandSalvarHandler;
import br.com.imbazzu.secretariaTransporte.lista.adpter.model.RegraCadastroInput;
import br.com.imbazzu.secretariaTransporte.lista.adpter.model.RegraSeparacaoInput;

import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscar;
import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.query.buscar.ListaQueryBuscarResultado;
import br.com.imbazzu.secretariaTransporte.lista.application.query.listarArquivado.ListaQueryListarArquivado;
import br.com.imbazzu.secretariaTransporte.lista.application.query.listarArquivado.ListaQueryListarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.lista.application.query.listarAtivos.ListaQueryListarAtivos;
import br.com.imbazzu.secretariaTransporte.lista.application.query.listarAtivos.ListaQueryListarAtivosHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Listas", description = "Cadastro e gerenciamento de tipos de lista e suas regras")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("lista")
@RequiredArgsConstructor
public class TipoListaController {

    // Commands
    private final TipoListaCommandSalvarHandler salvarHandler;
    private final TipoListaCommandEditarHandler editarHandler;
    private final TipoListaCommandAtualizarRegrasHandler atualizarRegrasHandler;
    private final TipoListaCommandArquivarHandler arquivarHandler;
    private final TipoListaCommandDesarquivarHandler desarquivarHandler;
    private final TipoListaCommandDeletarHandler deletarHandler;

    // Queries
    private final ListaQueryBuscarHandler buscarHandler;
    private final ListaQueryListarAtivosHandler listarAtivosHandler;
    private final ListaQueryListarArquivadoHandler listarArquivadoHandler;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid TipoListaRequestDto request) {
        var id = salvarHandler.handler(new TipoListaCommandSalvar(request.nome(), request.descricao()));
        return ResponseEntity.created(URI.create("/api/lista/%s".formatted(id))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoListaResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(buscarHandler.handler(new ListaQueryBuscar(id))));
    }

    @GetMapping("/listar")
    public ResponseEntity<ResultadoPaginado<TipoListaResumoResponseDto>> listarAtivos(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = listarAtivosHandler.handler(new ListaQueryListarAtivos(nome, pagina, tamanho))
                .map(lista -> new TipoListaResumoResponseDto(
                        lista.id(), lista.nome(), lista.descricao(), lista.totalRegras(), lista.totalCidades(),
                        false, null));
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listar/arquivado")
    public ResponseEntity<ResultadoPaginado<TipoListaResumoResponseDto>> listarArquivados(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = listarArquivadoHandler.handler(new ListaQueryListarArquivado(nome, pagina, tamanho))
                .map(lista -> new TipoListaResumoResponseDto(
                        lista.id(), lista.nome(), lista.descricao(), lista.totalRegras(), lista.totalCidades(),
                        true, lista.dataArquivado()));
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody @Valid TipoListaRequestDto request) {
        editarHandler.handler(new TipoListaCommandEditar(id, request.nome(), request.descricao()));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/regras")
    public ResponseEntity<Void> atualizarRegras(
            @PathVariable UUID id,
            @RequestBody @Valid TipoListaRegrasRequestDto request) {
        var regrasCadastro = request.regrasCadastro().stream()
                .map(TipoListaController::toInput)
                .toList();
        var regrasSeparacao = request.regrasSeparacao().stream()
                .map(TipoListaController::toInput)
                .toList();
        atualizarRegrasHandler.handler(new TipoListaCommandAtualizarRegras(id, regrasCadastro, regrasSeparacao));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable UUID id) {
        arquivarHandler.handler(new TipoListaCommandArquivar(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Void> desarquivar(@PathVariable UUID id) {
        desarquivarHandler.handler(new TipoListaCommandDesarquivar(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarHandler.handler(new TipoListaCommandDeletar(id));
        return ResponseEntity.noContent().build();
    }

    private static RegraCadastroInput toInput(RegraCadastroRequestDto request) {
        return new RegraCadastroInput(
                request.idsVeiculos(),
                request.idsCidades(),
                request.prioridades(),
                request.inicioHorario(),
                request.finalHorario(),
                request.obrigacaoAnexo(),
                request.descricao());
    }

    private static RegraSeparacaoInput toInput(RegraSeparacaoRequestDto request) {
        return new RegraSeparacaoInput(
                request.idTipoVeiculo(),
                Duration.ofSeconds(request.tempoToleranciaSegundos(), request.tempoToleranciaNanos()));
    }

    private static TipoListaResponseDto toResponse(ListaQueryBuscarResultado resultado) {
        var regrasCadastro = resultado.regrasCadastro().stream()
                .map(regra -> new TipoListaResponseDto.RegraCadastroResponseDto(
                        regra.id(),
                        regra.veiculos().stream()
                                .map(v -> new TipoListaResponseDto.VeiculoResponseDto(v.id(), v.nome()))
                                .toList(),
                        regra.cidades().stream()
                                .map(c -> new TipoListaResponseDto.CidadeResponseDto(c.id(), c.nome()))
                                .toList(),
                        regra.prioridades(),
                        regra.inicioHorario(),
                        regra.finalHorario(),
                        regra.obrigacaoAnexo(),
                        regra.descricao()))
                .toList();
        var regrasSeparacao = resultado.regrasSeparacao().stream()
                .map(regra -> new TipoListaResponseDto.RegraSeparacaoResponseDto(
                        regra.id(),
                        regra.nome(),
                        regra.tempoTolerancia()))
                .toList();
        return new TipoListaResponseDto(
                resultado.id(),
                resultado.nome(),
                resultado.descricao(),
                resultado.dataArquivado(),
                regrasCadastro,
                regrasSeparacao);
    }
}
