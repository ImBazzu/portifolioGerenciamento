package br.com.imbazzu.secretariaTransporte.pessoa.application.query.listar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.application.PessoaApplication;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaQueryListarHandler {

    private final PessoaApplication application;

    @Transactional(readOnly = true)
    public ResultadoPaginado<Pessoa> handler(PessoaQueryListar query) {
        return application.listar(query.nome(), false, query.pagina(), query.tamanho());
    }
}
