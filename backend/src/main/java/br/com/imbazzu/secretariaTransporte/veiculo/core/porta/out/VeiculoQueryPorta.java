package br.com.imbazzu.secretariaTransporte.veiculo.core.porta.out;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.veiculo.application.query.model.VeiculoHistoricoOutput;

public interface VeiculoQueryPorta {

    ResultadoPaginado<VeiculoHistoricoOutput> buscarHistorico(UUID idTipoVeiculo, int pagina, int tamanho);
}
