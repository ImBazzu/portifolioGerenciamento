package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.mapper.TipoVeiculoBancoMapper;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoCommandRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
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
        var veiculoJPA = TipoVeiculoBancoMapper.paraJpa(veiculo);
        try {
            repo.saveAndFlush(veiculoJPA);
            return veiculoJPA.getId();
        } catch (DataAccessException ex) {
            throw new BancoDeDadosException("Não foi possível salvar o veiculo no banco de dados", ex);
        }
    }

    @Override
    public TipoVeiculo buscarPorId(UUID id) {
        return repo.findById(id)
                .map(TipoVeiculoBancoMapper::paraDomain)
                .orElseThrow(() -> new TipoVeiculoNaoEncontradoException(id));
    }

    @Override
    public boolean verificarPorNome(NomeVeiculo nome) {
        return repo.existsByNome(nome.valor());
    }

    @Override
    public boolean verificarPorNomeIgnorandoId(UUID id, NomeVeiculo nome) {
        return repo.existsByNomeAndIdNot(nome.valor(), id);
    }

    @Override
    public void deletar(UUID id) {
        repo.deleteById(id);
    }

}
