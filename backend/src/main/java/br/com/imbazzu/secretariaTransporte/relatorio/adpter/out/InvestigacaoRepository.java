package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;

public interface InvestigacaoRepository extends JpaRepository<AgendamentoJpa, UUID> {
    interface Pessoa {
        UUID getId();
        String getNome();
        String getCpf();
        String getTelefone();
        String getEspecificacao();
        LocalDate getArquivadaEm();
    }

    interface Historico {
        UUID getId();
        LocalDate getData();
        LocalTime getChegada();
        String getCidade();
        String getDestino();
        String getLista();
        String getTipoVeiculo();
        String getIdentificacao();
        String getMotorista();
        UUID getVeiculoId();
        int getAcompanhantes();
        boolean getBuscarNaResidencia();
        String getAutor();
    }

    interface ResumoMotorista {
        UUID getId();
        String getNome();
        String getCategoria();
        long getVeiculos();
        long getPassageiros();
    }

    interface ResumoGeral {
        long getMotoristas();
        long getVeiculos();
        long getPassageiros();
    }

    interface Veiculo {
        UUID getId();
        LocalDate getData();
        String getMotorista();
        String getCategoria();
        String getTipo();
        String getIdentificacao();
        LocalTime getSaida();
        boolean getSaidaCalculada();
    }

    interface Passageiro {
        UUID getVeiculoId();
        String getNome();
        String getCidade();
        String getDestino();
        LocalTime getChegada();
        long getSegundos();
        int getNanos();
        int getAcompanhantes();
    }

    @Query(value = """
            SELECT p.id AS id, p.nome AS nome, p.cpf AS cpf, p.telefone AS telefone,
                   e.nome AS especificacao, p.dataArquivado AS arquivadaEm
            FROM PessoaJpa p JOIN p.especificacao e
            WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
               OR (:nome <> '' AND p.cpf LIKE CONCAT('%', :nome, '%'))
            ORDER BY p.nome, p.id
            """,
            countQuery = """
            SELECT COUNT(p) FROM PessoaJpa p
            WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
               OR (:nome <> '' AND p.cpf LIKE CONCAT('%', :nome, '%'))
            """)
    Page<Pessoa> buscarPessoas(@Param("nome") String nome, Pageable pageable);

    @Query("""
            SELECT p.id AS id, p.nome AS nome, p.cpf AS cpf, p.telefone AS telefone,
                   e.nome AS especificacao, p.dataArquivado AS arquivadaEm
            FROM PessoaJpa p JOIN p.especificacao e WHERE p.id = :id
            """)
    java.util.Optional<Pessoa> buscarPessoa(@Param("id") UUID id);

    String HISTORICO_FROM = """
            FROM AgendamentoJpa a
            JOIN DestinoJpa d ON d.id = a.idDestino
            JOIN CidadeJpa c ON c.id = d.cidadeId
            JOIN TipoListaJpa l ON l.id = a.idLista
            JOIN TipoVeiculoJpa t ON t.id = a.idTipoVeiculo
            LEFT JOIN VeiculoAgendaJpa v ON v.id = a.idVeiculoAberto
            LEFT JOIN MotoristaJpa m ON m.id = v.idMotorista
            LEFT JOIN a.autor autor
            WHERE a.idPessoa = :pessoa
              AND (:inicio IS NULL OR a.data >= :inicio)
              AND (:fim IS NULL OR a.data <= :fim)
              AND LOWER(c.nome) LIKE LOWER(CONCAT('%', :cidade, '%'))
              AND LOWER(d.nome) LIKE LOWER(CONCAT('%', :destino, '%'))
              AND (:situacao = 'TODOS' OR (:situacao = 'ALOCADO' AND v.id IS NOT NULL)
                   OR (:situacao = 'PENDENTE' AND v.id IS NULL))
            """;

    @Query(value = """
            SELECT a.id AS id, a.data AS data, a.horarioChegada AS chegada,
                   c.nome AS cidade, d.nome AS destino, l.nome AS lista,
                   COALESCE(v.nomeTipoVeiculo, t.nome) AS tipoVeiculo,
                   v.identificacao AS identificacao, v.id AS veiculoId, m.nome AS motorista,
                   a.acompanhantes AS acompanhantes, a.buscarNaResidencia AS buscarNaResidencia,
                   autor.nome AS autor
            """ + HISTORICO_FROM + " ORDER BY a.data DESC, a.horarioChegada DESC, a.id",
            countQuery = "SELECT COUNT(a) " + HISTORICO_FROM)
    Page<Historico> historico(@Param("pessoa") UUID pessoa, @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim, @Param("cidade") String cidade, @Param("destino") String destino,
            @Param("situacao") String situacao, Pageable pageable);

    String MOTORISTA_WHERE = """
            WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
              AND (:motorista IS NULL OR m.id = :motorista)
              AND LOWER(cat.nome) LIKE LOWER(CONCAT('%', :categoria, '%'))
              AND (:inicio IS NULL OR v.data >= :inicio)
              AND (:fim IS NULL OR v.data <= :fim)
              AND ((:cidade = '' AND :destino = '') OR EXISTS (
                  SELECT ag.id FROM AgendamentoJpa ag
                  JOIN DestinoJpa d ON d.id = ag.idDestino
                  JOIN CidadeJpa c ON c.id = d.cidadeId
                  WHERE ag.idVeiculoAberto = v.id
                    AND LOWER(c.nome) LIKE LOWER(CONCAT('%', :cidade, '%'))
                    AND LOWER(d.nome) LIKE LOWER(CONCAT('%', :destino, '%'))
              ))
            """;

    String MOTORISTA_FROM = """
            FROM VeiculoAgendaJpa v JOIN MotoristaJpa m ON m.id = v.idMotorista
            JOIN m.categoria cat
            """ + MOTORISTA_WHERE;

    @Query("""
            SELECT COUNT(DISTINCT m.id) AS motoristas, COUNT(DISTINCT v.id) AS veiculos,
                   COUNT(a.id) AS passageiros
            FROM VeiculoAgendaJpa v JOIN MotoristaJpa m ON m.id = v.idMotorista
            JOIN m.categoria cat LEFT JOIN AgendamentoJpa a ON a.idVeiculoAberto = v.id
            """ + MOTORISTA_WHERE)
    ResumoGeral resumoGeral(@Param("nome") String nome, @Param("motorista") UUID motorista, @Param("categoria") String categoria,
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim,
            @Param("cidade") String cidade, @Param("destino") String destino);

    @Query(value = """
            SELECT m.id AS id, m.nome AS nome, cat.nome AS categoria,
                   COUNT(DISTINCT v.id) AS veiculos, COUNT(a.id) AS passageiros
            FROM VeiculoAgendaJpa v JOIN MotoristaJpa m ON m.id = v.idMotorista
            JOIN m.categoria cat LEFT JOIN AgendamentoJpa a ON a.idVeiculoAberto = v.id
            """ + MOTORISTA_WHERE +
            " GROUP BY m.id, m.nome, cat.nome ORDER BY COUNT(DISTINCT v.id) DESC, m.nome, m.id",
            countQuery = "SELECT COUNT(DISTINCT m.id) " + MOTORISTA_FROM)
    Page<ResumoMotorista> resumoMotoristas(@Param("nome") String nome, @Param("motorista") UUID motorista, @Param("categoria") String categoria,
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim,
            @Param("cidade") String cidade, @Param("destino") String destino, Pageable pageable);

    @Query(value = """
            SELECT v.id AS id, v.data AS data, m.nome AS motorista, cat.nome AS categoria,
                   v.nomeTipoVeiculo AS tipo, v.identificacao AS identificacao,
                   v.horarioSaida AS saida, v.horarioSaidaCalculado AS saidaCalculada
            """ + MOTORISTA_FROM + " ORDER BY v.data DESC, m.nome, v.id",
            countQuery = "SELECT COUNT(v) " + MOTORISTA_FROM)
    Page<Veiculo> veiculos(@Param("nome") String nome, @Param("motorista") UUID motorista, @Param("categoria") String categoria,
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim,
            @Param("cidade") String cidade, @Param("destino") String destino, Pageable pageable);

    @Query("""
            SELECT a.idVeiculoAberto AS veiculoId, p.nome AS nome, c.nome AS cidade, d.nome AS destino,
                   a.horarioChegada AS chegada, a.tempoViagemSegundos AS segundos,
                   a.tempoViagemNanos AS nanos, a.acompanhantes AS acompanhantes
            FROM AgendamentoJpa a JOIN PessoaJpa p ON p.id = a.idPessoa
            JOIN DestinoJpa d ON d.id = a.idDestino JOIN CidadeJpa c ON c.id = d.cidadeId
            WHERE a.idVeiculoAberto IN :ids ORDER BY a.horarioChegada, p.nome, a.id
            """)
    List<Passageiro> passageiros(@Param("ids") List<UUID> ids);
}
