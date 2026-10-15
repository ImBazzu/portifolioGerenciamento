//package br.com.imbazzu.secretariaTransporte.operacao.viagem;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface ViagemRepository extends CrudRepository<Viagem, UUID> {
//
//    List<Viagem> findAllByData(LocalDate date);
//
//    @Query("""
//        SELECT v FROM Viagem v JOIN FETCH v.motorista m WHERE m.id = :idMotorista
//                AND((:inicioProcura IS NULL AND :finalProcura IS NULL)
//                        OR (v.data >= :inicioProcura AND v.data <= :finalProcura))
//        """)
//    Page<Viagem> findAllByMotorista(UUID idMotorista, LocalDate inicioProcura,
//                                    LocalDate finalProcura, Pageable pagina);
//
//    List<Viagem> findAllByMotorista_id(UUID idMotorista);
//}
