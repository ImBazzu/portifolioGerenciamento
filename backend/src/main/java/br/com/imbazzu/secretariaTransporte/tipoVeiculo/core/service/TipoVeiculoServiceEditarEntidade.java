package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;

public class TipoVeiculoServiceEditarEntidade {
    
    private final TipoVeiculoQueryPorta queryPorta;

    public TipoVeiculoServiceEditarEntidade(TipoVeiculoQueryPorta queryPorta) {
        this.queryPorta = queryPorta;
    }

    /**
     * Edita a entidade TipoVeiculo com os novos valores do DTO, caso exista outro veículo com o mesmo nome lança exceção
     * @param veiculo
     * @param id
     * @param nome
     * @param capacidade
     */
    public void executar(TipoVeiculo veiculo, UUID id, String nome, int capacidade) {
        // Criar um novo objeto NomeVeiculo com o novo nome
        var novoNome = new NomeVeiculo(nome);
        // Verificar se existe outro veículo com o mesmo nome, ignorando o ID do veículo atual
        var nomeDuplicado = queryPorta.existeOutroTipoVeiculoComNome(veiculo.getId(), novoNome);
        // Se houver um nome duplicado, lançar uma exceção
        if(nomeDuplicado) {
            throw new TipoVeiculoDuplicadoException(novoNome);
        }
        // Editar os atributos do veículo com os novos valores do DTO
        veiculo.editarVeiculo(
                novoNome,
                capacidade);
    }
}
