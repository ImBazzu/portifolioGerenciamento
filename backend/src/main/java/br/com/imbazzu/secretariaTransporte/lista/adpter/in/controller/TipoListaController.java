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
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.RegraCadastroRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.RegraSeparacaoRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaRegrasRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaRequestDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaResponseDto;
import br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto.TipoListaResumoResponseDto;
import br.com.imbazzu.secretariaTransporte.lista.application.TipoListaApplication;
import br.com.imbazzu.secretariaTransporte.lista.application.model.RegraCadastroInput;
import br.com.imbazzu.secretariaTransporte.lista.application.model.RegraSeparacaoInput;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
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

    private final TipoListaApplication application;
    private final CidadeQueryPorta cidadeQueryPorta;
private final TipoVeiculoQueryPorta tipoVeiculoQueryPorta; // novo campo, ao lado do cidadeQueryPorta


    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid TipoListaRequestDto request) {
        var id = application.criar(request.nome(), request.descricao());
        return ResponseEntity.created(URI.create("/api/lista/%s".formatted(id))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoListaResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(application.buscar(id)));
    }

    @GetMapping("/listar")
    public ResponseEntity<ResultadoPaginado<TipoListaResumoResponseDto>> listarAtivos(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = application.listar(nome, false, pagina, tamanho)
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
        var resultado = application.listar(nome, true, pagina, tamanho)
                .map(lista -> new TipoListaResumoResponseDto(
                        lista.id(), lista.nome(), lista.descricao(), lista.totalRegras(), lista.totalCidades(),
                        true, lista.dataArquivado()));
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody @Valid TipoListaRequestDto request) {
        application.editar(id, request.nome(), request.descricao());
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
        application.atualizarRegras(id, regrasCadastro, regrasSeparacao);
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

    private TipoListaResponseDto toResponse(TipoLista lista) {
        var regrasCadastro = lista.getListaRegrasCadastro().stream()
                .map(regra -> new TipoListaResponseDto.RegraCadastroResponseDto(
                        regra.getId(),
                        regra.getIdsVeiculos().stream()
                                .map(id -> new TipoListaResponseDto.VeiculoResponseDto(id, nomeVeiculo(id)))
                                .toList(),
                        regra.listaCidades().stream()
                                .map(id->new TipoListaResponseDto.CidadeResponseDto(id, nomeCidade(id)))
                                .toList(),
                        regra.getPrioridades(),
                        regra.getInicioHorario(),
                        regra.getFinalHorario(),
                        regra.isObrigacaoAnexo(),
                        regra.getDescricao() == null ? null : regra.getDescricao().valor()))
                .toList();
        var regrasSeparacao = lista.getListaRegraSeparacao().stream()
                .map(regra -> new TipoListaResponseDto.RegraSeparacaoResponseDto(
                        regra.getId(),
                        regra.getIdTipoVeiculo(),
                        regra.getTempoTolerancia().getSeconds(),
                        regra.getTempoTolerancia().getNano()))
                .toList();
        return new TipoListaResponseDto(
                lista.getId(),
                lista.getNome().valor(),
                lista.getDescricao() == null ? null : lista.getDescricao().valor(),
                lista.getDataArquivado(),
                regrasCadastro,
                regrasSeparacao);
    }

    private String nomeCidade(UUID idCidade) {
        return cidadeQueryPorta.buscar(idCidade)
                .map(CidadeQueryPorta.CidadeOutput::nome)
                .or(() -> cidadeQueryPorta.buscarArquivado(idCidade)
                        .map(CidadeQueryPorta.CidadeOutputArquivado::nome))
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Cidade não encontrada: " + idCidade));
    }


private String nomeVeiculo(UUID idTipoVeiculo) {
    return tipoVeiculoQueryPorta.buscarAtivo(idTipoVeiculo)
            .map(TipoVeiculoQueryPorta.TipoVeiculoOutput::nome)
            .or(() -> tipoVeiculoQueryPorta.buscarArquivado(idTipoVeiculo)
                    .map(TipoVeiculoQueryPorta.TipoVeiculoOutputArquivado::nome))
            .orElseThrow(() -> new EntidadeNaoEncontradoException(
                    "Tipo de veículo não encontrado: " + idTipoVeiculo));
}
}
