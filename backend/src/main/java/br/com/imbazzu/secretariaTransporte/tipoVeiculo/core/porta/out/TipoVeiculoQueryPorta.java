package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoArquivadoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;

public interface TipoVeiculoQueryPorta {

    /**
     * Busca um tipo de veículo por ID.
     * @param id
     * @return
     */
    Optional<TipoVeiculo> buscarEntidadePorId(UUID id);

    /**
     * Verifica se existe algum veículo usando o tipo de veículo.
     * @param tipoVeiculoId
     * @return
     */
    boolean existeVeiculoUsandoTipoVeiculo(UUID tipoVeiculoId);

    /**
     * Verifica se existe algum veículo com o mesmo nome, ignorando o ID do veículo atual.
     * @param id
     * @param nome
     * @return
     */
    boolean existeOutroTipoVeiculoComNome(UUID id, NomeVeiculo nome);

    /**
     * Verifica se existe algum veículo com o mesmo nome.
     * @param nome
     * @return
     */
    boolean existeOutroTipoVeiculoComNome(NomeVeiculo nome);

   /**
    * Busca um tipo de veículo ativo por ID.
    * @param id
    * @return
    */ Optional<TipoVeiculoOutput> buscarAtivo(UUID id);

    /**
     * Busca um tipo de veículo arquivado por ID.
     * @param id
     * @return
     */
    Optional<TipoVeiculoArquivadoOutput> buscarArquivado(UUID id);

    /**
     * Lista os tipos de veículos ativos com paginação.
     * @param filtro
     * @param pagina
     * @param tamanho
     * @return
     */
    ResultadoPaginado<TipoVeiculoOutput> listar(String filtro, int pagina, int tamanho);

    /**
     * Lista os tipos de veículos arquivados com paginação.
     * @param filtro
     * @param pagina
     * @param tamanho
     * @return
     */
    ResultadoPaginado<TipoVeiculoArquivadoOutput> listarArquivado(String filtro, int pagina, int tamanho);

}
