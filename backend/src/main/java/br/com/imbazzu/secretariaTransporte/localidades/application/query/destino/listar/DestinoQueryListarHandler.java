package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta.DestinoOutput;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoQueryListarHandler {

        private final DestinoQueryPorta porta;

        private final CidadeQueryPorta cidadePorta;

        public ResultadoPaginado<DestinoOutput> handler(DestinoQueryListar query) {
                var existeCidade = cidadePorta.existeCidade(query.idCidade());

                if (!existeCidade) {
                        throw new CidadeNaoEncontradaException(query.idCidade());
                }

                return porta.listarPorCidade(query.idCidade(), query.nomeDestino(), query.pagina(),
                                query.tamanho(), query.ordenacao());

        }
}
