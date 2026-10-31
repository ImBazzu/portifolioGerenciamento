package br.com.imbazzu.secretariaTransporte.lista.application.command.editar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.core.excecoes.TipoListaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TipoListaCommandEditarHandler {
    
    private final TipoListaPorta porta;

    @Transactional 
    public void handler(TipoListaCommandEditar command){
        var lista = porta.buscarPorId(command.id()).orElseThrow(
            ()->new TipoListaNaoEncontradoException(command.id())
        );

        var descricao = command.descricao() == null || command.descricao().isBlank() ? null : new Observacao(command.descricao());

        lista.editar(new Nome(command.nome()),descricao);
        porta.salvar(lista);
    }

}
