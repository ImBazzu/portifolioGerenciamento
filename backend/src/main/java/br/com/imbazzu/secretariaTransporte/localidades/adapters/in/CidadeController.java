package br.com.imbazzu.secretariaTransporte.localidades.adapters.in;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto.CidadeRequestDto;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto.CidadeResponseDto;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.arquivar.CidadeCommandArquivar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.arquivar.CidadeCommandArquivarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.deletar.CidadeCommandDeletarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.desarquivar.CidadeCommandDesarquivar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.desarquivar.CidadeCommandDesarquivarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.editar.CidadeCommandEditar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.editar.CidadeCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.salvar.CidadeCommandSalvar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.salvar.CidadeCommandSalvarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.buscar.CidadeQueryBuscar;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.buscar.CidadeQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.buscarArquivado.CidadeQueryBuscarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listar.CidadeQueryListar;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listar.CidadeQueryListarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listarArquivado.CidadeQueryListarArquivadoHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cidade")
@Tag(name = "Cidades", description = "Operações de cadastro, consulta e arquivamento de cidades")
@SecurityRequirement(name = "bearerAuth")

public class CidadeController {

private final CidadeCommandArquivarHandler arquivarHandler;
private final CidadeCommandDesarquivarHandler desarquivarHandler;
private final CidadeCommandDeletarHandler deletarHandler;
private final CidadeCommandEditarHandler editarHandler;
private final CidadeCommandSalvarHandler salvarHandler;

private final CidadeQueryBuscarHandler buscarHandler;
private final CidadeQueryBuscarArquivadoHandler buscarArquivadoHandler;
private final CidadeQueryListarHandler listarHandler;
private final CidadeQueryListarArquivadoHandler listarArquivadoHandler;


// ============================================================
// CIDADE
// ============================================================
@Operation(summary = "Busca uma cidade por ID", description = "Retorna os dados de uma cidade cadastrada, incluindo suas informações relacionadas.")
// @PreAuthorize("hasAnyRole('USER','ADMIN')")
@GetMapping("/{id}")
public ResponseEntity<CidadeResponseDto> buscarPorId(
@PathVariable UUID id) {
var result = buscarHandler.handler(new CidadeQueryBuscar(id));

var response = new CidadeResponseDto(result.id(), 
result.nome(), 
result.estado(), 
result.tempoViagem(),
result.totalDestinos());

return ResponseEntity.ok(response);
}

@Operation(summary = "Lista cidades", description = """
Retorna uma lista paginada de cidades.

É possível filtrar pelo nome e controlar a paginação
através dos parâmetros de página e tamanho.
""")
// @PreAuthorize("hasAnyRole('USER','ADMIN')")
@GetMapping
public ResponseEntity<ResultadoPaginado<CidadeResponseDto>> buscarPorNome(
@RequestParam(name = "nome", defaultValue = "") String nome,
@PageableDefault(size = 20, sort = "nome") Pageable pageable) {

var result = listarHandler.handler(new CidadeQueryListar(nome,  pageable.getPageNumber(), pageable.getPageSize()));

ResultadoPaginado<CidadeResponseDto> response = result.map(c-> new CidadeResponseDto(c.id(), c.nome(), c.estado(), c.tempoViagem(), c.totalDestinos())
);

return ResponseEntity.ok(response);
}

@Operation(summary = "Cadastra uma nova cidade", description = "Cria uma nova cidade no sistema.")
// @PreAuthorize("hasAnyRole('ADMIN')")
@PostMapping
public ResponseEntity<CidadeResponseDto> salvarCidade(
@RequestBody @Valid CidadeRequestDto request) {
var idSalvo = salvarHandler.handler(
    new CidadeCommandSalvar(
        request.nome(),
        request.estado(),
        request.horas(),
        request.minutos()
    ));

    var uri = URI.create("/cidade/%s".formatted(idSalvo));

return ResponseEntity.created(uri).build();
}

@Operation(summary = "Edita uma cidade", description = "Atualiza os dados de uma cidade existente.")
// @PreAuthorize("hasAnyRole('ADMIN')")
@PutMapping("/{id}")
public ResponseEntity<CidadeResponseDto> editarCidade(
@PathVariable UUID id,
@RequestBody @Valid CidadeRequestDto request) {
editarHandler.handler(
    new CidadeCommandEditar(
        id,
        request.nome(),
        request.estado(),
        request.horas(),
        request.minutos()
    ));


return ResponseEntity.noContent().build();
}

@Operation(summary = "Arquiva uma cidade", description = "Arquiva uma cidade, deixando-a indisponível para utilização normal no sistema.")
// @PreAuthorize("hasAnyRole('ADMIN')")
@PatchMapping("/{id}/arquivar")
public ResponseEntity<Void> arquivarCidade(
@PathVariable UUID id) {
arquivarHandler.handler(new CidadeCommandArquivar(id));
return ResponseEntity.noContent().build();
}

@Operation(summary = "Desarquiva uma cidade", description = "Reativa uma cidade previamente arquivada.")
// @PreAuthorize("hasAnyRole('ADMIN')")
@PatchMapping("/{id}/desarquivar")
public ResponseEntity<Void> desarquivarCidade(
@PathVariable UUID id) {
desarquivarHandler.handler(new CidadeCommandDesarquivar(id));

return ResponseEntity.noContent().build();
}
}