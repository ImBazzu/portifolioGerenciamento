package br.com.imbazzu.frontSpring.veiculo.adpter.out.api;

import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.excecoes.VeiculoDuplicadoException;
import br.com.imbazzu.frontSpring.veiculo.core.excecoes.VeiculoNaoEncontradoException;
import br.com.imbazzu.frontSpring.veiculo.core.porta.out.VeiculoApiPorta;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;


@Service
public class VeiculoApi implements VeiculoApiPorta {

    private final RestClient api;

    private final String pathApi =  "/veiculo";

    public VeiculoApi(RestClientProtegido api) {
        this.api = api.get();
    }
    @Override
    public void arquivar(UUID id) {
            api
                .patch()
                .uri("%s/%s/arquivar".formatted(pathApi,id))
                    .retrieve()
                    .onStatus(status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                            (r,p)->{throw new VeiculoNaoEncontradoException(id);})
                    .toBodilessEntity();

    }

    @Override
    public void desarquivar(UUID id) {
        api
                .patch()
                .uri("%s/%s/desarquivar".formatted(pathApi,id))
                .retrieve()
                .onStatus(status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (r,p)-> {throw new VeiculoNaoEncontradoException(id);})
                .toBodilessEntity();
    }

    @Override
    public void deletar(UUID id) {
        api
                .delete()
                .uri("%s/%s".formatted(pathApi,id))
                .retrieve()
                .onStatus(status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (r,p)-> {throw new VeiculoNaoEncontradoException(id);})
                .toBodilessEntity();
    }

    @Override
    public void editar(UUID id, TipoVeiculoRequestDto dto) {
        api
                .put()
                .uri(pathApi+"/"+id)
                .body(dto)
                .retrieve()
                .onStatus(status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (r,p)-> {throw new VeiculoNaoEncontradoException(id);})
                .onStatus(status->status.isSameCodeAs(HttpStatus.CONFLICT),
                        (r,p)->{throw new VeiculoDuplicadoException(id);})
                .toBodilessEntity();
    }

    @Override
    public void salvar(TipoVeiculoRequestDto dto) {
        api
                .post()
                .uri(pathApi)
                .body(dto)
                .retrieve()
                .onStatus(status->status.isSameCodeAs(HttpStatus.CONFLICT),
                        (r,p)->{throw new VeiculoDuplicadoException(null);})
                .toBodilessEntity();
    }

    @Override
    public ResultadoPaginado<TipoVeiculoResponseDto> listar(String nome, int pagina, int tamanho) {
        return api
                .get()
                .uri(uri->{
                    uri.path(pathApi+"/listar");
                    uri.queryParam("nome", nome);
                    uri.queryParam("pagina", pagina);
                    uri.queryParam("tamanho", tamanho);
                    return uri.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<ResultadoPaginado<TipoVeiculoResponseDto>>() {});
    }

    @Override
    public ResultadoPaginado<TipoVeiculoArquivadoResponseDto> listarArquivados(String nome, int pagina, int tamanho) {
        return api
                .get()
                .uri(uri->{
                    uri.path(pathApi+"/listar/arquivado");
                    uri.queryParam("nome", nome);
                    uri.queryParam("pagina", pagina);
                    uri.queryParam("tamanho", tamanho);
                    return uri.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<ResultadoPaginado<TipoVeiculoArquivadoResponseDto>>() {});
    }

    @Override
    public TipoVeiculoResponseDto buscar(UUID id) {
            return api
                .get()
                .uri(pathApi+"/"+id+"/ativo")
                .retrieve()
                .onStatus(status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (r,p)-> {throw new VeiculoNaoEncontradoException(id);})
                .body(TipoVeiculoResponseDto.class);
    }

    @Override
    public TipoVeiculoArquivadoResponseDto buscarArquivado(UUID id) {
        return api
                .get()
                .uri(pathApi+"/"+id+"/arquivado")
                .retrieve()
                .onStatus(status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (r,p)-> {throw new VeiculoNaoEncontradoException(id);})
                .body(TipoVeiculoArquivadoResponseDto.class);
    }
}
