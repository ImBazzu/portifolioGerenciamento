package br.com.imbazzu.frontSpring.pessoa.adpter.out.api;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaArquivadoResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaDadosInvalidosException;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaDuplicadaException;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaNaoEncontradaException;
import br.com.imbazzu.frontSpring.pessoa.core.porta.out.PessoaApiPorta;

@Service
public class PessoaApi implements PessoaApiPorta {
    private static final String PATH_API = "/pessoa";

    private final RestClient api;
    private final RetryComRefreshPorta retryComRefresh;

    public PessoaApi(RestClientProtegido api, RetryComRefreshPorta retryComRefresh) {
        this.api = api.get();
        this.retryComRefresh = retryComRefresh;
    }

    @Override
    public ResultadoPaginado<PessoaResponseDto> listar(String nome, boolean arquivadas, int pagina, int tamanho) {
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
    public PessoaResponseDto buscar(UUID id) {
        return api.get()
                .uri(PATH_API + "/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new PessoaNaoEncontradaException(id);
                        })
                .body(PessoaResponseDto.class);
    }

 @Override
    public UUID salvar(PessoaRequestDto pessoa) {
        var respostaCriacao = api.post()
                .uri(PATH_API)
                .body(pessoa)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> {
                            // Lê o corpo da resposta para pegar a mensagem exata do backend
                            throw new PessoaDuplicadaException(); // Certifique-se de que a exceção aceita String no construtor
                        })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST) || status.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (request, response) -> {
                            // Captura o texto retornado pelo ErroDto ou mensagem pura do backend
                            String mensagem = new String(response.getBody().readAllBytes());
                            throw new PessoaDadosInvalidosException();
                        })
                .toBodilessEntity();
        
        var location = respostaCriacao.getHeaders().getLocation();
        if (location == null) {
            throw new IllegalStateException("A API não retornou o endereço da pessoa criada.");
        }
        var path = location.getPath();
        return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
    }

    @Override
    public void editar(UUID id, PessoaRequestDto pessoa) {
        api.put()
                .uri(PATH_API + "/" + id)
                .body(pessoa)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new PessoaNaoEncontradaException(id);
                        })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        (request, response) -> {
                            throw new PessoaDuplicadaException();
                        })
                .onStatus(status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST) || status.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (request, response) -> {
                            String mensagem = new String(response.getBody().readAllBytes());
                            throw new PessoaDadosInvalidosException();
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
    public void validar(UUID id) {
        api.patch()
                .uri(PATH_API + "/" + id + "/validar")
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new PessoaNaoEncontradaException(id);
                        })
                .toBodilessEntity();
    }

    private void alterarArquivamento(UUID id, String acao) {
        api.patch()
                .uri(PATH_API + "/" + id + "/" + acao)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new PessoaNaoEncontradaException(id);
                        })
                .toBodilessEntity();
    }

    private <T> T executarComRefresh(Supplier<T> chamadaApi) {
        return retryComRefresh.executar(chamadaApi);
    }

    @Override
    public PessoaResponseDto buscarArquivado(UUID id) {

                return api.get()
                .uri("%s/%s/arquivado".formatted(PATH_API, id))
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new PessoaNaoEncontradaException(id);
                        })
                .body(PessoaResponseDto.class);    }
}
