package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.buscar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta.DestinoOutput;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoQueryBuscarHandler {

    private final DestinoQueryPorta porta;

    public DestinoOutput handler(DestinoQueryBuscar query) {
        return porta.buscarPorId(query.id()).orElseThrow(
                () -> new DestinoNaoEncontradoException(query.id()));
    }
}
