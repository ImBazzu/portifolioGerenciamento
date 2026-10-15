package br.com.imbazzu.secretariaTransporte.localidades.adapters.in;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto.DestinoRequestDto;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto.DestinoResponseDto;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.arquivar.DestinoCommandArquivar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.arquivar.DestinoCommandArquivarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.desarquivar.DestinoCommandDesarquivar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.desarquivar.DestinoCommandDesarquivarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.editar.DestinoCommandEditar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.editar.DestinoCommandEditarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.salvar.DestinoCommandSalvar;
import br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.salvar.DestinoCommandSalvarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.buscar.DestinoQueryBuscar;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.buscar.DestinoQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.buscarArquivado.DestinoQueryBuscarArquivado;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.buscarArquivado.DestinoQueryBuscarArquivadoHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listar.DestinoQueryListar;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listar.DestinoQueryListarHandler;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listarArquivado.DestinoQueryListarArquivado;
import br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listarArquivado.DestinoQueryListarArquivadoHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Destinos", description = "Operações de cadastro, edição, consulta e arquivamento de destinos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/destino")
@RequiredArgsConstructor
public class DestinoController {

        private final DestinoCommandArquivarHandler arquivarHandler;
        private final DestinoCommandDesarquivarHandler desarquivarHandler;
        private final DestinoCommandEditarHandler editarHandler;
        private final DestinoCommandSalvarHandler salvarHandler;

        private final DestinoQueryListarHandler listarHandler;
        private final DestinoQueryListarArquivadoHandler listarArquivadoHandler;
        private final DestinoQueryBuscarHandler buscarHandler;
        private final DestinoQueryBuscarArquivadoHandler buscarArquivadoHandler;

        @Operation(summary = "Cadastra um novo destino", description = "Cria um novo destino associado a uma cidade.")
        // @PreAuthorize("hasAnyRole('ADMIN')")
        @PostMapping()
        public ResponseEntity<Void> salvarDestino(
                        @Valid @RequestBody DestinoRequestDto dto) {

                var idDestino = salvarHandler.handler(new DestinoCommandSalvar(
                                dto.idCidade(),
                                dto.nome()));
                var uri = URI.create("/destino/%s".formatted(idDestino));
                return ResponseEntity.created(uri).build();
        }

        @Operation(summary = "Edita um destino", description = "Atualiza o nome de um destino existente.")
        // @PreAuthorize("hasAnyRole('ADMIN')")
        @PutMapping("/{idDestino}")
        public ResponseEntity<Void> editarDestino(
                        @PathVariable UUID idDestino,
                        @RequestParam String nome) {
                editarHandler.handler(new DestinoCommandEditar(
                                idDestino,
                                nome));
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Arquiva um destino", description = "Arquiva um destino existente, tornando-o indisponível para utilização normal.")
        // @PreAuthorize("hasAnyRole('ADMIN')")
        @PatchMapping("/arquivar/{idDestino}")
        public ResponseEntity<Void> arquivarDestino(
                        @PathVariable UUID idDestino) {
                arquivarHandler.handler(new DestinoCommandArquivar(idDestino));

                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Desarquiva um destino", description = "Reativa um destino que foi previamente arquivado.")
        // @PreAuthorize("hasAnyRole('ADMIN')")
        @PatchMapping("/desarquivar/{idDestino}")
        public ResponseEntity<Void> desarquivarDestino(
                        @PathVariable UUID idDestino) {
                desarquivarHandler.handler(new DestinoCommandDesarquivar(idDestino));

                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Lista os destinos de uma cidade", description = """
                        Retorna uma lista paginada de destinos pertencentes a uma cidade.
                        É possível filtrar os destinos pelo nome e controlar a paginação
                        através dos parâmetros de página e tamanho.
                        """)
        // @PreAuthorize("hasAnyRole('USER','ADMIN')")
        @GetMapping("/listar/{idCidade}")
        public ResponseEntity<ResultadoPaginado<DestinoResponseDto>> listar(
                        @PathVariable UUID idCidade,
                        @RequestParam(name = "nome", defaultValue = "") String nome,
                        @PageableDefault(size = 20, sort = "nome") Pageable pageable) {

                var ordenacao = pageable.getSort().stream()
                                .map(order -> new OrdemPaginacao(order.getProperty(), order.isAscending()))
                                .toList();
                var result = listarHandler.handler(new DestinoQueryListar(idCidade, nome, pageable.getPageNumber(),
                                pageable.getPageSize(), ordenacao));
                var retorno = result.map(destino -> new DestinoResponseDto(
                                destino.id(),
                                destino.nome(),
                                destino.qtdPassageiros()));
                return ResponseEntity.ok(retorno);
        }

        @Operation(summary = "Lista os destinos de uma cidade", description = """
                        Retorna uma lista paginada de destinos pertencentes a uma cidade.
                        É possível filtrar os destinos pelo nome e controlar a paginação
                        através dos parâmetros de página e tamanho.
                        """)
        // @PreAuthorize("hasAnyRole('USER','ADMIN')")
        @GetMapping("/listarArquivados/{idCidade}")
        public ResponseEntity<ResultadoPaginado<DestinoResponseDto>> listarArquivados(
                        @PathVariable UUID idCidade,
                        @RequestParam(name = "nome", defaultValue = "") String nome,
                        @PageableDefault(size = 20, sort = "nome") Pageable pageable) {

                var ordenacao = pageable.getSort().stream()
                                .map(order -> new OrdemPaginacao(order.getProperty(), order.isAscending()))
                                .toList();
                var result = listarArquivadoHandler
                                .handler(new DestinoQueryListarArquivado(idCidade, nome, pageable.getPageNumber(),
                                                pageable.getPageSize(), ordenacao));
                var retorno = result.map(destino -> new DestinoResponseDto(
                                destino.id(),
                                destino.nome(),
                                destino.qtdPassageiros()));
                return ResponseEntity.ok(retorno);
        }

        @GetMapping("/{idDestino}")
        public ResponseEntity<DestinoResponseDto> buscarDestino(
                        @PathVariable UUID idDestino) {
                var destino = buscarHandler.handler(new DestinoQueryBuscar(idDestino));
                var retorno = new DestinoResponseDto(
                                destino.id(),
                                destino.nome(),
                                destino.qtdPassageiros());
                return ResponseEntity.ok(retorno);
        }

        @GetMapping("/arquivado/{idDestino}")
        public ResponseEntity<DestinoResponseDto> buscarDestinoArquivado(
                        @PathVariable UUID idDestino) {
                var destino = buscarArquivadoHandler.handler(new DestinoQueryBuscarArquivado(idDestino));
                var retorno = new DestinoResponseDto(
                                destino.id(),
                                destino.nome(),
                                destino.qtdPassageiros());
                return ResponseEntity.ok(retorno);
        }

}
