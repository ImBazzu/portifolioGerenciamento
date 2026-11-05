package br.com.imbazzu.secretariaTransporte.veiculo.application.query.listarVeiculos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryListarVeiculosHandler {

    private final AgendamentoQueryPorta consultas;

    @Transactional(readOnly = true)
    public List<VeiculoAberto> handler(AgendamentoQueryListarVeiculos query) {
        AgendamentoValidacao.exigirDiaAberto(consultas, query.data());
        return consultas.listarVeiculos(query.data());
    }
}
