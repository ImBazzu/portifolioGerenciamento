package br.com.imbazzu.secretariaTransporte.motorista;

import java.util.UUID;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "motoristas")
@Getter
@Setter
@NoArgsConstructor
public class MotoristaJpa {
    @Id
    private UUID id;
    @Column(nullable = false, length = 150)
    private String nome;
    @Column(nullable = false, length = 11)
    private String telefone;
    @Column(name = "data_arquivado")
    private LocalDateTime dataArquivado;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaMotoristaJpa categoria;
}
