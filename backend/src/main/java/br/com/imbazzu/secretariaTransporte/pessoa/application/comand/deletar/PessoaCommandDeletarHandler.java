package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.deletar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaCommandDeletarHandler {
    
    private final PessoaCommandPorta porta;

    @Transactional
    public void handler(PessoaCommandDeletar command){
        var pessoa = porta.buscarPorId(command.id()).orElseThrow(
            ()->new PessoaNaoEncontradoException(command.id())
        );
        pessoa.garantirNaoValidada();
        porta.deletar(command.id());
    }
}
