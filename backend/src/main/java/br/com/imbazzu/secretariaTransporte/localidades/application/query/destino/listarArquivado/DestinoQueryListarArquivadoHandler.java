package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listarArquivado;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta.DestinoOutputArquivado;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoQueryListarArquivadoHandler {

    private final DestinoQueryPorta porta;

    private final CidadeQueryPorta cidadePorta;

    public ResultadoPaginado<DestinoOutputArquivado> handler(DestinoQueryListarArquivado query) {
        var existeCidade = cidadePorta.existeCidade(query.idCidade());
        if (!existeCidade) {
            throw new CidadeNaoEncontradaException(query.idCidade());
        }
        return porta.listarPorCidadeArquivado(
                query.idCidade(), query.nome(), query.pagina(), query.tamanho(), query.ordenacao());
    }
}
