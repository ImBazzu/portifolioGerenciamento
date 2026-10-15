package br.com.imbazzu.secretariaTransporte.localidades.application.command.destino.editar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.DestinoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinoCommandEditarHandler {

    private final DestinoCommandPorta porta;

    private final DestinoQueryPorta query;

    public void handler(DestinoCommandEditar command) {
        var destino = porta.buscarPorId(command.id()).orElseThrow(
                () -> new DestinoNaoEncontradoException(command.id()));

        destino.alterarNome(new Nome(command.nome()));

        var duplicado = query.verificarDuplicidadeIgnorandoIdDestino(destino.getIdCidade(), destino.getNome(),
                destino.getId());

        if (duplicado) {
            throw new DestinoDuplicadoException(destino.getNome());
        }

        porta.salvar(destino);
    }
}
