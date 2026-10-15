package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.mapper.CidadeBancoMapper;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.CidadeRepository;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Cidade;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

//Adaptador da porta de persistência
@Repository
public class CidadeCommandAdapter implements CidadeCommandPorta {

    // Spring Jpa
    private final CidadeRepository repo;

    // Construtor
    public CidadeCommandAdapter(CidadeRepository cidadeRepository) {
        // inserindo o Jpa
        this.repo = cidadeRepository;
    }

    /**
     * Salva a cidade no banco de dados
     * 
     * @param cidade cidade a ser salva
     * @return Cidade Salva
     */
    @Override
    public UUID salvar(Cidade cidade) {
        // Converte o Model para o JpaEntity
        var cidadeJpa = CidadeBancoMapper.paraEntityCidade(cidade);
        try {
            // Salva e verifica se o banco lança algum erro
            cidadeJpa = repo.saveAndFlush(cidadeJpa);
            // Converte a JpaEntity para o Model
            return cidadeJpa.getId();
        } catch (DataIntegrityViolationException ex) {

            // Exceção para caso a exceção do Spring seja qualquer outra
            throw new BancoDeDadosException("Não foi possível salvar a cidade no banco de dados.", ex);
        }
    }

    /**
     * Busca a cidade pelo identificador
     * 
     * @param id identificador
     * @return cidade encontrada
     */
    @Override
    public Optional<Cidade> buscarPorId(UUID id) {
        // procura no banco a cidade
        return repo.findById(id).map(CidadeBancoMapper::paraDomainCidade);
    }

    @Override
    public void deletar(UUID cidade) {
        repo.deleteById(cidade);
    }

}
