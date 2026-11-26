package br.com.imbazzu.secretariaTransporte.motorista;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categorias_motorista")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaMotoristaJpa {
    @Id
    private UUID id;
    @Column(nullable = false, length = 100)
    private String nome;
}
