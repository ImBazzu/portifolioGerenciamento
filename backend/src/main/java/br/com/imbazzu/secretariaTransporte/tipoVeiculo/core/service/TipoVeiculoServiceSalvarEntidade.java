package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;

public class TipoVeiculoServiceSalvarEntidade {

    private final TipoVeiculoQueryPorta queryPorta;

    public TipoVeiculoServiceSalvarEntidade(TipoVeiculoQueryPorta queryPorta) {
        this.queryPorta = queryPorta;
    }

    /**
     * Executa a lógica de salvar uma entidade TipoVeiculo.
     * @param nome
     * @param capacidade
     * @return
     */
    public TipoVeiculo executar(String nome, int capacidade) {
        //Nome do veículo
        var nomeVeiculo = new NomeVeiculo(nome);
        // Verifica se já existe outro tipo de veículo com o mesmo nome
        var nomeDuplicado = queryPorta.existeOutroTipoVeiculoComNome(nomeVeiculo);
        // Se existir, lança uma exceção informando que o nome já está em uso
        if (nomeDuplicado) {
            throw new TipoVeiculoDuplicadoException(nomeVeiculo);
        } 
        // Cria uma nova instância de TipoVeiculo com o nome e capacidade fornecidos
        // e retorna a entidade criada
        return TipoVeiculo.criar(nomeVeiculo, capacidade);
    }
    
}
