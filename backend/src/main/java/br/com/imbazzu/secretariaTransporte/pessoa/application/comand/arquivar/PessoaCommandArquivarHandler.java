package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.arquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class PessoaCommandArquivarHandler {

    private final PessoaCommandPorta porta;
    
    @Transactional
    public void handler(PessoaCommandArquivar command) {
        var pessoa = porta.buscarPorId(command.id()).orElseThrow(
            ()->new PessoaNaoEncontradoException(command.id())
        );
        pessoa.arquivar();
        porta.salvar(pessoa);
    }
}
