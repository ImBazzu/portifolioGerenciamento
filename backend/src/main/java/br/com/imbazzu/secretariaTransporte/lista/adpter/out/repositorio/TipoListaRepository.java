package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;

/**
 * Repositório do lado Command (escrita e carga do agregado).
 * As consultas de leitura ficam em {@link TipoListaQueryRepository}.
 */
public interface TipoListaRepository extends JpaRepository<TipoListaJpa, UUID> {
}
