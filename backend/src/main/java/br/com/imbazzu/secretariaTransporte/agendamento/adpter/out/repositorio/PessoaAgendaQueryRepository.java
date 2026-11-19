package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.PessoaAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;

public interface PessoaAgendaQueryRepository extends Repository<PessoaJpa, UUID> {

    @Query("""
            SELECT p.especificacao.prioridade AS prioridade,
                   prioridade.buscarNaResidencia AS buscarNaResidencia,
                   prioridade.quantidadeAcompanhantes AS acompanhantes,
                   CASE WHEN prioridade.dataArquivado IS NULL THEN true ELSE false END AS prioridadeAtiva,
                   CASE WHEN p.dataArquivado IS NULL THEN true ELSE false END AS ativa
            FROM PessoaJpa p
            JOIN PrioridadeJpa prioridade ON prioridade.id = p.especificacao.prioridade
            WHERE p.id = :id
            """)
    Optional<PessoaAgendaProjecao> buscarProjecaoPorId(@Param("id") UUID id);
}
