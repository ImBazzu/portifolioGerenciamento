package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;

public interface TipoVeiculoCommandRepository extends JpaRepository<TipoVeiculoJpa, UUID> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, UUID id);

}
