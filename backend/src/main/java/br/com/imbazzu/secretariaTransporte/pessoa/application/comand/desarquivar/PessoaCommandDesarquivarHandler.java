package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.desarquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PessoaCommandDesarquivarHandler {
    
    private final PessoaCommandPorta porta;

    @Transactional 
    public void handler(PessoaCommandDesarquivar command){
        var pessoa = porta.buscarPorId(command.id()).orElseThrow(
            ()->new PessoaNaoEncontradoException(command.id())
        );
        pessoa.desarquivar();
        porta.salvar(pessoa);
    }
}
