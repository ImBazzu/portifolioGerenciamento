package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper.PessoaCondicaoMapper;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaCondicaoApplicationPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaCondicaoRequestDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaCondicaoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;



@Tag( name = "Justificativas", description = "Operações de cadastro, consulta, edição e gerenciamento de justificativas de condições de pessoa" )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("justificativa")
public class PessoaCondicaoController {

    private final PessoaCondicaoApplicationPorta service;

    @Operation( summary = "Busca uma justificativa por ID",
            description = "Retorna os dados de uma justificativa através do seu identificador." )
    @GetMapping({"{id}"})
   // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PessoaCondicaoResponseDto> buscarPorId(@PathVariable UUID id){
        var result = service.buscarPorId(id);
        var response = PessoaCondicaoMapper.converterDto(result);
        return ResponseEntity.ok().body(response);
    }

    @Operation( summary = "Lista justificativas",
            description = """
            Retorna uma lista paginada de justificativas. É possível filtrar as justificativas
            pelo nome e controlar a paginação através dos parâmetros de página e tamanho.""" )
    @GetMapping
    //@PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ResultadoPaginado<PessoaCondicaoResponseDto>>buscarTodos(
            @RequestParam(name = "procurar", defaultValue = "") String procura,
            @PageableDefault(size=20,sort = "name")
            Pageable pageable){
        var result = service.buscarPorNome(procura,pageable.getPageNumber(),pageable.getPageSize());
        var response = result.map(PessoaCondicaoMapper::converterDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation( summary = "Cadastra uma nova justificativa",
            description = "Cria uma nova justificativa de condição de pessoa." )
    @PostMapping()
    //@PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PessoaCondicaoResponseDto>criar(
            @Valid @RequestParam PessoaCondicaoRequestDto dto){
        var pessoaSalvarInput = PessoaCondicaoMapper.paraInputSalvar(dto);
        var idPessoaCondicao = service.salvar(pessoaSalvarInput);
        var result = service.buscarPorId(idPessoaCondicao);
        var response = PessoaCondicaoMapper.converterDto(result);
        return ResponseEntity.ok().body(response);
    }

    @Operation( summary = "Desabilita uma justificativa",
            description = "Desabilita uma justificativa existente." )
    @DeleteMapping("{id}")
  //  @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> desabilitar(UUID id){
        service.desarquivar(id);
        return ResponseEntity.ok().build();
    }

    @Operation( summary = "Edita uma justificativa",
            description = "Atualiza os dados de uma justificativa existente." )
    @PutMapping("{id}")
  //  @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PessoaCondicaoResponseDto> editar(
            @PathVariable UUID id, @Valid @RequestBody PessoaCondicaoRequestDto dto){
        var inputDto = PessoaCondicaoMapper.paraInputSalvar(dto);
        service.editar(id, inputDto);
        var result = service.buscarPorId(id);
        var response =  PessoaCondicaoMapper.converterDto(result);
        return ResponseEntity.ok().body(response);
    }

    @Operation( summary = "Habilita uma justificativa",
            description = "Habilita novamente uma justificativa que estava arquivada ou desabilitada." )
    @PutMapping("{id}/habilitar")
   // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> arquivar(UUID id){
        service.arquivar(id);
        return ResponseEntity.ok().build();
    }
}
