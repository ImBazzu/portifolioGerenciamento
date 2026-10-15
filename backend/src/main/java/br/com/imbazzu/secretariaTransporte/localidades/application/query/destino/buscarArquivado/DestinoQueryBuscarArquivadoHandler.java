package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.buscarArquivado;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta.DestinoOutputArquivado;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoQueryBuscarArquivadoHandler {

    private final DestinoQueryPorta porta;

    public DestinoOutputArquivado handler(DestinoQueryBuscarArquivado query) {
        return porta.buscarPorIdArquivado(query.id()).orElseThrow(
                () -> new DestinoNaoEncontradoException(query.id()));
    }
}
