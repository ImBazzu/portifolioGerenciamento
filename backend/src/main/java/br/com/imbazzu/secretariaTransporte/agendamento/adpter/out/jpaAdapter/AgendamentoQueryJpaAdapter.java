package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.jpaAdapter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.AgendamentoQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.DestinoAgendaQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.DiaAgendamentoQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.PessoaAgendaQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.TipoListaAgendaQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.TipoVeiculoAgendaQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.VeiculoAgendaQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.AgendamentoProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.VeiculoAgendaProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgendamentoQueryJpaAdapter implements AgendamentoQueryPorta {

    private final AgendamentoQueryRepository agendamentoRepository;
    private final DiaAgendamentoQueryRepository diaRepository;
    private final VeiculoAgendaQueryRepository veiculoRepository;
    private final PessoaAgendaQueryRepository pessoaRepository;
    private final TipoListaAgendaQueryRepository listaRepository;
    private final TipoVeiculoAgendaQueryRepository tipoVeiculoRepository;
    private final DestinoAgendaQueryRepository destinoRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean diaAberto(LocalDate data) {
        return diaRepository.existsById(data);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeAgendamento(LocalDate data, UUID idPessoa) {
        return agendamentoRepository.existsByDataAndIdPessoa(data, idPessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Agendamento> buscarAgendamento(UUID id) {
        return agendamentoRepository.buscarProjecaoPorId(id).map(AgendamentoQueryJpaAdapter::paraDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agendamento> listarAgendamentos(LocalDate data) {
        return agendamentoRepository.listarProjecoesPorData(data).stream()
                .map(AgendamentoQueryJpaAdapter::paraDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PessoaAgendaDados buscarPessoa(UUID id) {
        var pessoa = pessoaRepository.buscarProjecaoPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Pessoa não encontrada: " + id));
        return new PessoaAgendaDados(pessoa.getPrioridade(), pessoa.isAtiva());
    }

    @Override
    @Transactional(readOnly = true)
    public DestinoAgendaDados buscarDestino(UUID id) {
        var destino = destinoRepository.buscarProjecaoPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Destino não encontrado: " + id));
        return new DestinoAgendaDados(destino.getIdCidade(), destino.getTempoViagem(), destino.isAtivo());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean listaAtiva(UUID id) {
        return listaRepository.existsByIdAndDataArquivadoIsNull(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegraCadastroAgendaDados> listarRegrasCadastro(UUID idLista) {
        var regras = listaRepository.listarRegrasCadastro(idLista);
        var prioridadesPorRegra = listaRepository.listarPrioridadesCadastro(idLista).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getId(),
                        HashMap::new,
                        Collectors.mapping(item -> item.getPrioridade().name(), Collectors.toList())));
        var tiposPorRegra = listaRepository.listarTiposCadastro(idLista).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getId(),
                        HashMap::new,
                        Collectors.mapping(item -> item.getIdTipoVeiculo(), Collectors.toList())));
        var cidadesPorRegra = listaRepository.listarCidadesCadastro(idLista).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getId(),
                        HashMap::new,
                        Collectors.mapping(item -> item.getIdCidade(), Collectors.toList())));

        return regras.stream()
                .map(regra -> new RegraCadastroAgendaDados(
                        regra.getId(),
                        prioridadesPorRegra.getOrDefault(regra.getId(), List.of()),
                        tiposPorRegra.getOrDefault(regra.getId(), List.of()),
                        cidadesPorRegra.getOrDefault(regra.getId(), List.of()),
                        regra.isObrigacaoAnexo(),
                        regra.getInicioHorario(),
                        regra.getFinalHorario()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoVeiculoAgendaDados buscarTipoVeiculo(UUID id) {
        var tipo = tipoVeiculoRepository.buscarProjecaoPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Tipo de veículo não encontrado: " + id));
        return new TipoVeiculoAgendaDados(tipo.getId(), tipo.getNome(), tipo.getCapacidade(), tipo.isAtivo());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Duration> buscarToleranciaSeparacao(UUID idLista, UUID idTipoVeiculo) {
        return listaRepository.buscarToleranciasSeparacao(idLista, idTipoVeiculo).stream()
                .findFirst()
                .map(tolerancia -> Duration.ofSeconds(tolerancia.getSegundos(), tolerancia.getNanos()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VeiculoAberto> buscarVeiculo(UUID id) {
        return veiculoRepository.buscarProjecaoPorId(id).map(AgendamentoQueryJpaAdapter::paraVeiculoDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeiculoAberto> listarVeiculos(LocalDate data) {
        return veiculoRepository.listarProjecoesPorData(data).stream()
                .map(AgendamentoQueryJpaAdapter::paraVeiculoDomain)
                .toList();
    }

    private static Agendamento paraDomain(AgendamentoProjecao projeccao) {
        return Agendamento.recriar(
                projeccao.getId(),
                projeccao.getData(),
                projeccao.getIdPessoa(),
                projeccao.getIdLista(),
                projeccao.getIdRegraCadastro(),
                projeccao.getIdTipoVeiculo(),
                projeccao.getIdDestino(),
                Duration.ofSeconds(projeccao.getTempoViagemSegundos(), projeccao.getTempoViagemNanos()),
                Duration.ofSeconds(projeccao.getToleranciaSegundos(), projeccao.getToleranciaNanos()),
                projeccao.getHorarioChegada(),
                projeccao.isBuscarNaResidencia(),
                projeccao.getAcompanhantes(),
                projeccao.getIdVeiculoAberto());
    }

    private static VeiculoAberto paraVeiculoDomain(VeiculoAgendaProjecao projeccao) {
        return new VeiculoAberto(
                projeccao.getId(),
                projeccao.getData(),
                projeccao.getIdTipoVeiculo(),
                projeccao.getNomeTipoVeiculo(),
                projeccao.getCapacidade(),
                projeccao.getIdentificacao(),
                projeccao.getHorarioSaida(),
                projeccao.isHorarioSaidaCalculado());
    }
}
