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
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto.PessoaDetalhesResponseDto;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto.PessoaComHistoricoResponseDto;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.arquivar.PessoaCommandArquivar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.arquivar.PessoaCommandArquivarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.deletar.PessoaCommandDeletar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.deletar.PessoaCommandDeletarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.desarquivar.PessoaCommandDesarquivar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.desarquivar.PessoaCommandDesarquivarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar.PessoaCommandEditar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar.PessoaCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.salvar.PessoaCommandSalvar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.salvar.PessoaCommandSalvarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.comand.validar.PessoaCommandValidarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscar.PessoaQueryBuscar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscar.PessoaQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscarArquivado.PessoaQueryBuscarArquivado;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscarArquivado.PessoaQueryBuscarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.listar.PessoaQueryListar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.listar.PessoaQueryListarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.listarArquivado.PessoaQueryListarArquivado;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.listarArquivado.PessoaQueryListarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.historico.AgendamentoQueryListarHistoricoPessoa;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.historico.AgendamentoQueryListarHistoricoPessoaHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.prioridade.PrioridadeRepository;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
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

    private final PessoaCommandArquivarHandler arquivarHandler;
    private final PessoaCommandDesarquivarHandler desarquivarHandler;
    private final PessoaCommandDeletarHandler deletarHandler;
    private final PessoaCommandEditarHandler editarHandler;
    private final PessoaCommandSalvarHandler salvarHandler;
    private final PessoaCommandValidarHandler validarHandler;

    private final PessoaQueryBuscarHandler buscarHandler;
    private final PessoaQueryBuscarArquivadoHandler buscarArquivadoHandler;
    private final PessoaQueryListarHandler listarHandler;
    private final PessoaQueryListarArquivadoHandler listarArquivadoHandler;
    private final PrioridadeRepository prioridades;
    private final AgendamentoQueryListarHistoricoPessoaHandler historicoHandler;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid PessoaRequestDto request) {
        var id = salvarHandler.handler(new PessoaCommandSalvar(request.nome(), request.observacao(), request.especificacaoId(),
                request.endereco(), request.cpf(), request.telefone(), request.buscarNaResidencia()));
                
        return ResponseEntity.created(URI.create("/api/pessoa/%s".formatted(id))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(buscarHandler.handler(new PessoaQueryBuscar(id))));
    }

    @GetMapping("/{id}/arquivado")
    public ResponseEntity<PessoaResponseDto> buscarArquivado(@PathVariable UUID id) {

        return ResponseEntity.ok(toResponse(buscarArquivadoHandler.handler(new PessoaQueryBuscarArquivado(id))));
    }

    @GetMapping("/listar")
    public ResponseEntity<ResultadoPaginado<PessoaResponseDto>> listarAtivos(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = listarHandler.handler(new PessoaQueryListar(nome, pagina, tamanho));
        return ResponseEntity.ok(resultado.map(this::toResponse));
    }

    @GetMapping("/listar/arquivado")
    public ResponseEntity<ResultadoPaginado<PessoaResponseDto>> listarArquivados(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        var resultado = listarArquivadoHandler.handler(new PessoaQueryListarArquivado(nome, pagina, tamanho));
        return ResponseEntity.ok(resultado.map(this::toResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody @Valid PessoaRequestDto request) {
        editarHandler.handler(new PessoaCommandEditar(id, request.nome(), request.observacao(), request.especificacaoId(),
                request.endereco(), request.cpf(), request.telefone(), request.buscarNaResidencia()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable UUID id) {
        arquivarHandler.handler(new PessoaCommandArquivar(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Void> desarquivar(@PathVariable UUID id) {
        desarquivarHandler.handler(new PessoaCommandDesarquivar(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/validar")
    public ResponseEntity<Void> validar(@PathVariable UUID id) {
        validarHandler.handler(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarHandler.handler(new PessoaCommandDeletar(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detalhes-completo")
    public ResponseEntity<PessoaComHistoricoResponseDto> buscarDetalhesCompleto(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "") String destinoBusca,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        var pessoa = buscarHandler.handler(new PessoaQueryBuscar(id));
        var historicoViagens = historicoHandler.handler(
                new AgendamentoQueryListarHistoricoPessoa(id, destinoBusca, pagina, tamanho));
        
        return ResponseEntity.ok(new PessoaComHistoricoResponseDto(
                toResponseDetalhes(pessoa),
                historicoViagens
        ));
    }

    @GetMapping("/{id}/historico-viagens")
    public ResponseEntity<ResultadoPaginado<HistoricoViagemResponseDto>> buscarHistoricoViagens(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "") String destinoBusca,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        var resultado = historicoHandler.handler(
                new AgendamentoQueryListarHistoricoPessoa(id, destinoBusca, pagina, tamanho));
        return ResponseEntity.ok(resultado);
    }

    private PessoaResponseDto toResponse(Pessoa pessoa) {
        var prioridade = prioridades.findById(pessoa.getEspecificacao().getPrioridade())
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Prioridade não encontrada"));
        return new PessoaResponseDto(
                pessoa.getId(),
                pessoa.getNome().valor(),
                pessoa.getObservacao() == null ? null : pessoa.getObservacao().valor(),
                pessoa.getEspecificacao().getId(),
                pessoa.getEspecificacao().getNome(),
                prioridade.getNome(),
                prioridade.getId(),
                prioridade.getQuantidadeAcompanhantes(),
                pessoa.isBuscarNaResidencia(),
                pessoa.getQuantidadeAcompanhantes(),
                pessoa.isValidada(),
                pessoa.getDataArquivado(),
                pessoa.getEndereco() == null ? null : pessoa.getEndereco().valor(),
                pessoa.getCpf() == null ? null : pessoa.getCpf().valor(),
                pessoa.getTelefone().valor());
    }

    private PessoaDetalhesResponseDto toResponseDetalhes(Pessoa pessoa) {
        var prioridade = prioridades.findById(pessoa.getEspecificacao().getPrioridade())
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Prioridade não encontrada"));
        return new PessoaDetalhesResponseDto(
                pessoa.getId(),
                pessoa.getNome().valor(),
                pessoa.getObservacao() == null ? null : pessoa.getObservacao().valor(),
                pessoa.getEspecificacao().getId(),
                pessoa.getEspecificacao().getNome(),
                prioridade.getNome(),
                prioridade.getId(),
                prioridade.getQuantidadeAcompanhantes(),
                pessoa.isBuscarNaResidencia(),
                pessoa.getQuantidadeAcompanhantes(),
                pessoa.isValidada(),
                pessoa.getDataArquivado() == null ? null : pessoa.getDataArquivado().toString(),
                pessoa.getEndereco() == null ? null : pessoa.getEndereco().valor(),
                pessoa.getCpf() == null ? null : pessoa.getCpf().valor(),
                pessoa.getTelefone().valor());
    }
}
