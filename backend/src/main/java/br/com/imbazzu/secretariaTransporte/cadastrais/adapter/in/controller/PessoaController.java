package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper.PessoaControllerMapper;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaRequestDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaResponseDto;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
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

@Tag( name = "Pessoas", description = "Operações de cadastro, consulta, edição e gerenciamento de pessoas" )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("paciente")
public class PessoaController {

    private final PessoaApplicationPorta application;

    @Operation( summary = "Lista pessoas",
            description = "Retorna uma lista paginada de pessoas filtrada opcionalmente pelo nome" )
    @GetMapping
   // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResultadoPaginado<PessoaResponseDto>> buscarListaPorNome(
            @RequestParam(name = "nome", defaultValue = "") String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pagina) {
        var result = application.listarPorNome(nome, pagina.getPageNumber(), pagina.getPageSize());
        var response = result.map(PessoaControllerMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @Operation( summary = "Busca pessoa por ID",
            description = "Retorna os dados de uma pessoa a partir do seu identificador" )
    @GetMapping("{id}")
    //@PreAuthorize("hasRole('USER')")
    public ResponseEntity<PessoaResponseDto> buscarPorId(@PathVariable UUID id) {
        var result = application.buscarPorId(id);
        return ResponseEntity.ok(PessoaControllerMapper.toResponse(result));
    }


    @Operation( summary = "Edita uma pessoa",
            description = "Atualiza os dados cadastrais de uma pessoa existente" )
    @PutMapping("{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> editar(@PathVariable UUID id,
            @Valid @RequestBody PessoaRequestDto dto) {
        application.editar(id, new PessoaApplicationPorta.PessoaSalvarInputDto(
                dto.nome(),dto.cpf(),dto.idCondicao(),dto.telefone(),dto.endereco(),dto.observacao()
        ));
        var result = application.buscarPorId(id);
        var response =  PessoaControllerMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @Operation( summary = "Cadastra uma pessoa",
            description = "Cria uma nova pessoa e retorna os dados cadastrados" )

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> salvar(@RequestBody @Valid PessoaRequestDto dto) {
        var pessoaSalvarInput = PessoaControllerMapper.toInputDto(dto);
        var idPessoa = application.salvar(pessoaSalvarInput);
        var result = application.buscarPorId(idPessoa);
        var response =   PessoaControllerMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @Operation( summary = "Arquiva uma pessoa",
            description = "Arquiva uma pessoa, deixando-a inativa para as operações que consideram apenas registros ativos" )
    @PutMapping("/{idPessoa}/arquivar")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> arquivar(@PathVariable UUID idPessoa){
        application.arquivar(idPessoa);
        return ResponseEntity.ok().build();
    }

    @Operation( summary = "Desarquiva uma pessoa",
            description = "Reativa uma pessoa previamente arquivada" )
    @PutMapping("/{idPessoa}/desarquivar")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desarquivar(@PathVariable UUID idPessoa){
        application.desarquivar(idPessoa);
        return ResponseEntity.ok().build();
    }
}
