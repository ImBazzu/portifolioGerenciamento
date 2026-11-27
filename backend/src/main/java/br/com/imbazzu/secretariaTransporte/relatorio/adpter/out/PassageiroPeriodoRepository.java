package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.PassageiroPeriodoDTO;

public interface PassageiroPeriodoRepository extends JpaRepository<AgendamentoJpa, UUID> {
    interface Opcao {
        UUID getId();
        String getNome();
    }

    interface Destino extends Opcao {
        UUID getIdCidade();
    }

    @Query("SELECT l.id AS id, l.nome AS nome FROM TipoListaJpa l ORDER BY l.nome, l.id")
    List<Opcao> listarListas();

    @Query("SELECT c.id AS id, c.nome AS nome FROM CidadeJpa c ORDER BY c.nome, c.id")
    List<Opcao> listarCidades();

    @Query("SELECT d.id AS id, d.nome AS nome, d.cidadeId AS idCidade FROM DestinoJpa d ORDER BY d.nome, d.id")
    List<Destino> listarDestinos();

    @Query("SELECT t.id AS id, t.nome AS nome FROM TipoVeiculoJpa t ORDER BY t.nome, t.id")
    List<Opcao> listarTiposVeiculo();

    @Query("""
            SELECT new br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto.PassageiroPeriodoDTO(
                p.id, p.nome, p.cpf, p.telefone, e.nome, a.data, l.id, l.nome,
                c.id, c.nome, d.id, d.nome, tv.id, tv.nome, a.horarioChegada,
                a.tempoViagemSegundos, a.tempoViagemNanos, a.acompanhantes,
                v.id, v.horarioSaida, v.horarioSaidaCalculado, (a.canceladoEm IS NOT NULL))
            FROM AgendamentoJpa a
            JOIN PessoaJpa p ON p.id = a.idPessoa
            JOIN p.especificacao e
            JOIN TipoListaJpa l ON l.id = a.idLista
            JOIN DestinoJpa d ON d.id = a.idDestino
            JOIN CidadeJpa c ON c.id = d.cidadeId
            JOIN TipoVeiculoJpa tv ON tv.id = a.idTipoVeiculo
            LEFT JOIN VeiculoAgendaJpa v ON v.id = a.idVeiculoAberto
            WHERE a.data BETWEEN :inicio AND :fim
            ORDER BY p.nome, p.id, a.data, a.horarioChegada, a.id
            """)
    List<PassageiroPeriodoDTO> buscar(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
