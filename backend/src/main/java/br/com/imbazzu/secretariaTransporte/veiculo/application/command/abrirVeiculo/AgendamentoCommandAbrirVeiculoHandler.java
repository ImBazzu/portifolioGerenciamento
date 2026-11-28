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
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;
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
        var finalidade = command.finalidadeAbertura() == null
                ? FinalidadeAberturaVeiculo.AJUSTE
                : command.finalidadeAbertura();
        boolean carro = VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome());
        boolean atendimentoEspecial = finalidade != FinalidadeAberturaVeiculo.AJUSTE;
        if (atendimentoEspecial && (!carro || tipo.capacidade() != 4)) {
            throw new DadosInvalidosException("Emergência e alta devem ser abertas como carro de quatro lugares");
        }
        if (!atendimentoEspecial && command.idListaAbertura() != null) {
            throw new DadosInvalidosException("Informe uma lista somente para emergência ou alta");
        }
        if (atendimentoEspecial && (command.horarioSaida() == null || command.idListaAbertura() == null)) {
            throw new DadosInvalidosException("Informe a lista e o horário de saída para emergência ou alta");
        }
        if (command.idListaAbertura() != null && !consultas.listaAtiva(command.idListaAbertura())) {
            throw new DadosInvalidosException("Selecione uma lista ativa para o atendimento");
        }
        if (!carro && command.horarioSaida() == null) {
            throw new DadosInvalidosException("Informe o horário de saída para este tipo de veículo");
        }
        if (carro && !atendimentoEspecial && command.horarioSaida() != null) {
            throw new DadosInvalidosException("Horário de saída manual disponível somente para emergência ou alta");
        }
        boolean calculaSaida = carro && !atendimentoEspecial;
        var identificacao = command.identificacao();
        var veiculo = new VeiculoAberto(
                UUID.randomUUID(),
                command.data(),
                command.idTipoVeiculo(),
                tipo.nome(),
                tipo.capacidade(),
                identificacao == null || identificacao.isBlank() ? null : identificacao.trim(),
                command.horarioSaida(),
                calculaSaida,
                null,
                finalidade,
                command.idListaAbertura());
        return comandos.salvar(veiculo);
    }
}
