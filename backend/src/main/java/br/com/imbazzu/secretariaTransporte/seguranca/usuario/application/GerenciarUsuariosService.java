package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        if (pagina < 0) throw new RegraDeNegocioException("Página inválida.");
        return usuarios.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim(), PageRequest.of(pagina, 20))
                .map(GerenciarUsuariosService::resumo);
    }

    @Transactional(readOnly = true)
    public UsuarioResumo buscar(UUID id) {
        return resumo(encontrar(id));
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
        var administradores = jdbc.queryForList(
                "SELECT DISTINCT usuario_id FROM usuario_permissoes WHERE permissao = 'ADMIN' FOR UPDATE", byte[].class);
        var usuario = encontrar(id);
        if (usuario.getPermissoes().contains(UsuarioRoleEnum.ADMIN)
                && novoPerfil != UsuarioRoleEnum.ADMIN && administradores.size() <= 1) {
            throw new RegraDeNegocioException("O último administrador não pode perder o perfil de administrador.");
        }
        usuario.setPermissoes(new ArrayList<>(java.util.List.of(novoPerfil)));
        usuarios.saveAndFlush(usuario);
    }

    @Transactional
    public TokenResponseDto atualizarProprio(UUID id, String nome, String senhaAtual, String novaSenha) {
        var usuario = usuarios.buscarParaAtualizacao(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Usuário não encontrado."));
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
