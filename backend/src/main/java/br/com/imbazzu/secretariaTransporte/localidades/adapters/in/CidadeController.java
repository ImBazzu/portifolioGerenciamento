package br.com.imbazzu.secretariaTransporte.localidades.adapters.in;
// package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller;

// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper.CidadeControllerMapper;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.CidadeResponseDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.CidadeRequestDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.CidadeApplicationPorta;
// import
// br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.web.PageableDefault;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import java.util.UUID;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/cidade")
// @Tag(name = "Cidades", description = "Operações de cadastro, consulta e
// arquivamento de cidades")
// @SecurityRequirement(name = "bearerAuth")

// public class CidadeController {

// private final CidadeApplicationPorta application;

// // ============================================================
// // CIDADE
// // ============================================================
// @Operation(summary = "Busca uma cidade por ID", description = "Retorna os
// dados de uma cidade cadastrada, incluindo suas informações relacionadas.")
// // @PreAuthorize("hasAnyRole('USER','ADMIN')")
// @GetMapping("/{id}")
// public ResponseEntity<CidadeResponseDto> buscarPorId(
// @PathVariable UUID id) {
// var result = application.buscarPorId(id);

// var response = CidadeControllerMapper.paraResponse(result);

// return ResponseEntity.ok(response);
// }

// @Operation(summary = "Lista cidades", description = """
// Retorna uma lista paginada de cidades.

// É possível filtrar pelo nome e controlar a paginação
// através dos parâmetros de página e tamanho.
// """)
// // @PreAuthorize("hasAnyRole('USER','ADMIN')")
// @GetMapping
// public ResponseEntity<ResultadoPaginado<CidadeResponseDto>> buscarPorNome(
// @RequestParam(name = "nome", defaultValue = "") String nome,
// @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
// var result = application.buscarCidadeComDestinoPorNome(
// nome,
// pageable.getPageNumber(),
// pageable.getPageSize());

// var response = result.map(
// CidadeControllerMapper::paraResponse);

// return ResponseEntity.ok(response);
// }

// @Operation(summary = "Cadastra uma nova cidade", description = "Cria uma nova
// cidade no sistema.")
// // @PreAuthorize("hasAnyRole('ADMIN')")
// @PostMapping
// public ResponseEntity<CidadeResponseDto> salvarCidade(
// @RequestBody @Valid CidadeRequestDto request) {
// var command = CidadeControllerMapper.paraSalvarInput(request);

// var idCidadeSalva = application.salvar(command);

// var result = application.buscarPorId(idCidadeSalva);

// var response = CidadeControllerMapper.paraResponse(result);

// return ResponseEntity.ok(response);
// }

// @Operation(summary = "Edita uma cidade", description = "Atualiza os dados de
// uma cidade existente.")
// // @PreAuthorize("hasAnyRole('ADMIN')")
// @PutMapping("/{id}")
// public ResponseEntity<CidadeResponseDto> editarCidade(
// @PathVariable UUID id,
// @RequestBody @Valid CidadeRequestDto request) {
// var command = CidadeControllerMapper.paraSalvarInput(request);

// application.editar(id, command);

// var result = application.buscarPorId(id);
// var response = CidadeControllerMapper.paraResponse(result);

// return ResponseEntity.ok(response);
// }

// @Operation(summary = "Arquiva uma cidade", description = "Arquiva uma cidade,
// deixando-a indisponível para utilização normal no sistema.")
// // @PreAuthorize("hasAnyRole('ADMIN')")
// @PatchMapping("/{id}/arquivar")
// public ResponseEntity<Void> arquivarCidade(
// @PathVariable UUID id) {
// application.arquivar(id);

// return ResponseEntity.noContent().build();
// }

// @Operation(summary = "Desarquiva uma cidade", description = "Reativa uma
// cidade previamente arquivada.")
// // @PreAuthorize("hasAnyRole('ADMIN')")
// @PatchMapping("/{id}/desarquivar")
// public ResponseEntity<Void> desarquivarCidade(
// @PathVariable UUID id) {
// application.desarquivar(id);

// return ResponseEntity.noContent().build();
// }
// }