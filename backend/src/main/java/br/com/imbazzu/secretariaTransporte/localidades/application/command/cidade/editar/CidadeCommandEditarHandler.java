package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.editar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeDuplicadoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeNaoEncontradaException;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.valueobject.TempoViagem;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CidadeCommandEditarHandler {

    private final CidadeCommandPorta porta;
    private final CidadeQueryPorta queryPorta;

    @Transactional
    public void handler(CidadeCommandEditar command) {

        var cidade = porta.buscarPorId(command.id()).orElseThrow(
                () -> new CidadeNaoEncontradaException(command.id()));
        var nome = new Nome(command.nome());
        var estado = CidadeEstadoEnum.valueOf(command.estado());
        if (queryPorta.verificarCidadeCadastradaIgnorandoId(command.id(), nome.valor(), estado)) {
            throw new CidadeDuplicadoException(estado, nome);
        }

        cidade.editar(nome, estado,
                new TempoViagem(command.horas(), command.minutos()));
        porta.salvar(cidade);
    }
}
