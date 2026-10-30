package br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaQueryBuscarHandler {

    private final PessoaPorta porta;

    @Transactional(readOnly = true)
    public Pessoa handler(PessoaQueryBuscar query) {
        return porta.buscar(query.id())
                .orElseThrow(() -> new PessoaNaoEncontradoException(query.id()));
    }
}
