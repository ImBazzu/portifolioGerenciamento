// package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller;

// import
// br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.MotoristaRequestDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.MotoristaResponseDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.MotoristaApplicationPorta;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper.MotoristaControllerMapper;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.web.PageableDefault;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// import java.net.URI;
// import java.util.UUID;

// /**
// * Responsável pela interação do navegador e o servidor
// */
// @Tag( name = "Motoristas", description = "Operações de cadastro, consulta,
// edição e gerenciamento de motoristas" )
// @SecurityRequirement(name = "bearerAuth")

// @RestController
// @RequestMapping("motorista")
// @RequiredArgsConstructor
// public class MotoristaController {

// private final MotoristaApplicationPorta application;

// /**
// * Responsável por receber a requisição para a procura pelo motorista com base
// no nome
// *
// * @param nome nome do motorista procurado
// * @param pagina pagina procurado
// * @return retorna para o navegador a lista do motorista
// */
// @Operation( summary = "Lista motoristas",
// description = """
// Retorna uma lista paginada de motoristas. É possível filtrar os motoristas
// pelo nome e controlar a paginação através dos parâmetros de página e tamanho.
// """ )
// @GetMapping
// @PreAuthorize("hasAnyRole('USER','ADMIN')")
// public ResponseEntity<ResultadoPaginado<MotoristaResponseDto>> buscarPorNome(
// @RequestParam(name = "nome", defaultValue = "") String nome,
// @PageableDefault(size = 20,sort = "nome") Pageable pagina) {
// //procura o motorista pelo nome

// var result = application.buscarPorNome(nome, pagina.getPageNumber(),
// pagina.getPageSize());
// var response = result.map(MotoristaControllerMapper::paraResponseDto);
// //Retorna a lista para o navegador
// return ResponseEntity.ok(response);
// }

// /**
// * Buscar o motorista pelo o identificador dele
// *
// * @param id identificador
// * @return motorista encontrado
// */
// @Operation( summary = "Busca um motorista por ID",
// description = "Retorna os dados de um motorista através do seu
// identificador." )
// @GetMapping("{id}")
// @PreAuthorize("hasRole('USER')")
// public ResponseEntity<MotoristaResponseDto> buscarPorId(@PathVariable UUID
// id) {
// //motorista encontrado
// var result = application.buscarPorId(id);
// var response = MotoristaControllerMapper.paraResponseDto(result);
// //Retorna para o navegador
// return ResponseEntity.ok(response);
// }

// /**
// * Delete o motorista usando o identificador
// *
// * @param id identificador
// * @return nada
// */
// @Operation( summary = "Arquiva um motorista",
// description = "Arquiva um motorista existente, tornando-o indisponível para
// utilização normal." )
// @PatchMapping("{id}/arquivar")
// @PreAuthorize("hasRole('ADMIN')")
// public ResponseEntity<Void> arquivar(@PathVariable UUID id) {
// //Deleta o motorista
// application.arquivar(id);
// //Retorna o código que deu tudo certo ao deletar
// return ResponseEntity.noContent().build();
// }

// /**
// * Delete o motorista usando o identificador
// *
// * @param id identificador
// * @return nada
// */
// @Operation( summary = "Desarquiva um motorista",
// description = "Reativa um motorista que foi previamente arquivado." )
// @PatchMapping("{id}/desarquivar")
// @PreAuthorize("hasRole('ADMIN')")
// public ResponseEntity<Void> desarquivar(@PathVariable UUID id) {
// //Deleta o motorista
// application.desarquivar(id);
// //Retorna o código que deu tudo certo ao deletar
// return ResponseEntity.noContent().build();
// }

// /**
// * Salva um novo motorista no banco de dados
// * @param dto contendo as informações do novo motorista
// * @return Dto do novo motorista cadastrado no banco
// */
// @Operation( summary = "Cadastra um novo motorista",
// description = "Cria um novo motorista no sistema." )
// @PostMapping
// @PreAuthorize("hasRole('ADMIN')")
// public ResponseEntity<MotoristaResponseDto> salvar(@RequestBody @Valid
// MotoristaRequestDto dto) {
// //Salvando o motorista no banco de dados
// var dtoCommand = MotoristaControllerMapper.paraCommandSalvarDto(dto);
// UUID idSalvo =application.salvar(dtoCommand);
// var result = application.buscarPorId(idSalvo);
// var response = MotoristaControllerMapper.paraResponseDto(result);
// //Uri do para caso precise realizar um get para a procura do motorista salvo
// URI uri = ServletUriComponentsBuilder
// .fromCurrentServletMapping()
// .path("/motorista/{id}")
// .buildAndExpand(idSalvo)
// .toUri();
// //Retorno no banco
// return ResponseEntity.created(uri).body(response);
// }

// /**
// * Busca o motorista usando o Id e o edita
// * @param id identificador do motorista
// * @param dto novos dados do motorista
// * @return retorna o motorista editado
// */
// @Operation( summary = "Edita um motorista",
// description = "Atualiza os dados de um motorista existente." )
// @PutMapping("{id}")
// @PreAuthorize("hasRole('ADMIN')")
// public ResponseEntity<MotoristaResponseDto> editar(@PathVariable UUID id,
// @RequestBody @Valid MotoristaRequestDto dto) {
// //Motorista editado
// var dtoCommand = MotoristaControllerMapper.paraCommandSalvarDto(dto);
// application.editar(id, dtoCommand);
// var result = application.buscarPorId(id);
// var response = MotoristaControllerMapper.paraResponseDto(result);
// //Retorna o motorista editado e ok
// return ResponseEntity.ok(response);
// }

// }
