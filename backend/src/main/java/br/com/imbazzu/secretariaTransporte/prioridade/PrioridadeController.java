package br.com.imbazzu.secretariaTransporte.prioridade;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/prioridades")
@RequiredArgsConstructor
public class PrioridadeController {
    private final PrioridadeRepository repository;

    public record Request(@NotBlank @Size(max = 100) String nome,
                          @NotNull Boolean buscarNaResidencia,
                          @NotNull @Min(0) Integer quantidadeAcompanhantes) {}

    public record Response(UUID id, String nome, boolean buscarNaResidencia, int quantidadeAcompanhantes,
                           boolean arquivada, long quantidadeEspecificacoes) {
        static Response de(PrioridadeJpa prioridade, long quantidadeEspecificacoes) {
            return new Response(prioridade.getId(), prioridade.getNome(), prioridade.isBuscarNaResidencia(),
                    prioridade.getQuantidadeAcompanhantes(), prioridade.getDataArquivado() != null,
                    quantidadeEspecificacoes);
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<Response> listar() {
        return repository.listarComTotalEspecificacoes().stream()
                .map(item -> Response.de(item.getPrioridade(), item.getTotal())).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public Response buscar(@PathVariable UUID id) {
        return Response.de(encontrar(id), repository.contarEspecificacoes(id));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Response> criar(@RequestBody @Valid Request request) {
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new EntidadeDuplicadaException("Já existe uma prioridade com este nome");
        }
        var prioridade = new PrioridadeJpa();
        prioridade.setId(UUID.randomUUID());
        atualizar(prioridade, request, nome);
        repository.saveAndFlush(prioridade);
        return ResponseEntity.created(URI.create("/api/prioridades/" + prioridade.getId()))
                .body(Response.de(prioridade, 0));
    }

    @PutMapping("/{id}")
    @Transactional
    public Response editar(@PathVariable UUID id, @RequestBody @Valid Request request) {
        var prioridade = encontrar(id);
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new EntidadeDuplicadaException("Já existe uma prioridade com este nome");
        }
        atualizar(prioridade, request, nome);
        return Response.de(repository.saveAndFlush(prioridade), repository.contarEspecificacoes(id));
    }

    @PatchMapping("/{id}/arquivar")
    @Transactional
    public void arquivar(@PathVariable UUID id) {
        var prioridade = encontrar(id);
        if (prioridade.getDataArquivado() == null) {
            prioridade.setDataArquivado(LocalDate.now());
        }
    }

    @PatchMapping("/{id}/desarquivar")
    @Transactional
    public void desarquivar(@PathVariable UUID id) {
        encontrar(id).setDataArquivado(null);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable UUID id) {
        var prioridade = encontrar(id);
        if (repository.possuiEspecificacoes(id) || repository.possuiRegras(id)) {
            throw new OperacaoNaoPermitidaException(
                    "Remova os vínculos com especificações e regras de lista antes de excluir a prioridade.");
        }
        try {
            repository.delete(prioridade);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new OperacaoNaoPermitidaException(
                    "Não é possível excluir uma prioridade vinculada a especificações ou regras de lista.");
        }
    }

    private PrioridadeJpa encontrar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Prioridade não encontrada: " + id));
    }

    private static void atualizar(PrioridadeJpa prioridade, Request request, String nome) {
        prioridade.setNome(nome);
        prioridade.setBuscarNaResidencia(request.buscarNaResidencia());
        prioridade.setQuantidadeAcompanhantes(request.quantidadeAcompanhantes());
    }
}
