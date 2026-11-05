package br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

public interface AgendamentoCommandPorta {

    void abrirDia(LocalDate data);

    UUID salvar(Agendamento agendamento);

    void deletar(UUID id);

    VeiculoAberto salvar(VeiculoAberto veiculo);

    void deletarVeiculo(UUID id);
}
