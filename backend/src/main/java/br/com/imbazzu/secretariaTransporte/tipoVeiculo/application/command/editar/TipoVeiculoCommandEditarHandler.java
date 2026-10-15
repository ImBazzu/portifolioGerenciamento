package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.editar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandEditarHandler {

    private final TipoVeiculoCommandPorta commandPorta;

    public void handler(TipoVeiculoCommandEditar dto) {
        var veiculo = commandPorta.buscarPorId(dto.id());

        veiculo.editarVeiculo(
                new NomeVeiculo(dto.nome()),
                dto.capacidade());

        var nomeDuplicado = commandPorta.verificarPorNomeIgnorandoId(veiculo.getId(), veiculo.getNome());

        if (nomeDuplicado) {
            throw new TipoVeiculoDuplicadoException(veiculo.getNome());
        }

        commandPorta.salvar(veiculo);
    }
}
