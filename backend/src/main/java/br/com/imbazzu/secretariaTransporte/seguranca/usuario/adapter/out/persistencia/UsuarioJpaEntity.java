package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia;


import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.UsuarioRoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Table(name = "usuarios",
        uniqueConstraints = {@UniqueConstraint(name = "uk_usuario_nome",columnNames = "nome")})
@Entity
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;
    private String senha;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_permissoes", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permissao")
    private List<UsuarioRoleEnum> permissoes;

    protected UsuarioJpaEntity() {}

    public static UsuarioJpaEntity fromDomain(Usuario usuario) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.id = usuario.getId();
        entity.nome = usuario.getNome().valor();
        entity.senha = usuario.getSenha();
        // reconstrói permissoes a partir das strings "ROLE_ADMIN" → enum
        entity.permissoes = usuario.getPermissoes().stream()
                .map(r -> r.replace("ROLE_", ""))
                .map(UsuarioRoleEnum::valueOf)
                .toList();
        return entity;
    }

    public Usuario toDomain() {
        return Usuario.reconstruir(id, new Nome(nome), senha, permissoes);
    }
}
