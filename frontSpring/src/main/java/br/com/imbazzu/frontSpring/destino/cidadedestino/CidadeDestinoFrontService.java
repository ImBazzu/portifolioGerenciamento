package br.com.imbazzu.frontSpring.destino.cidadedestino;

import java.util.UUID;

import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.destino.cidadedestino.dto.CidadeDestinoRequestDto;
import br.com.imbazzu.frontSpring.destino.cidadedestino.dto.CidadeDestinoResponseDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;

/**
 * CidadeDestinoFrontService
 */
@Service
public class CidadeDestinoFrontService {

    private final RestClient backendRestClient;

    private final String BASE_URL = "/cidade";

    public CidadeDestinoFrontService(RestClientProtegido backendRestClient) {
        this.backendRestClient = backendRestClient.get();
    }

    public ResultadoPaginado<CidadeDestinoResponseDto> buscarPorNome(String nome, int page, int size) {
        return backendRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("nome", nome)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ResultadoPaginado<CidadeDestinoResponseDto>>() {
                });
    }

    public CidadeDestinoResponseDto buscarPorId(UUID id) {
        return backendRestClient.get()
                .uri(BASE_URL + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CidadeDestinoResponseDto.class);
    }

    public void salvar(CidadeDestinoRequestDto dto) {
        backendRestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void editar(UUID id, CidadeDestinoRequestDto dto) {
        backendRestClient.put()
                .uri(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void arquivar(UUID id) {
        backendRestClient.patch()
                .uri(BASE_URL + "/{id}/arquivar", id)
                .retrieve()
                .toBodilessEntity();
    }
}
