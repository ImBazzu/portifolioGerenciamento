package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;

@Entity
@Table(name = "motorista_veiculo", uniqueConstraints = @UniqueConstraint(name = "uk_motoristaveiculo_idmotorista_idveiculo", columnNames = {
                "motorista_id", "veiculo_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotoristaVeiculoJpa {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne
        @JoinColumn(name = "motorista_id")
        private MotoristaJpa motoristaJpa;

        @ManyToOne
        @JoinColumn(name = "veiculo_id")
        private TipoVeiculoJpa veiculoJpa;

}
