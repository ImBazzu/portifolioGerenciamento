package br.com.imbazzu.frontSpring.localidade.adpter.out.api;

import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeArquivadoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.excecoes.LocalidadeDuplicadaException;
import br.com.imbazzu.frontSpring.localidade.core.excecoes.LocalidadeNaoEncontradaException;
import br.com.imbazzu.frontSpring.localidade.core.porta.out.LocalidadeApiPorta;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;
import java.util.function.Supplier;

@Service
public class LocalidadeApi implements LocalidadeApiPorta {

    private static final String CIDADE_PATH = "/cidade";
    private static final String DESTINO_PATH = "/destino";
    private final RestClient api;
    private final RetryComRefreshPorta retryComRefresh;

    public LocalidadeApi(RestClientProtegido api, RetryComRefreshPorta retryComRefresh) {
        this.api = api.get();
        this.retryComRefresh = retryComRefresh;
    }

    @Override
    public void salvarCidade(CidadeRequestDto cidade) {
        api.post()
                .uri(CIDADE_PATH)
                .body(cidade)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> { throw new LocalidadeDuplicadaException(null); })
                .toBodilessEntity();
    }

    @Override
    public void editarCidade(UUID id, CidadeRequestDto cidade) {
        api.put()
                .uri(CIDADE_PATH + "/" + id)
                .body(cidade)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> { throw new LocalidadeDuplicadaException(id); })
                .toBodilessEntity();
    }

    @Override
    public void arquivarCidade(UUID id) {
        api.patch()
                .uri(CIDADE_PATH + "/" + id + "/arquivar")
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .toBodilessEntity();
    }

    @Override
    public void desarquivarCidade(UUID id) {
        api.patch()
                .uri(CIDADE_PATH + "/" + id + "/desarquivar")
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .toBodilessEntity();
    }

    @Override
    public void deletarCidade(UUID id) {
        api.delete()
                .uri(CIDADE_PATH + "/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .toBodilessEntity();
    }

    @Override
    public ResultadoPaginado<CidadeResponseDto> listarCidades(String nome, int pagina, int tamanho) {
        return executarComRefresh(() -> api.get()
                .uri(uri -> uri.path(CIDADE_PATH)
                        .queryParam("nome", nome)
                        .queryParam("page", pagina)
                        .queryParam("size", tamanho)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {}));
    }

    @Override
    public ResultadoPaginado<CidadeArquivadoResponseDto> listarCidadesArquivadas(String nome, int pagina, int tamanho) {
        return api.get()
                .uri(uri -> uri.path(CIDADE_PATH + "/listar/arquivado")
                        .queryParam("nome", nome)
                        .queryParam("page", pagina)
                        .queryParam("size", tamanho)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public CidadeResponseDto buscarCidade(UUID id) {
        return api.get()
                .uri(CIDADE_PATH + "/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .body(CidadeResponseDto.class);
    }

    @Override
    public CidadeArquivadoResponseDto buscarCidadeArquivada(UUID id) {
        return api.get()
                .uri(CIDADE_PATH + "/" + id + "/arquivado")
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .body(CidadeArquivadoResponseDto.class);
    }

    @Override
    public void salvarDestino(DestinoRequestDto destino) {
        api.post()
                .uri(DESTINO_PATH)
                .body(destino)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> { throw new LocalidadeDuplicadaException(null); })
                .toBodilessEntity();
    }

    @Override
    public void editarDestino(UUID id, String nome) {
        api.put()
                .uri(uri -> uri.path(DESTINO_PATH + "/" + id)
                        .queryParam("nome", nome)
                        .build())
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> { throw new LocalidadeDuplicadaException(id); })
                .toBodilessEntity();
    }

    @Override
    public void arquivarDestino(UUID id) {
        api.patch()
                .uri(DESTINO_PATH + "/arquivar/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .toBodilessEntity();
    }

    @Override
    public void desarquivarDestino(UUID id) {
        api.patch()
                .uri(DESTINO_PATH + "/desarquivar/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .toBodilessEntity();
    }

    @Override
    public ResultadoPaginado<DestinoResponseDto> listarDestinos(UUID idCidade, String nome, int pagina, int tamanho) {
        return listarDestinos(DESTINO_PATH + "/listar/" + idCidade, nome, pagina, tamanho);
    }

    @Override
    public ResultadoPaginado<DestinoResponseDto> listarDestinosArquivados(
            UUID idCidade, String nome, int pagina, int tamanho) {
        return listarDestinos(DESTINO_PATH + "/listarArquivados/" + idCidade, nome, pagina, tamanho);
    }

    private ResultadoPaginado<DestinoResponseDto> listarDestinos(String path, String nome, int pagina, int tamanho) {
        return api.get()
                .uri(uri -> uri.path(path)
                        .queryParam("nome", nome)
                        .queryParam("page", pagina)
                        .queryParam("size", tamanho)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public DestinoResponseDto buscarDestino(UUID id) {
        return buscarDestino(DESTINO_PATH + "/" + id, id);
    }

    @Override
    public DestinoResponseDto buscarDestinoArquivado(UUID id) {
        return buscarDestino(DESTINO_PATH + "/arquivado/" + id, id);
    }

    private DestinoResponseDto buscarDestino(String path, UUID id) {
        return api.get()
                .uri(path)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> { throw new LocalidadeNaoEncontradaException(id); })
                .body(DestinoResponseDto.class);
    }

    private <T> T executarComRefresh(Supplier<T> chamadaApi) {
        return retryComRefresh.executar(chamadaApi);
    }
}
