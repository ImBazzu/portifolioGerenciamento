package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.mapper.EspecificacaoBancoMapper;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.repositorio.EspecificacaoRepository;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaDuplicadoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PessoaCommandSalvarHandler {
    
    private final PessoaCommandPorta porta;
    private final EspecificacaoRepository especificacaoRepository;
    
    @Transactional
    public UUID handler(PessoaCommandSalvar command){
        
        var observacao = command.observacao() == null || command.observacao().isBlank()
                ? null
                : new Observacao(command.observacao());
        var cpf = command.cpf()==null || command.cpf().isBlank()
                ? null
                : new Cpf(command.cpf());
        
        if(cpf != null){
            if(porta.existeCpf(cpf.valor(), null)){
                throw new PessoaDuplicadoException(cpf);
            }
        }

        var endereco = command.endereco() == null || command.endereco().isBlank()
                ? null
                : new Endereco(command.endereco());
        
        var especificacaoJpa = especificacaoRepository.findById(command.especificacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Especificação não encontrada"));
        
        var especificacao = EspecificacaoBancoMapper.paraDomain(especificacaoJpa);
        
        var pessoa = Pessoa.criar(new Nome(command.nome()), observacao, especificacao,
                false, 0,
                endereco, cpf, new Telefone(command.telefone()));
        
     
        
        return porta.salvar(pessoa);
    
    }
}
