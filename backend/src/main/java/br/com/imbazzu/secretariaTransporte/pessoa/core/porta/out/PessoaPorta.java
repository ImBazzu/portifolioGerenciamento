package br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

public interface PessoaPorta {


    ResultadoPaginado<Pessoa> listar(String nome, boolean arquivado, int pagina, int tamanho);


}
