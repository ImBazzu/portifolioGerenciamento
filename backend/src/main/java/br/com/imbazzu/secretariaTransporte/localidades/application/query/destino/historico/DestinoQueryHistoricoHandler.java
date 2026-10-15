package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.historico;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta.DestinoHistoricoOutput;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoQueryHistoricoHandler {

    private final DestinoQueryPorta porta;

    public ResultadoPaginado<DestinoHistoricoOutput> handler(DestinoQueryHistorico query) {
        var existeDestino = porta.verificarExistenciaPorId(query.id());
        if (!existeDestino) {
            throw new DestinoNaoEncontradoException(query.id());
        }
        return porta.buscarHistorico(query.id(), query.pagina(), query.tamanhoPagina());
    }
}
