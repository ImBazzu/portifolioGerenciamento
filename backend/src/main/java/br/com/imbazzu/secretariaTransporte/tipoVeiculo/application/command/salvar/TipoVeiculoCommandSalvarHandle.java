package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandSalvarHandle {

    private final TipoVeiculoCommandPorta commandPorta;

    public UUID handler(TipoVeiculoCommandSalvar dto) {
        var veiculo = TipoVeiculo.criar(new NomeVeiculo(dto.nome()), dto.capacidade());

        var nomeDuplicado = commandPorta.verificarPorNome(veiculo.getNome());
        if (nomeDuplicado) {
            throw new TipoVeiculoDuplicadoException(veiculo.getNome());
        }
        return commandPorta.salvar(veiculo);

    }
}
