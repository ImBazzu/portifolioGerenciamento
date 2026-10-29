package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaDuplicadoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class PessoaCommandEditarHandler {
    
    private final PessoaCommandPorta porta;
    
    @Transactional
    public void handler(PessoaCommandEditar command) {
        var pessoa = porta.buscarPorId(command.id()).orElseThrow(
            ()->new PessoaNaoEncontradoException(command.id())
        );
        var novoCpf = new Cpf(command.cpf());
        
        if(porta.existeCpf(novoCpf.valor(), command.id())){
            throw new PessoaDuplicadoException(novoCpf);
        }

        var observacao = command.observacao() == null || command.observacao().isBlank() ? null : new Observacao(command.observacao());

        pessoa.editar(new Nome(command.nome()),  observacao, command.prioridade(),
                new Endereco(command.endereco()), novoCpf, new Telefone(command.telefone()));
        porta.salvar(pessoa);
    }
}
