package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia.UsuarioJpaEntity;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia.UsuarioJpaRepository;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.UsuarioRoleEnum;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;

@Service
public class GerenciarUsuariosService {
    public record UsuarioResumo(UUID id, String nome, String perfil) {}
    public record ListaAgendamento(UUID id, String nome) {}
    public record VisibilidadeListas(boolean restrita, List<UUID> idsListas, List<ListaAgendamento> listas) {}
    private final UsuarioJpaRepository usuarios;
    private final PasswordEncoder senhas;
    private final TokenPorta tokens;
    private final JdbcTemplate jdbc;

    public GerenciarUsuariosService(UsuarioJpaRepository usuarios, PasswordEncoder senhas, TokenPorta tokens,
            JdbcTemplate jdbc) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
        this.jdbc = jdbc;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResumo> listar(String nome, int pagina) {
        return listar(nome, false, pagina);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResumo> listar(String nome, boolean arquivados, int pagina) {
        if (pagina < 0) throw new RegraDeNegocioException("Página inválida.");
        var resultado = arquivados
                ? usuarios.findByNomeContainingIgnoreCaseAndDataArquivadoIsNotNullOrderByNomeAsc(nome.trim(), PageRequest.of(pagina, 20))
                : usuarios.findByNomeContainingIgnoreCaseAndDataArquivadoIsNullOrderByNomeAsc(nome.trim(), PageRequest.of(pagina, 20));
        return resultado
                .map(GerenciarUsuariosService::resumo);
    }

    @Transactional(readOnly = true)
    public UsuarioResumo buscar(UUID id) {
        return resumo(encontrar(id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResumo> listarParaVisibilidadeListas() {
        return jdbc.query("""
                SELECT BIN_TO_UUID(u.id) AS id, u.nome,
                       COALESCE(MAX(CASE p.permissao
                           WHEN 'ADMIN' THEN 3 WHEN 'GESTAO' THEN 2
                           WHEN 'MOTORISTA' THEN 1 ELSE 0 END), 0) AS nivel
                FROM usuarios u
                LEFT JOIN usuario_permissoes p ON p.usuario_id = u.id
                WHERE u.data_arquivado IS NULL
                GROUP BY u.id, u.nome
                ORDER BY u.nome
                """, (rs, row) -> new UsuarioResumo(
                UUID.fromString(rs.getString("id")),
                rs.getString("nome"),
                switch (rs.getInt("nivel")) {
                    case 3 -> "ADMIN";
                    case 2 -> "GESTAO";
                    case 1 -> "MOTORISTA";
                    default -> "AGENDAMENTO";
                }));
    }

    @Transactional(readOnly = true)
    public VisibilidadeListas buscarVisibilidadeListas(UUID usuarioId) {
        if (!usuarios.existsById(usuarioId)) {
            throw new EntidadeNaoEncontradoException("Usuário não encontrado.");
        }
        boolean restrita = Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT restricao_listas_agendamento FROM usuarios WHERE id = UUID_TO_BIN(?)",
                Boolean.class, usuarioId.toString()));
        var idsSelecionados = jdbc.query(
                "SELECT BIN_TO_UUID(lista_id) AS id FROM usuario_listas_agendamento WHERE usuario_id = UUID_TO_BIN(?)",
                (rs, row) -> UUID.fromString(rs.getString("id")), usuarioId.toString());
        var listas = jdbc.query("""
                SELECT id, nome FROM (
                    SELECT BIN_TO_UUID(id) AS id, nome FROM listas WHERE data_arquivado IS NULL
                ) ativas ORDER BY nome
                """, (rs, row) -> new ListaAgendamento(UUID.fromString(rs.getString("id")), rs.getString("nome")));
        return new VisibilidadeListas(restrita, idsSelecionados, listas);
    }

    @Transactional
    public void atualizarVisibilidadeListas(UUID usuarioId, boolean restrita, List<UUID> idsListas) {
        if (!usuarios.existsById(usuarioId)) {
            throw new EntidadeNaoEncontradoException("Usuário não encontrado.");
        }
        var idsUnicos = idsListas == null ? List.<UUID>of() : idsListas.stream().distinct().toList();
        if (restrita && !idsUnicos.isEmpty()) {
            int listasValidas = jdbc.queryForObject("""
                    SELECT COUNT(*) FROM listas
                    WHERE data_arquivado IS NULL
                      AND id IN (%s)
                    """.formatted(String.join(",", java.util.Collections.nCopies(idsUnicos.size(), "UUID_TO_BIN(?)"))),
                    Integer.class, idsUnicos.stream().map(UUID::toString).toArray());
            if (listasValidas != idsUnicos.size()) {
                throw new DadosInvalidosException("Uma ou mais listas selecionadas não estão ativas.");
            }
        }
        jdbc.update("DELETE FROM usuario_listas_agendamento WHERE usuario_id = UUID_TO_BIN(?)",
                usuarioId.toString());
        if (restrita) {
            jdbc.batchUpdate(
                    "INSERT INTO usuario_listas_agendamento (usuario_id, lista_id) VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?))",
                    idsUnicos,
                    idsUnicos.size(),
                    (statement, idLista) -> {
                        statement.setString(1, usuarioId.toString());
                        statement.setString(2, idLista.toString());
                    });
        }
        jdbc.update("UPDATE usuarios SET restricao_listas_agendamento = ? WHERE id = UUID_TO_BIN(?)",
                restrita, usuarioId.toString());
    }

    @Transactional
    public UsuarioResumo criar(String nome, String senha, String perfil) {
        String nomeValido = validarNome(nome);
        validarSenha(senha);
        var usuario = Usuario.criar(new Nome(nomeValido), senhas.encode(senha), perfil(perfil));
        if (usuarios.existsByNomeIgnoreCaseAndIdNot(nomeValido, usuario.getId())) {
            throw new EntidadeDuplicadaException("Já existe um usuário com este nome.");
        }
        var entidade = UsuarioJpaEntity.fromDomain(usuario);
        salvar(entidade);
        return resumo(entidade);
    }

    @Transactional
    public void alterarPerfil(UUID id, String perfil) {
        var novoPerfil = perfil(perfil);
        var administradores = bloquearAdministradoresAtivos();
        var usuario = bloquear(id);
        if (usuario.getPermissoes().contains(UsuarioRoleEnum.ADMIN)
                && usuario.getDataArquivado() == null && novoPerfil != UsuarioRoleEnum.ADMIN && administradores <= 1) {
            throw new RegraDeNegocioException("O último administrador não pode perder o perfil de administrador.");
        }
        usuario.setPermissoes(new ArrayList<>(java.util.List.of(novoPerfil)));
        usuarios.saveAndFlush(usuario);
    }

    @Transactional
    public void alterarArquivamento(UUID id, UUID operador, boolean arquivar) {
        if (id.equals(operador)) throw new RegraDeNegocioException("Você não pode arquivar a própria conta.");
        long administradores = bloquearAdministradoresAtivos();
        var usuario = bloquear(id);
        if (arquivar && usuario.getDataArquivado() == null) {
            if (usuario.getPermissoes().contains(UsuarioRoleEnum.ADMIN) && administradores <= 1) {
                throw new RegraDeNegocioException("O último administrador ativo não pode ser arquivado.");
            }
            usuario.setDataArquivado(LocalDateTime.now());
            usuario.setVersaoToken(usuario.getVersaoToken() + 1);
        } else if (!arquivar) {
            usuario.setDataArquivado(null);
        }
        usuarios.saveAndFlush(usuario);
    }

    @Transactional
    public void excluir(UUID id, UUID operador) {
        if (id.equals(operador)) throw new RegraDeNegocioException("Você não pode excluir a própria conta.");
        long administradores = bloquearAdministradoresAtivos();
        var usuario = bloquear(id);
        if (usuario.getDataArquivado() == null && usuario.getPermissoes().contains(UsuarioRoleEnum.ADMIN)
                && administradores <= 1) {
            throw new RegraDeNegocioException("O último administrador ativo não pode ser excluído.");
        }
        Integer vinculos = jdbc.queryForObject("""
                SELECT (SELECT COUNT(*) FROM pessoas WHERE autor_id = UUID_TO_BIN(?))
                     + (SELECT COUNT(*) FROM agendamentos WHERE autor_id = UUID_TO_BIN(?))
                """, Integer.class, id.toString(), id.toString());
        if (vinculos != null && vinculos > 0) {
            throw new RegraDeNegocioException("Usuário vinculado a cadastros não pode ser excluído; arquive-o.");
        }
        try {
            usuarios.delete(usuario);
            usuarios.flush();
        } catch (DataIntegrityViolationException e) {
            if (causadaPor(e, "fk_pessoa_autor") || causadaPor(e, "fk_agendamento_autor")) {
                throw new RegraDeNegocioException("Usuário vinculado a cadastros não pode ser excluído; arquive-o.");
            }
            throw e;
        }
    }

    @Transactional
    public TokenResponseDto atualizarProprio(UUID id, String nome, String senhaAtual, String novaSenha) {
        var usuario = usuarios.buscarParaAtualizacao(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Usuário não encontrado."));
        if (usuario.getDataArquivado() != null) throw new RegraDeNegocioException("Usuário arquivado.");
        if (senhaAtual == null || !senhas.matches(senhaAtual, usuario.getSenha())) {
            throw new RegraDeNegocioException("Senha atual incorreta.");
        }
        String nomeValido = validarNome(nome);
        if (usuarios.existsByNomeIgnoreCaseAndIdNot(nomeValido, id)) {
            throw new EntidadeDuplicadaException("Já existe um usuário com este nome.");
        }
        if (novaSenha != null && !novaSenha.isBlank()) {
            validarSenha(novaSenha);
            usuario.setSenha(senhas.encode(novaSenha));
            usuario.setVersaoToken(usuario.getVersaoToken() + 1);
        }
        usuario.setNome(nomeValido);
        salvar(usuario);
        var domain = usuario.toDomain();
        return new TokenResponseDto(tokens.gerarTokenAcesso(domain), tokens.gerarTokenAtualizacao(domain));
    }

    private UsuarioJpaEntity encontrar(UUID id) {
        return usuarios.findById(id).orElseThrow(() -> new EntidadeNaoEncontradoException("Usuário não encontrado."));
    }

    private UsuarioJpaEntity bloquear(UUID id) {
        return usuarios.buscarParaAtualizacao(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Usuário não encontrado."));
    }

    private long bloquearAdministradoresAtivos() {
        return jdbc.queryForList("""
                SELECT u.id FROM usuarios u JOIN usuario_permissoes p ON p.usuario_id = u.id
                WHERE p.permissao = 'ADMIN' AND u.data_arquivado IS NULL ORDER BY u.id FOR UPDATE
                """, byte[].class).stream().map(java.util.Arrays::toString).distinct().count();
    }

    private static boolean causadaPor(Throwable e, String constraint) {
        for (Throwable causa = e; causa != null; causa = causa.getCause()) {
            if (causa.getMessage() != null && causa.getMessage().contains(constraint)) return true;
        }
        return false;
    }

    private void salvar(UsuarioJpaEntity usuario) {
        try {
            usuarios.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            for (Throwable causa = e; causa != null; causa = causa.getCause()) {
                if (causa.getMessage() != null && causa.getMessage().contains("uk_usuario_nome")) {
                    throw new EntidadeDuplicadaException("Já existe um usuário com este nome.");
                }
            }
            throw e;
        }
    }

    private static UsuarioResumo resumo(UsuarioJpaEntity usuario) {
        var perfil = usuario.getPermissoes().stream().max(java.util.Comparator.comparingInt(UsuarioRoleEnum::getNivel))
                .orElseThrow(() -> new IllegalStateException("Usuário sem perfil."));
        return new UsuarioResumo(usuario.getId(), usuario.getNome(),
                perfil == UsuarioRoleEnum.USUARIO ? "AGENDAMENTO" : perfil.name());
    }

    private static UsuarioRoleEnum perfil(String valor) {
        if (valor != null) {
            try {
                var perfil = UsuarioRoleEnum.valueOf(valor);
                if (perfil != UsuarioRoleEnum.USUARIO) return perfil;
            } catch (IllegalArgumentException ignored) {
                // O valor é tratado como perfil inválido abaixo.
            }
        }
        throw new RegraDeNegocioException("Perfil inválido.");
    }

    private static String validarNome(String valor) {
        try {
            return new Nome(valor).valor();
        } catch (DadosInvalidosException e) {
            throw new RegraDeNegocioException(e.getMessage());
        }
    }

    private static void validarSenha(String senha) {
        if (senha == null || senha.length() < 8 || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new RegraDeNegocioException("A senha deve ter ao menos 8 caracteres e no máximo 72 bytes.");
        }
    }
}
