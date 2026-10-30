package br.com.imbazzu.secretariaTransporte.pessoa.application.query.listarArquivado;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.application.PessoaApplication;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaQueryListarArquivadoHandler {

    private final PessoaApplication application;

    @Transactional(readOnly = true)
    public ResultadoPaginado<Pessoa> handler(PessoaQueryListarArquivado query) {
        return application.listar(query.nome(), true, query.pagina(), query.tamanho());
    }
}
