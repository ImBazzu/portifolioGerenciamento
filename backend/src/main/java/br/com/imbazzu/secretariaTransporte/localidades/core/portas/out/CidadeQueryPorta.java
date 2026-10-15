package br.com.imbazzu.secretariaTransporte.localidades.core.portas.out;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;

public interface CidadeQueryPorta {

    ResultadoPaginado<CidadeOutput> listar(String nome, int pagina, int tamanho);

    ResultadoPaginado<CidadeOutputArquivado> listarArquivadas(String nome, int pagina, int tamanho);

    boolean existeCidade(UUID idCidade);

    boolean verificarCidadeCadastrada(String nome, CidadeEstadoEnum estado);

    boolean verificarCidadeCadastradaIgnorandoId(UUID idCidade, String nomeCidade, CidadeEstadoEnum estado);

    record CidadeOutput(UUID id, String nome, String estado, Duration tempoViagem, Integer totalDestinos) {
    }
    record CidadeOutputArquivado(UUID id, String nome, String estado, Duration tempoViagem, Integer totalDestinos, LocalDate dataArquivamento) {
    }
}
