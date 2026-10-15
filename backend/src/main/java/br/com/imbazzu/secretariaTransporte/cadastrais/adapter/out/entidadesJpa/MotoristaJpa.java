package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "motoristas",
        uniqueConstraints = @UniqueConstraint(name = "uk_motorista_cpf",
                columnNames = {"cpf"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MotoristaJpa {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(name = "nome", nullable = false, length = 60)
        private String nome;

        @Column(name = "cpf", nullable = false, length = 11)
        private String cpf;

        @Column(name = "telefone",nullable = false, length = 11)
        private String telefone;

        @OneToMany(mappedBy = "motoristaJpa")
        private List<MotoristaVeiculoJpa> listaVeiculos;

        @Column(name = "arquivado", nullable = false)
        private boolean arquivado;

}
