package br.com.imbazzu.secretariaTransporte.lista.application.query.buscar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ListaQueryBuscarHandler {
    
    private final TipoListaPorta porta;

    @Transactional(readOnly = true)
    public TipoLista handler(UUID id) {
        porta
        return buscarObrigatorio(id);
    }
}
