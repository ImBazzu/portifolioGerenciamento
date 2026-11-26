package br.com.imbazzu.frontSpring.motorista;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

@Service
public class MotoristaApi {
    public record Categoria(UUID id, String nome) {}
    public record Motorista(UUID id, String nome, String telefone, UUID categoriaId, String categoriaNome) {}
    public record Pagina(List<Motorista> content, int totalPages, int number) {}
    public record Atribuicao(UUID veiculoId, UUID motoristaId, String nome) {}
    public record CategoriaRequest(String nome) {}
    public record MotoristaRequest(String nome, String telefone, UUID categoriaId) {}
    public record AtribuicaoRequest(UUID motoristaId) {}
    private final RestClient api;
    private final RetryComRefreshPorta retry;

    public MotoristaApi(RestClientProtegido api, RetryComRefreshPorta retry) {
        this.api = api.get();
        this.retry = retry;
    }

    public List<Categoria> categorias() {
        return executar(() -> api.get().uri("/motoristas/categorias").retrieve()
                .body(new ParameterizedTypeReference<List<Categoria>>() {}));
    }

    public Pagina buscar(String nome, UUID categoria, int pagina) {
        return executar(() -> api.get().uri(uri -> {
            var builder = uri.path("/motoristas").queryParam("nome", nome).queryParam("pagina", pagina);
            if (categoria != null) builder.queryParam("categoria", categoria);
            return builder.build();
        }).retrieve().body(Pagina.class));
    }

    public List<Atribuicao> atribuicoes(LocalDate data) {
        return executar(() -> api.get().uri(uri -> uri.path("/motoristas/atribuicoes")
                .queryParam("data", data).build()).retrieve()
                .body(new ParameterizedTypeReference<List<Atribuicao>>() {}));
    }

    public void salvarCategoria(UUID id, String nome) {
        executar(() -> id == null
                ? api.post().uri("/motoristas/categorias").body(new CategoriaRequest(nome)).retrieve().toBodilessEntity()
                : api.put().uri("/motoristas/categorias/{id}", id).body(new CategoriaRequest(nome)).retrieve().toBodilessEntity());
    }

    public void salvar(UUID id, String nome, String telefone, UUID categoriaId) {
        var request = new MotoristaRequest(nome, telefone, categoriaId);
        executar(() -> id == null
                ? api.post().uri("/motoristas").body(request).retrieve().toBodilessEntity()
                : api.put().uri("/motoristas/{id}", id).body(request).retrieve().toBodilessEntity());
    }

    public void atribuir(UUID veiculoId, UUID motoristaId) {
        executar(() -> api.put().uri("/motoristas/veiculos/{id}", veiculoId)
                .body(new AtribuicaoRequest(motoristaId)).retrieve().toBodilessEntity());
    }

    private <T> T executar(Supplier<T> chamada) {
        return retry.executar(chamada);
    }
}
