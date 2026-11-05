package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceSalvarEntidade;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandSalvarHandle {

    private final TipoVeiculoCommandPorta commandPorta;

    private final TipoVeiculoServiceSalvarEntidade serviceSalvarEntidade;


    public UUID handler(TipoVeiculoCommandSalvar dto) {
        // Cria uma nova instância de TipoVeiculo usando o serviço de salvar entidade
        var veiculo = serviceSalvarEntidade.executar(dto.nome(), dto.capacidade());
        // Salva a entidade no repositório e retorna o ID gerado
        return commandPorta.salvar(veiculo);

    }
}
