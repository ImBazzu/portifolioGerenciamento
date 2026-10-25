package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.jpaAdapter;

import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
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
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.CidadeJpa;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.DestinoJpa;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.CidadeRepository;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.DestinoRepository;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.TipoListaRepository;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.repositorio.PessoaRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoQueryRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgendamentoJpaAdapter implements AgendamentoPorta {

    private final AgendamentoRepository agendamentoRepository;
    private final DiaAgendamentoRepository diaRepository;
    private final VeiculoAgendaRepository veiculoRepository;
    private final PessoaRepository pessoaRepository;
    private final TipoListaRepository listaRepository;
    private final TipoVeiculoQueryRepository tipoVeiculoRepository;
    private final DestinoRepository destinoRepository;
    private final CidadeRepository cidadeRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean diaAberto(LocalDate data) {
        return diaRepository.existsById(data);
    }

    @Override
    @Transactional
    public void abrirDia(LocalDate data) {
        var dia = new DiaAgendamentoJpa();
        dia.setData(data);
        dia.setAbertoEm(java.time.LocalDateTime.now());
        diaRepository.saveAndFlush(dia);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeAgendamento(LocalDate data, UUID idPessoa) {
        return agendamentoRepository.existsByDataAndIdPessoa(data, idPessoa);
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
    @Transactional(readOnly = true)
    public Optional<Agendamento> buscarPorId(UUID id) {
        return agendamentoRepository.findById(id).map(AgendamentoJpaAdapter::paraDomain);
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        agendamentoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorData(LocalDate data) {
        return agendamentoRepository.findByDataOrderByHorarioChegadaAsc(data).stream()
                .map(AgendamentoJpaAdapter::paraDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PessoaAgendaDados buscarPessoa(UUID id) {
        var pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Pessoa não encontrada: " + id));
        return new PessoaAgendaDados(pessoa.getPrioridade(), pessoa.getDataArquivado() == null);
    }

    @Override
    @Transactional(readOnly = true)
    public DestinoAgendaDados buscarDestino(UUID id) {
        var destino = destinoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Destino não encontrado: " + id));
        var cidade = cidadeRepository.findById(destino.getCidadeId())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Cidade não encontrada para o destino: " + destino.getCidadeId()));
        return dadosDestino(destino, cidade);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean listaAtiva(UUID id) {
        return listaRepository.findById(id)
                .map(lista -> lista.getDataArquivado() == null)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegraCadastroAgendaDados> listarRegrasCadastro(UUID idLista) {
        return listaRepository.findById(idLista)
                .map(TipoListaJpa::getRegrasCadastro)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Lista não encontrada: " + idLista))
                .stream()
                .map(regra -> new RegraCadastroAgendaDados(
                        regra.getId(),
                        regra.getPrioridades().stream().map(Enum::name).toList(),
                        List.copyOf(regra.getIdsVeiculos()),
                        List.copyOf(regra.getIdsCidades()),
                        regra.isObrigacaoAnexo(),
                        regra.getInicioHorario(),
                        regra.getFinalHorario()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoVeiculoAgendaDados buscarTipoVeiculo(UUID id) {
        var tipo = tipoVeiculoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Tipo de veículo não encontrado: " + id));
        return dadosTipo(tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Duration> buscarToleranciaSeparacao(UUID idLista, UUID idTipoVeiculo) {
        var lista = listaRepository.findById(idLista);
        return lista.flatMap(tipoLista -> tipoLista.getRegrasSeparacao().stream()
                        .filter(regra -> regra.getIdTipoVeiculo().equals(idTipoVeiculo))
                        .findFirst())
                .map(regra -> Duration.ofSeconds(regra.getTempoToleranciaSegundos(),
                        regra.getTempoToleranciaNanos()));
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

    @Override
    @Transactional(readOnly = true)
    public Optional<VeiculoAberto> buscarVeiculo(UUID id) {
        return veiculoRepository.findById(id).map(this::paraVeiculoDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeiculoAberto> listarVeiculos(LocalDate data) {
        return veiculoRepository.findByDataOrderById(data).stream()
                .map(this::paraVeiculoDomain)
                .toList();
    }

    private VeiculoAberto paraVeiculoDomain(VeiculoAgendaJpa entidade) {
        return new VeiculoAberto(entidade.getId(), entidade.getData(), entidade.getIdTipoVeiculo(),
                entidade.getNomeTipoVeiculo(), entidade.getCapacidade(), entidade.getIdentificacao(),
                entidade.getHorarioSaida(), entidade.isHorarioSaidaCalculado());
    }

    private static Agendamento paraDomain(AgendamentoJpa entidade) {
        return Agendamento.recriar(
                entidade.getId(), entidade.getData(), entidade.getIdPessoa(), entidade.getIdLista(),
                entidade.getIdRegraCadastro(), entidade.getIdTipoVeiculo(), entidade.getIdDestino(),
                entidade.getTempoViagem(), entidade.getTolerancia(), entidade.getHorarioChegada(),
                entidade.isBuscarNaResidencia(), entidade.getAcompanhantes(),
                entidade.getIdVeiculoAberto());
    }

    private static DestinoAgendaDados dadosDestino(DestinoJpa destino, CidadeJpa cidade) {
        return new DestinoAgendaDados(
                cidade.getId(),
                cidade.getTempoViagem(),
                destino.getDataArquivado() == null && cidade.getDataArquivado() == null);
    }

    private static TipoVeiculoAgendaDados dadosTipo(TipoVeiculoJpa tipo) {
        return new TipoVeiculoAgendaDados(
                tipo.getId(), tipo.getNome(), tipo.getCapacidade(), tipo.getDataArquivado() == null);
    }
}
