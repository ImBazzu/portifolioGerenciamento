package br.com.imbazzu.secretariaTransporte.localidades.core.portas.out;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

public interface DestinoQueryPorta {

        ResultadoPaginado<DestinoOutput> listarPorCidade(
                        UUID idCidade, String nome, int pagina, int tamanho, List<OrdemPaginacao> ordenacao);

        ResultadoPaginado<DestinoOutputArquivado> listarPorCidadeArquivado(UUID idCidade, String nome, int pagina,
                        int tamanho, List<OrdemPaginacao> ordenacao);

        ResultadoPaginado<DestinoHistoricoOutput> buscarHistorico(UUID id, int pagina, int tamanho);

        Optional<DestinoOutput> buscarPorId(UUID idDestino);

        Optional<DestinoOutputArquivado> buscarPorIdArquivado(UUID idDestino);

        boolean verificarExistenciaPorId(UUID idDestino);

        boolean verificarDuplicidade(UUID IdCidade, Nome nome);

        boolean verificarDuplicidadeIgnorandoIdDestino(UUID idCidade, Nome nome, UUID idDestino);

        record DestinoOutputArquivado(
                        UUID id,
                        String nome,
                        LocalDate dataArquivado,
                        Integer qtdPassageiros) {
        }

        record DestinoOutput(
                        UUID id,
                        String nome,
                        Integer qtdPassageiros) {

        }

        record DestinoHistoricoOutput(
                        LocalDate data,
                        int qtdPassageiros) {
        }

}
