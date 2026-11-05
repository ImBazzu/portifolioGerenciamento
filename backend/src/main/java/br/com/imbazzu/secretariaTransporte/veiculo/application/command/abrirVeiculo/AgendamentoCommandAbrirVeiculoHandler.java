package br.com.imbazzu.secretariaTransporte.veiculo.application.command.abrirVeiculo;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandAbrirVeiculoHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public VeiculoAberto handler(AgendamentoCommandAbrirVeiculo command) {
        AgendamentoValidacao.exigirDiaAberto(consultas, command.data());
        var tipo = consultas.buscarTipoVeiculo(command.idTipoVeiculo());
        if (!tipo.ativo()) {
            throw new OperacaoNaoPermitidaException("Não é possível abrir um tipo de veículo arquivado");
        }
        boolean calculaSaida = VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome());
        if (calculaSaida && command.horarioSaida() != null) {
            throw new DadosInvalidosException("O horário de saída de veículos do tipo Carro é calculado");
        }
        if (!calculaSaida && command.horarioSaida() == null) {
            throw new DadosInvalidosException("Informe o horário de saída para este tipo de veículo");
        }
        var identificacao = command.identificacao();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(),
                command.data(),
                command.idTipoVeiculo(),
                tipo.nome(),
                tipo.capacidade(),
                identificacao == null || identificacao.isBlank() ? null : identificacao.trim(),
                command.horarioSaida(),
                calculaSaida);
        return comandos.salvar(veiculo);
    }
}
