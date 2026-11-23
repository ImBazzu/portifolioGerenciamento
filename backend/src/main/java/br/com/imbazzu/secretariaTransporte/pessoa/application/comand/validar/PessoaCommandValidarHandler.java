package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.validar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaCommandValidarHandler {

    private final PessoaCommandPorta porta;

    @Transactional
    public void handler(UUID id) {
        var pessoa = porta.buscarPorId(id).orElseThrow(() -> new PessoaNaoEncontradoException(id));
        pessoa.validar();
        porta.salvar(pessoa);
    }
}
