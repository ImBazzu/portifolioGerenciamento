package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.jpaAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.DiaAgendamentoJpa;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.VeiculoAgendaJpa;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.AgendamentoRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.DiaAgendamentoRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.VeiculoAgendaRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgendamentoCommandJpaAdapter implements AgendamentoCommandPorta {

    private final AgendamentoRepository agendamentoRepository;
    private final DiaAgendamentoRepository diaRepository;
    private final VeiculoAgendaRepository veiculoRepository;

    @Override
    @Transactional
    public void abrirDia(LocalDate data) {
        var dia = new DiaAgendamentoJpa();
        dia.setData(data);
        dia.setAbertoEm(LocalDateTime.now());
        diaRepository.saveAndFlush(dia);
    }

    @Override
    @Transactional
    public UUID salvar(Agendamento agendamento) {
        var entidade = agendamentoRepository.findById(agendamento.getId()).orElseGet(AgendamentoJpa::new);
        entidade.setId(agendamento.getId());
        entidade.setData(agendamento.getData());
        entidade.setIdPessoa(agendamento.getIdPessoa());
        entidade.setIdLista(agendamento.getIdLista());
        entidade.setIdRegraCadastro(agendamento.getIdRegraCadastro());
        entidade.setIdTipoVeiculo(agendamento.getIdTipoVeiculo());
        entidade.setIdDestino(agendamento.getIdDestino());
        entidade.setTempoViagem(agendamento.getTempoViagem());
        entidade.setTolerancia(agendamento.getToleranciaSeparacao());
        entidade.setHorarioChegada(agendamento.getHorarioChegada());
        entidade.setBuscarNaResidencia(agendamento.isBuscarNaResidencia());
        entidade.setAcompanhantes(agendamento.getAcompanhantes());
        entidade.setIdVeiculoAberto(agendamento.getIdVeiculoAberto());
        return agendamentoRepository.saveAndFlush(entidade).getId();
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        agendamentoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VeiculoAberto salvar(VeiculoAberto veiculo) {
        var entidade = new VeiculoAgendaJpa();
        entidade.setId(veiculo.id());
        entidade.setData(veiculo.data());
        entidade.setIdTipoVeiculo(veiculo.idTipoVeiculo());
        entidade.setNomeTipoVeiculo(veiculo.nomeTipoVeiculo());
        entidade.setCapacidade(veiculo.capacidade());
        entidade.setIdentificacao(veiculo.identificacao());
        entidade.setHorarioSaida(veiculo.horarioSaida());
        entidade.setHorarioSaidaCalculado(veiculo.horarioSaidaCalculado());
        veiculoRepository.saveAndFlush(entidade);
        return veiculo;
    }
}
