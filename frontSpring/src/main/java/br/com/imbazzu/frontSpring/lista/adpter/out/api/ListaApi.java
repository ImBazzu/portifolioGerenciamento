package br.com.imbazzu.frontSpring.lista.adpter.out.api;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.DiaSemana;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaDetalheApiDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResumoApiDto;
import br.com.imbazzu.frontSpring.lista.core.porta.out.ListaApiPorta;
import br.com.imbazzu.frontSpring.prioridade.PrioridadeApi;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListaApi implements ListaApiPorta {

    private static final String PATH = "/lista";
    private static final int TAMANHO_PAGINA = 5;

    private final RestClient api;
    private final RetryComRefreshPorta retryComRefresh;
    private final PrioridadeApi prioridades;

    public ListaApi(RestClientProtegido api, RetryComRefreshPorta retryComRefresh, PrioridadeApi prioridades) {
        this.api = api.get();
        this.retryComRefresh = retryComRefresh;
        this.prioridades = prioridades;
    }

    @Override
    public UUID salvar(ListaRequestDto dto) {
        return executarComRefresh(() -> {
            ResponseEntity<Void> response = api.post()
                    .uri(PATH)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
            var location = response.getHeaders().getLocation();
            if (location == null) {
                throw new IllegalStateException("A API não retornou o endereço da lista criada.");
            }
            var path = location.getPath();
            return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
        });
    }

    @Override
    public void editar(UUID id, ListaRequestDto dto) {
        executarComRefresh(() -> api.put()
                .uri(PATH + "/" + id)
                .body(dto)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new IllegalArgumentException("Lista não encontrada: " + id);
                        })
                .toBodilessEntity());
    }

    @Override
    public void atualizarRegras(UUID id, ListaRegrasRequestDto dto) {
        executarComRefresh(() -> api.put()
                .uri(PATH + "/" + id + "/regras")
                .body(dto)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new IllegalArgumentException("Lista não encontrada: " + id);
                        })
                .toBodilessEntity());
    }

    @Override
    public void arquivar(UUID id) {
        alterarStatus(id, "arquivar");
    }

    @Override
    public void desarquivar(UUID id) {
        alterarStatus(id, "desarquivar");
    }

    @Override
    public void deletar(UUID id) {
        executarComRefresh(() -> api.delete()
                .uri(PATH + "/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new IllegalArgumentException("Lista não encontrada: " + id);
                        })
                .toBodilessEntity());
    }

    @Override
    public ResultadoPaginado<ListaResponseDto> listarAtivas(String nome, int pagina, int tamanho) {
        return listar("/listar", nome, pagina, tamanho);
    }

    @Override
    public ResultadoPaginado<ListaResponseDto> listarArquivadas(String nome, int pagina, int tamanho) {
        return listar("/listar/arquivado", nome, pagina, tamanho);
    }

    @Override
    public ListaResponseDto buscarAtiva(UUID id) {
        var lista = buscar(id);
        if (lista.dataArquivado() != null) {
            throw new IllegalArgumentException("A lista está arquivada: " + id);
        }
        return toResponse(lista);
    }

    @Override
    public ListaResponseDto buscarArquivada(UUID id) {
        var lista = buscar(id);
        if (lista.dataArquivado() == null) {
            throw new IllegalArgumentException("A lista não está arquivada: " + id);
        }
        return toResponse(lista);
    }

    private void alterarStatus(UUID id, String acao) {
        executarComRefresh(() -> api.patch()
                .uri(PATH + "/" + id + "/" + acao)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new IllegalArgumentException("Lista não encontrada: " + id);
                        })
                .toBodilessEntity());
    }

    private ResultadoPaginado<ListaResponseDto> listar(String path, String nome, int pagina, int tamanho) {
        return executarComRefresh(() -> api.get()
                .uri(uri -> uri.path(PATH + path)
                        .queryParam("nome", nome == null ? "" : nome)
                        .queryParam("pagina", pagina)
                        .queryParam("tamanho", tamanho)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<ResultadoPaginado<ListaResumoApiDto>>() {})
                .map(ListaApi::toResponse));
    }

    private ListaDetalheApiDto buscar(UUID id) {
        return executarComRefresh(() -> api.get()
                .uri(PATH + "/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {
                            throw new IllegalArgumentException("Lista não encontrada: " + id);
                        })
                .body(ListaDetalheApiDto.class));
    }

    private <T> T executarComRefresh(Supplier<T> chamadaApi) {
        return retryComRefresh.executar(chamadaApi);
    }

    private static ListaResponseDto toResponse(ListaResumoApiDto lista) {
        return new ListaResponseDto(
                lista.id(),
                lista.nome(),
                lista.descricao(),
                lista.totalRegras(),
                lista.totalCidades(),
                lista.arquivado(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    private ListaResponseDto toResponse(ListaDetalheApiDto lista) {
        var nomes = prioridades.listar().stream().collect(Collectors.toMap(
                item -> item.id().toString(), PrioridadeApi.Prioridade::nome));
        var regras = java.util.stream.Stream.concat(
                lista.regrasCadastro().stream().map(regra -> formatarRegraCadastro(regra, nomes)),
                lista.regrasSeparacao().stream().map(ListaApi::formatarRegraSeparacao))
                .toList();
        var cidades = lista.regrasCadastro().stream()
            .flatMap(regra -> regra.cidades().stream())
            .map(cidade -> cidade.nome())
            .distinct()
            .toList();
        return new ListaResponseDto(
                lista.id(),
                lista.nome(),
                lista.descricao(),
                regras.size(),
                cidades.size(),
                lista.dataArquivado() != null,
                regras,
                cidades,
                lista.regrasCadastro(),
                lista.regrasSeparacao(),
                lista.finalidadeEspecial());
    }

    private static String formatarRegraCadastro(ListaDetalheApiDto.RegraCadastro regra, Map<String, String> nomes) {
        var horario = regra.inicioHorario() == null
                ? "sem restrição de horário"
                : "%s–%s".formatted(regra.inicioHorario(), regra.finalHorario());
        
        var dias = regra.diasSemana() == null || regra.diasSemana().isEmpty()
                ? "qualquer dia"
                : String.join(", ", regra.diasSemana().stream()
                    .map(dia -> DiaSemana.valueOf(dia).getLabelCurto())
                    .toList());
        
        var intervalo = regra.intervaloRepetacao() == null ? 1 : regra.intervaloRepetacao();
        var intervaloStr = intervalo == 1 ? "semanalmente" : "a cada %d semanas".formatted(intervalo);
        
        var descricao = regra.descricao() == null ? "" : " — " + regra.descricao();
        return "Cadastro: prioridades %s, %d veículo(s), %d cidade(s), %s, %s, %s, anexo %s%s".formatted(
                regra.prioridades().stream().map(id -> {
                    var nome = nomes.get(id);
                    if (nome == null) {
                        throw new IllegalStateException("Prioridade não encontrada: " + id);
                    }
                    return nome;
                }).collect(Collectors.joining(", ")),
                regra.veiculos().size(),
                regra.cidades().size(),
                horario,
                dias,
                intervaloStr,
                regra.obrigacaoAnexo() ? "obrigatório" : "não obrigatório",
                descricao);
    }

    private static String formatarRegraSeparacao(ListaDetalheApiDto.RegraSeparacao regra) {
        return "Separação: tipo de veículo %s, tolerância %s".formatted(
                regra.nome(),
                regra.tempoTolerancia());
    }
}
