package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoCommandRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TipoVeiculoCommandAdapter implements TipoVeiculoCommandPorta {

    private final TipoVeiculoCommandRepository repo;

    @Override
    public UUID salvar(TipoVeiculo veiculo) {
        var veiculoJPA = new TipoVeiculoJpa(
                veiculo.getId(),
                veiculo.getNome().toString(),
                veiculo.getCapacidade(),
                veiculo.getDataArquivado());
        try {
            repo.saveAndFlush(veiculoJPA);
            return veiculoJPA.getId();
        } catch (DataAccessException ex) {
            throw new BancoDeDadosException("Não foi possível salvar o veiculo no banco de dados", ex);
        }
    }

    @Override
    public void deletar(UUID id) {
        repo.deleteById(id);
    }

}
