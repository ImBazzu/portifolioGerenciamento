package br.com.imbazzu.secretariaTransporte.agendamento.application.query.listarPorData;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryListarPorDataHandler {

    private final AgendamentoQueryPorta consultas;

    @Transactional(readOnly = true)
    public List<Agendamento> handler(AgendamentoQueryListarPorData query) {
        AgendamentoValidacao.exigirDiaAberto(consultas, query.data());
        return consultas.listarAgendamentos(query.data());
    }
}
