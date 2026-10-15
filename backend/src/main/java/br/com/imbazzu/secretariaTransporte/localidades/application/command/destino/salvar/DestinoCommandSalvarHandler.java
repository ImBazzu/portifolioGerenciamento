package br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Destino;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoCommandSalvarHandler {

    private final DestinoCommandPorta porta;

    private final DestinoQueryPorta query;

    private final CidadeQueryPorta cidadeQuery;

    public UUID handler(DestinoCommandSalvar command) {

        var existeCidade = cidadeQuery.existeCidade(command.idCidade());

        if (!existeCidade) {
            throw new CidadeNaoEncontradaException(command.idCidade());
        }

        var destino = Destino.criar(command.idCidade(), new Nome(command.nome()));

        var duplicidade = query.verificarDuplicidade(destino.getIdCidade(), destino.getNome());

        if (duplicidade) {
            throw new DestinoDuplicadoException(destino.getNome());
        }

        return porta.salvar(destino);
    }
}
