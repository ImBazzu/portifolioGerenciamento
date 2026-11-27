package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.entidadeJpa.VeiculoAgendaJpa;

public interface VeiculoAgendaRepository extends JpaRepository<VeiculoAgendaJpa, UUID> {

    List<VeiculoAgendaJpa> findByDataOrderById(LocalDate data);

    
    boolean existsByIdTipoVeiculo(UUID idTipoVeiculo);
    boolean existsByIdMotorista(UUID idMotorista);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE VeiculoAgendaJpa v SET v.idMotorista = :motorista WHERE v.id = :veiculo")
    int atribuirMotorista(@Param("veiculo") UUID veiculo, @Param("motorista") UUID motorista);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE VeiculoAgendaJpa v
            SET v.saidaConfirmadaEm = :confirmadaEm,
                v.saidaConfirmadaPorId = :autorId
            WHERE v.id = :veiculo AND v.saidaConfirmadaEm IS NULL
            """)
    int confirmarSaida(
            @Param("veiculo") UUID veiculo,
            @Param("confirmadaEm") java.time.LocalDateTime confirmadaEm,
            @Param("autorId") UUID autorId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE VeiculoAgendaJpa v
            SET v.saidaConfirmadaEm = NULL, v.saidaConfirmadaPorId = NULL
            WHERE v.id = :veiculo AND v.saidaConfirmadaEm IS NOT NULL
            """)
    int desfazerConfirmacaoSaida(@Param("veiculo") UUID veiculo);
}
