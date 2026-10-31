package br.com.imbazzu.secretariaTransporte.lista.application.command.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TipoListaCommandSalvarHandler {
    
    private final TipoListaPorta porta;


    @Transactional
    public UUID handler(TipoListaCommandSalvar command) {
        var descricao = command.descricao() == null || command.descricao().isBlank() ? null : new Observacao(command.descricao());
        return porta.salvar(TipoLista.criar(new Nome(command.nome()), descricao));
    }
}
