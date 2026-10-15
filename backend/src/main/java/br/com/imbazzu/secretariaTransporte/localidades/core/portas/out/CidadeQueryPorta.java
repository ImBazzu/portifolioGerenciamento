package br.com.imbazzu.secretariaTransporte.localidades.core.portas.out;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;

public interface CidadeQueryPorta {

    ResultadoPaginado<CidadeOutput> listar(String nome, int pagina, int tamanho, List<OrdemPaginacao> ordenacao);

    ResultadoPaginado<CidadeOutputArquivado> listarArquivados(
            String nome, int pagina, int tamanho, List<OrdemPaginacao> ordenacao);

    Optional<CidadeOutput> buscar(UUID idCidade);

    Optional<CidadeOutputArquivado> buscarArquivado(UUID idCidade);

    boolean existeCidade(UUID idCidade);

    boolean verificarCidadeCadastrada(String nome, CidadeEstadoEnum estado);

    boolean verificarCidadeCadastradaIgnorandoId(UUID idCidade, String nomeCidade, CidadeEstadoEnum estado);

    record CidadeOutput(UUID id, String nome, String estado, String tempoViagem, Integer totalDestinos) {
    }
    record CidadeOutputArquivado(UUID id, String nome, String estado, Duration tempoViagem, Integer totalDestinos, LocalDate dataArquivamento) {
    }
}
