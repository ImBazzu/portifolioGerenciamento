package br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

public interface AgendamentoCommandPorta {

    void abrirDia(LocalDate data);

    UUID salvar(Agendamento agendamento);

    void deletar(UUID id);

    boolean cancelar(UUID id, LocalDateTime canceladoEm, String motivo, UUID autorId);

    boolean confirmarSaidaVeiculo(UUID id, LocalDateTime confirmadaEm, UUID autorId);

    boolean desfazerConfirmacaoSaidaVeiculo(UUID id);

    VeiculoAberto salvar(VeiculoAberto veiculo);

    void deletarVeiculo(UUID id);
}
