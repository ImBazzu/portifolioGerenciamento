package br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryBuscarHandler {

    private final AgendamentoQueryPorta consultas;

    @Transactional(readOnly = true)
    public Agendamento handler(AgendamentoQueryBuscar query) {
        return AgendamentoValidacao.buscarAgendamento(consultas, query.id());
    }
}
