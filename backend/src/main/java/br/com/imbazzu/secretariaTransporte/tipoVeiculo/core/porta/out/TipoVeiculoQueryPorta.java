package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoArquivadoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoHistoricoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;

public interface TipoVeiculoQueryPorta {

    TipoVeiculoOutput buscarAtivo(UUID id);

    TipoVeiculoArquivadoOutput buscarArquivado(UUID id);

    ResultadoPaginado<TipoVeiculoOutput> listar(String filtro, int pagina, int tamanho);

    ResultadoPaginado<TipoVeiculoArquivadoOutput> listarArquivado(String filtro, int pagina, int tamanho);

    ResultadoPaginado<TipoVeiculoHistoricoOutput> listarHistorico(UUID idTipoVeiculo, int pagina, int tamanho);

}
