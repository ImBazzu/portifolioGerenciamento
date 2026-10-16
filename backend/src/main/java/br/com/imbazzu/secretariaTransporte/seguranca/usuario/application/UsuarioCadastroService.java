package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.UsuarioRoleEnum;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.CriptografiaSenhaPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.UsuarioRepositorioPorta;

public class UsuarioCadastroService implements UsuarioApplicationPorta {

    private final UsuarioRepositorioPorta repository;
    private final CriptografiaSenhaPorta criptografia;

    public UsuarioCadastroService(UsuarioRepositorioPorta repository, CriptografiaSenhaPorta criptografia) {
        this.repository = repository;
        this.criptografia = criptografia;
    }

    @Override
    public void cadastrar(String nome, String senha, String usuarioRole) {
        if (repository.existePorNome(nome)) {
            throw new IllegalArgumentException("Usuário já cadastrado: " + nome);
        }

        var usuario = Usuario.criar(new Nome(nome), criptografia.criptografar(senha), UsuarioRoleEnum.valueOf(usuarioRole));
        repository.salvar(usuario);
    }
}
