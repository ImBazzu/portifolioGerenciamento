package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.deletar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaCommandDeletarHandler {
    
    private final PessoaCommandPorta porta;

    public void handler(PessoaCommandDeletar command){
        porta.buscarPorId(command.id()).orElseThrow(
            ()->new PessoaNaoEncontradoException(command.id())
        );
        porta.deletar(command.id());
    }
}
