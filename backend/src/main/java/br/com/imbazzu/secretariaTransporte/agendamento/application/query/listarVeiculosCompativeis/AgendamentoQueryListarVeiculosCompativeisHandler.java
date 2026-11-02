package br.com.imbazzu.secretariaTransporte.agendamento.application.query.listarVeiculosCompativeis;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryListarVeiculosCompativeisHandler {

    private final AgendamentoQueryPorta consultas;

    @Transactional(readOnly = true)
    public Map<UUID, List<UUID>> handler(AgendamentoQueryListarVeiculosCompativeis query) {
        AgendamentoValidacao.exigirDiaAberto(consultas, query.data());
        return CompatibilidadeVeiculoAgendamento.listarCompativeis(
                consultas.listarAgendamentos(query.data()),
                consultas.listarVeiculos(query.data()));
    }
}
