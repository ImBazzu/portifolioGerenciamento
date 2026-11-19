package br.com.imbazzu.frontSpring.especificacao.adpter.out.api;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.excecoes.EspecificacaoDadosInvalidosException;
import br.com.imbazzu.frontSpring.especificacao.core.excecoes.EspecificacaoDuplicadaException;
import br.com.imbazzu.frontSpring.especificacao.core.excecoes.EspecificacaoNaoEncontradaException;
import br.com.imbazzu.frontSpring.especificacao.core.porta.out.EspecificacaoApiPorta;

@Service
public class EspecificacaoApi implements EspecificacaoApiPorta {
    private static final String PATH_API = "/especificacao";

    private final RestClient api;
    private final RetryComRefreshPorta retryComRefresh;

    public EspecificacaoApi(RestClientProtegido api, RetryComRefreshPorta retryComRefresh) {
        this.api = api.get();
        this.retryComRefresh = retryComRefresh;
    }

    @Override
    public ResultadoPaginado<EspecificacaoResponseDto> listar(String nome, boolean arquivadas, int pagina, int tamanho) {
        return executarComRefresh(() -> api.get()
                .uri(uri -> uri.path(PATH_API + (arquivadas ? "/listar/arquivado" : "/listar"))
                        .queryParam("nome", nome)
                        .queryParam("pagina", pagina)
                        .queryParam("tamanho", tamanho)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
    }

    @Override
    public EspecificacaoResponseDto buscar(UUID id) {
        return api.get()
                .uri(PATH_API + "/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new EspecificacaoNaoEncontradaException(id);
                        })
                .body(EspecificacaoResponseDto.class);
    }

    @Override
    public EspecificacaoResponseDto buscarArquivado(UUID id) {
        return api.get()
                .uri("%s/%s/arquivado".formatted(PATH_API, id))
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new EspecificacaoNaoEncontradaException(id);
                        })
                .body(EspecificacaoResponseDto.class);
    }

    @Override
    public UUID salvar(EspecificacaoRequestDto especificacao) {
        var respostaCriacao = api.post()
                .uri(PATH_API)
                .body(especificacao)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> {
                            throw new EspecificacaoDuplicadaException();
                        })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST) || status.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (request, response) -> {
                            String mensagem = new String(response.getBody().readAllBytes());
                            throw new EspecificacaoDadosInvalidosException();
                        })
                .toBodilessEntity();
        
        var location = respostaCriacao.getHeaders().getLocation();
        if (location == null) {
            throw new IllegalStateException("A API não retornou o endereço da especificação criada.");
        }
        var path = location.getPath();
        return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
    }

    @Override
    public void editar(UUID id, EspecificacaoRequestDto especificacao) {
        api.put()
                .uri(PATH_API + "/" + id)
                .body(especificacao)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new EspecificacaoNaoEncontradaException(id);
                        })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> {
                            throw new EspecificacaoDuplicadaException();
                        })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST) || status.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (request, response) -> {
                            String mensagem = new String(response.getBody().readAllBytes());
                            throw new EspecificacaoDadosInvalidosException();
                        })
                .toBodilessEntity();
    }

    @Override
    public void arquivar(UUID id) {
        alterarArquivamento(id, "arquivar");
    }

    @Override
    public void desarquivar(UUID id) {
        alterarArquivamento(id, "desarquivar");
    }

    @Override
    public void excluir(UUID id) {
        executarComRefresh(() -> api.delete().uri(PATH_API + "/{id}", id).retrieve().toBodilessEntity());
    }

    private void alterarArquivamento(UUID id, String acao) {
        api.patch()
                .uri(PATH_API + "/" + id + "/" + acao)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new EspecificacaoNaoEncontradaException(id);
                        })
                .toBodilessEntity();
    }

    private <T> T executarComRefresh(Supplier<T> chamadaApi) {
        return retryComRefresh.executar(chamadaApi);
    }
}
