package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.jpaAdapter;

import lombok.RequiredArgsConstructor;
import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.mapper.CidadeBancoMapper;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.CidadeRepository;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Cidade;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.UUID;

//Adaptador da porta de persistência
@Repository
@RequiredArgsConstructor
public class CidadeQueryAdapter implements CidadeQueryPorta {

    // Spring Jpa
    private final CidadeRepository repo;

    /**
     * Realiza a procura paginada por nome
     * 
     * @param nome    nome da cidade
     * @param pagina  pagina a ser procurada
     * @param tamanho quantidade de itens em cada página
     * @return lista paginada de cidade compatível com a busca
     */
    @Override
    public ResultadoPaginado<Cidade> listar(String nome, int pagina, int tamanho) {
        // Cria o objeto de paginação
        var pageable = PageRequest.of(pagina, tamanho);

        var pageCidade =
                // procura a cidade com o objeto paginado
                repo.findByNome(nome, pageable)
                        // Mapeia o JpaEntity para o Model
                        .map(CidadeBancoMapper::paraDomainCidade);
        // retorna a lista da cidade paginada
        return PageMapper.toDomain(pageCidade);
    }

    @Override
    public boolean verificarCidadeCadastrada(String nome, CidadeEstadoEnum estado) {
        return repo.existsByNomeAndEstado(nome, estado);
    }

    @Override
    public boolean verificarCidadeCadastradaIgnorandoId(UUID idCidade, String nome, CidadeEstadoEnum estado) {
        return repo.existsByNomeAndEstadoAndIdNot(nome, estado, idCidade);
    }

    @Override
    public boolean existeCidade(UUID idCidade) {
        return repo.existsById(idCidade);
    }

}
