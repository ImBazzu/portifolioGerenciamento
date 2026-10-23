package br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "listas")
@NoArgsConstructor
@Getter
@Setter
public class TipoListaJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 59)
    private String nome;

    @Column(name = "descricao", length = 249)
    private String descricao;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RegraCadastroJpa> regrasCadastro = new ArrayList<>();

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RegraSeparacaoJpa> regrasSeparacao = new ArrayList<>();
}
