package br.com.imbazzu.secretariaTransporte.pessoa.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaApplication {

    private final PessoaPorta pessoaPorta;


    @Transactional(readOnly = true)
    public ResultadoPaginado<Pessoa> listar(String nome, boolean arquivado, int pagina, int tamanho) {
        if (pagina < 0) {
            throw new DadosInvalidosException("A página não pode ser negativa");
        }
        if (tamanho < 1 || tamanho > 100) {
            throw new DadosInvalidosException("O tamanho da página deve estar entre 1 e 100");
        }
        return pessoaPorta.listar(nome == null ? "" : nome.trim(), arquivado, pagina, tamanho);
    }
}
