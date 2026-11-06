package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoQueryRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoArquivadoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
public class TipoVeiculoQueryAdapter implements TipoVeiculoQueryPorta {

    private final TipoVeiculoQueryRepository repo;
    

    @Override
    public Optional<TipoVeiculo> buscarEntidadePorId(UUID id) {
        //Entidade Jpa buscada no banco
        var jpaTipoVeiculo = repo.findById(id);
        //Mapeando para o modelo de domínio
         return jpaTipoVeiculo.map(
            v -> TipoVeiculo.recriar(
                v.getId(), 
                new NomeVeiculo(v.getNome()), 
                v.getCapacidade(), 
                v.getDataArquivado())
        );
    }


     @Override
    public boolean existeVeiculoUsandoTipoVeiculo(UUID tipoVeiculoId) {
        // Verifica se existe algum veículo usando o tipo de veículo especificado
        return repo.existeVeiculoUsandoTipoVeiculo(tipoVeiculoId);
    }

    @Override
    public boolean existeOutroTipoVeiculoComNome(UUID id, NomeVeiculo nome) {
        // Verifica se existe outro tipo de veículo com o mesmo nome, ignorando o tipo de veículo com o ID especificado
        return repo.existsByNomeAndIdNot(nome.valor(), id);
    }

        @Override
    public boolean existeOutroTipoVeiculoComNome(NomeVeiculo nome) {
        // Verifica se existe outro tipo de veículo com o mesmo nome
        return repo.existsByNome(nome.valor());
    }

    
    @Override
    public Optional<TipoVeiculoOutput> buscarAtivo(UUID id) {
        // Busca o tipo de veículo ativo por ID e mapeia para o modelo de saída
        return repo.buscarAtivoPorId(id).map(
            tipo->{
                return new TipoVeiculoOutput(tipo.getId(), tipo.getNome(), tipo.getCapacidade());
            }
        );
    }

    
    @Override
    public Optional<TipoVeiculoArquivadoOutput> buscarArquivado(UUID id) {
        // Busca o tipo de veículo arquivado por ID e mapeia para o modelo de saída
        return repo.buscarArquivadoPorId(id).map(
            tipo->{return new TipoVeiculoArquivadoOutput(
                tipo.getId(), 
                tipo.getNome(), 
                tipo.getCapacidade(), 
                tipo.getDataArquivado());}
        );
    }


    @Override
    public ResultadoPaginado<TipoVeiculoOutput> listar(String nome, int pagina, int tamanho) {
        // Cria um objeto PageRequest para paginação
        var pageable = PageRequest.of(pagina, tamanho);
        // Busca os tipos de veículos ativos com paginação e filtro por nome, mapeando para o modelo de saída
        var paginado = repo.listarAtivo(nome, pageable)
                .map(v -> new TipoVeiculoOutput(v.getId(), v.getNome(), v.getCapacidade()));
        // Converte o resultado paginado para o modelo de domínio e retorna
        return PageMapper.toDomain(paginado);
    }

    @Override
    public ResultadoPaginado<TipoVeiculoArquivadoOutput> listarArquivado(String nome, int pagina, int tamanho) {
        // Cria um objeto PageRequest para paginação
        var pageable = PageRequest.of(pagina, tamanho);
        // Busca os tipos de veículos arquivados com paginação e filtro por nome, mapeando para o modelo de saída
        var paginado = repo.listarArquivado(nome, pageable)
                .map(v -> new TipoVeiculoArquivadoOutput(v.getId(), v.getNome(), v.getCapacidade(),
                        v.getDataArquivado()));
        // Converte o resultado paginado para o modelo de domínio e retorna
        return PageMapper.toDomain(paginado);
    }

}
