package br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscarArquivado;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaQueryBuscarArquivadoHandler {

    private final PessoaPorta porta;

    @Transactional(readOnly = true)
    public Pessoa handler(PessoaQueryBuscarArquivado query) {
        return porta.buscarArquivado(query.id())
                .orElseThrow(() -> new PessoaNaoEncontradoException(query.id()));
    }
}
