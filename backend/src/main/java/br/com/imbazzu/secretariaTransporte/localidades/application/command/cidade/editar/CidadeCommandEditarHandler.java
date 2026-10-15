package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.editar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.valueobject.TempoViagem;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CidadeCommandEditarHandler {

    private final CidadeCommandPorta porta;

    public void handler(CidadeCommandEditar command) {

        var cidade = porta.buscarPorId(command.id()).orElseThrow(
                () -> new RuntimeException("Cidade não encontrada"));
        cidade.editar(new Nome(command.nome()), CidadeEstadoEnum.valueOf(command.estado()),
                new TempoViagem(command.horas(), command.minutos()));
        porta.salvar(cidade);
    }
}
