package br.com.imbazzu.secretariaTransporte.agendamento.application;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.application.model.ResultadoSeparacaoAutomatica;
import br.com.imbazzu.secretariaTransporte.agendamento.application.model.VeiculoPermitido;
import br.com.imbazzu.secretariaTransporte.agendamento.application.model.VerificacaoVeiculos;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoApplication {

    private final AgendamentoPorta porta;

    @Transactional
    public void abrirDia(LocalDate data) {
        if (porta.diaAberto(data)) {
            throw new EntidadeDuplicadaException("O dia já foi aberto: " + data);
        }
        porta.abrirDia(data);
    }

    @Transactional
    public VeiculoAberto abrirVeiculo(LocalDate data, UUID idTipoVeiculo,
            String identificacao, LocalTime horarioSaida) {
        exigirDiaAberto(data);
        var tipo = porta.buscarTipoVeiculo(idTipoVeiculo);
        if (!tipo.ativo()) {
            throw new OperacaoNaoPermitidaException("Não é possível abrir um tipo de veículo arquivado");
        }
        boolean calculaSaida = VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome());
        if (calculaSaida && horarioSaida != null) {
            throw new DadosInvalidosException("O horário de saída de veículos do tipo Carro é calculado");
        }
        if (!calculaSaida && horarioSaida == null) {
            throw new DadosInvalidosException("Informe o horário de saída para este tipo de veículo");
        }
        var veiculo = new VeiculoAberto(UUID.randomUUID(), data, idTipoVeiculo, tipo.nome(),
                tipo.capacidade(), identificacao == null || identificacao.isBlank()
                        ? null : identificacao.trim(),
                horarioSaida, calculaSaida);
        return porta.salvar(veiculo);
    }

    @Transactional(readOnly = true)
    public VerificacaoVeiculos verificarVeiculos(LocalDate data, UUID idPessoa, UUID idLista,
            UUID idDestino, LocalTime horarioChegada, int acompanhantes) {
        exigirDiaAberto(data);
        var veiculosAbertos = porta.listarVeiculos(data);
        if (veiculosAbertos.isEmpty()) {
            throw new OperacaoNaoPermitidaException("Abra pelo menos um veículo antes de agendar passageiros");
        }
        if (acompanhantes < 0) {
            throw new DadosInvalidosException("A quantidade de acompanhantes não pode ser negativa");
        }
        long lugaresNecessarios = (long) acompanhantes + 1;
        var pessoa = validarPessoa(idPessoa);
        var destino = validarDestino(idDestino);
        validarLista(idLista);
        return verificarVeiculos(idLista, pessoa, destino, porta.listarRegrasCadastro(idLista),
                horarioChegada, lugaresNecessarios, veiculosAbertos);
    }

    @Transactional
    public UUID criar(LocalDate data, UUID idPessoa, UUID idLista, UUID idRegraCadastro,
            UUID idTipoVeiculo, UUID idDestino, LocalTime horarioChegada,
            boolean buscarNaResidencia, int acompanhantes) {
        exigirDiaAberto(data);
        var veiculosAbertos = porta.listarVeiculos(data);
        if (veiculosAbertos.isEmpty()) {
            throw new OperacaoNaoPermitidaException("Abra pelo menos um veículo antes de agendar passageiros");
        }
        if (acompanhantes < 0) {
            throw new DadosInvalidosException("A quantidade de acompanhantes não pode ser negativa");
        }
        long lugaresNecessarios = (long) acompanhantes + 1;
        if (porta.existeAgendamento(data, idPessoa)) {
            throw new EntidadeDuplicadaException("A pessoa já possui agendamento nesta data");
        }

        var pessoa = validarPessoa(idPessoa);
        var destino = validarDestino(idDestino);
        validarLista(idLista);
        var tipo = porta.buscarTipoVeiculo(idTipoVeiculo);
        if (!tipo.ativo()) {
            throw new OperacaoNaoPermitidaException("Não é possível agendar para um tipo de veículo arquivado");
        }
        if (veiculosAbertos.stream().noneMatch(veiculo -> veiculo.idTipoVeiculo().equals(idTipoVeiculo))) {
            throw new OperacaoNaoPermitidaException(
                    "Abra pelo menos um veículo físico do tipo selecionado antes de agendar");
        }
        if (lugaresNecessarios > tipo.capacidade()) {
            throw new DadosInvalidosException("A quantidade de pessoas excede a capacidade do tipo de veículo");
        }

        var regras = porta.listarRegrasCadastro(idLista);
        var regra = regras.stream()
                .filter(item -> item.id().equals(idRegraCadastro))
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "A regra de cadastro não pertence à lista informada"));
        validarRegra(regra, pessoa, destino, horarioChegada, idTipoVeiculo);

        var tolerancia = porta.buscarToleranciaSeparacao(idLista, idTipoVeiculo)
                .orElseThrow(() -> new OperacaoNaoPermitidaException(
                        "A lista não possui regra de tolerância para o tipo de veículo selecionado"));
        var agendamento = Agendamento.criar(data, idPessoa, idLista, idRegraCadastro,
                idTipoVeiculo, idDestino, destino.tempoViagem(), tolerancia,
                horarioChegada, buscarNaResidencia, acompanhantes);
        return porta.salvar(agendamento);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarPorData(LocalDate data) {
        exigirDiaAberto(data);
        return porta.listarPorData(data);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarPendentes(LocalDate data) {
        exigirDiaAberto(data);
        return porta.listarPorData(data).stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() == null)
                .sorted(Comparator.comparing(Agendamento::getHorarioChegada))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VeiculoAberto> listarVeiculos(LocalDate data) {
        exigirDiaAberto(data);
        return porta.listarVeiculos(data);
    }

    @Transactional(readOnly = true)
    public Map<UUID, List<UUID>> listarVeiculosCompativeis(LocalDate data) {
        exigirDiaAberto(data);
        var agendamentos = porta.listarPorData(data);
        var slotsConhecidos = new HashMap<UUID, Slot>();
        var ocupantesPorVeiculo = new HashMap<UUID, List<Agendamento>>();
        for (var agendamento : agendamentos) {
            slotsConhecidos.put(agendamento.getId(), criarSlot(agendamento));
            if (agendamento.getIdVeiculoAberto() != null) {
                ocupantesPorVeiculo.computeIfAbsent(agendamento.getIdVeiculoAberto(), ignored -> new ArrayList<>())
                        .add(agendamento);
            }
        }

        var veiculos = porta.listarVeiculos(data);
        var resultado = new HashMap<UUID, List<UUID>>();
        for (var agendamento : agendamentos) {
            if (agendamento.getIdVeiculoAberto() != null) {
                continue;
            }
            var slot = slotsConhecidos.get(agendamento.getId());
            var idsCompativeis = veiculos.stream()
                    .filter(veiculo -> veiculo.idTipoVeiculo().equals(agendamento.getIdTipoVeiculo()))
                    .filter(veiculo -> veiculoCompativel(veiculo,
                            ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()), slot, slotsConhecidos))
                    .map(VeiculoAberto::id)
                    .toList();
            resultado.put(agendamento.getId(), idsCompativeis);
        }
        return resultado;
    }

    @Transactional
    public void atribuirManualmente(UUID idAgendamento, UUID idVeiculo) {
        var agendamento = buscarAgendamento(idAgendamento);
        var veiculo = porta.buscarVeiculo(idVeiculo)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Veículo aberto não encontrado: " + idVeiculo));
        if (!agendamento.getData().equals(veiculo.data())) {
            throw new DadosInvalidosException("O agendamento e o veículo devem pertencer ao mesmo dia");
        }
        if (!agendamento.getIdTipoVeiculo().equals(veiculo.idTipoVeiculo())) {
            throw new DadosInvalidosException("O tipo do veículo não corresponde ao tipo necessário no agendamento");
        }
        if (agendamento.getIdVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException("O agendamento já está atribuído a um veículo físico");
        }

        var ocupantes = agendamentosDoVeiculo(agendamento.getData(), idVeiculo);
        var slotsConhecidos = new HashMap<UUID, Slot>();
        slotsConhecidos.put(agendamento.getId(), criarSlot(agendamento));
        ocupantes.forEach(ocupante -> slotsConhecidos.put(ocupante.getId(), criarSlot(ocupante)));
        if (!veiculoCompativel(veiculo, ocupantes, slotsConhecidos.get(agendamento.getId()), slotsConhecidos)) {
            throw new OperacaoNaoPermitidaException(
                    "O veículo não atende à capacidade ou aos horários e tolerâncias deste passageiro");
        }
        agendamento.atribuirVeiculo(idVeiculo);
        porta.salvar(agendamento);
    }

    @Transactional
    public ResultadoSeparacaoAutomatica separarAutomaticamente(LocalDate data) {
        exigirDiaAberto(data);
        var todos = porta.listarPorData(data);
        var motivosPendencia = new HashMap<UUID, String>();
        var pendentes = new ArrayList<Slot>();
        var slotsConhecidos = new HashMap<UUID, Slot>();
        for (var agendamento : todos) {
            var slot = criarSlot(agendamento);
            slotsConhecidos.put(agendamento.getId(), slot);
            if (agendamento.getIdVeiculoAberto() == null) {
                pendentes.add(slot);
            }
        }
        pendentes.sort(Comparator.comparing(slot -> slot.agendamento().getHorarioChegada()));
        var veiculos = porta.listarVeiculos(data);
        var ocupantesPorVeiculo = new HashMap<UUID, List<Agendamento>>();
        for (var agendamento : todos) {
            if (agendamento.getIdVeiculoAberto() != null) {
                ocupantesPorVeiculo.computeIfAbsent(agendamento.getIdVeiculoAberto(), ignored -> new ArrayList<>())
                        .add(agendamento);
            }
        }

        int alocados = 0;
        for (var slot : pendentes) {
            var candidatos = veiculos.stream()
                    .filter(veiculo -> veiculo.idTipoVeiculo().equals(slot.agendamento().getIdTipoVeiculo()))
                    .filter(veiculo -> veiculoCompativel(veiculo,
                            ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()), slot, slotsConhecidos))
                    .sorted(Comparator.comparingInt(veiculo -> capacidadeRestante(
                            veiculo, ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                            slot.agendamento())))
                    .toList();
            if (candidatos.isEmpty()) {
                var veiculosDoTipo = veiculos.stream()
                        .filter(veiculo -> veiculo.idTipoVeiculo().equals(slot.agendamento().getIdTipoVeiculo()))
                        .toList();
                var motivo = veiculosDoTipo.isEmpty()
                        ? "Nenhum veículo físico deste tipo foi aberto para o dia"
                        : veiculosDoTipo.stream().noneMatch(veiculo -> capacidadeDisponivel(
                                veiculo, ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                                slot.agendamento()))
                                ? "Não há assentos disponíveis nos veículos deste tipo"
                                : "Horário de saída incompatível com a tolerância das regras";
                motivosPendencia.put(slot.agendamento().getId(), motivo);
                continue;
            }
            var escolhido = candidatos.getFirst();
            slot.agendamento().atribuirVeiculo(escolhido.id());
            porta.salvar(slot.agendamento());
            ocupantesPorVeiculo.computeIfAbsent(escolhido.id(), ignored -> new ArrayList<>())
                    .add(slot.agendamento());
            alocados++;
        }

        var aindaPendentes = porta.listarPorData(data).stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() == null)
                .sorted(Comparator.comparing(Agendamento::getHorarioChegada))
                .map(agendamento -> new ResultadoSeparacaoAutomatica.PassageiroPendente(
                        agendamento.getId(),
                        motivosPendencia.getOrDefault(agendamento.getId(),
                                "Nenhum veículo compatível com a capacidade e o horário foi encontrado")))
                .toList();
        return new ResultadoSeparacaoAutomatica(alocados, aindaPendentes);
    }

    @Transactional(readOnly = true)
    public Agendamento buscar(UUID id) {
        return buscarAgendamento(id);
    }

    @Transactional
    public void remover(UUID id) {
        var agendamento = buscarAgendamento(id);
        if (agendamento.getIdVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException(
                    "Remova o passageiro do veículo antes de excluir o agendamento");
        }
        porta.deletar(id);
    }

    @Transactional
    public void removerDoVeiculo(UUID idAgendamento) {
        var agendamento = buscarAgendamento(idAgendamento);
        if (agendamento.getIdVeiculoAberto() == null) {
            throw new OperacaoNaoPermitidaException("O agendamento não está atribuído a um veículo");
        }
        agendamento.removerVeiculo();
        porta.salvar(agendamento);
    }

    @Transactional(readOnly = true)
    public LocalTime horarioSaidaNecessario(Agendamento agendamento) {
        return agendamento.getHorarioChegada().minus(agendamento.getTempoViagem());
    }

    @Transactional(readOnly = true)
    public LocalTime horarioSaidaDoVeiculo(VeiculoAberto veiculo) {
        if (!veiculo.calculaHorarioSaida()) {
            return veiculo.horarioSaida();
        }
        return porta.listarPorData(veiculo.data()).stream()
                .filter(agendamento -> veiculo.id().equals(agendamento.getIdVeiculoAberto()))
                .map(this::horarioSaidaNecessario)
                .min(LocalTime::compareTo)
                .orElse(null);
    }

    private VerificacaoVeiculos verificarVeiculos(UUID idLista, PessoaAgendaDados pessoa,
            DestinoAgendaDados destino, List<RegraCadastroAgendaDados> regras, LocalTime horarioChegada,
            long lugaresNecessarios, List<VeiculoAberto> veiculosAbertos) {
        var idsPorTipo = new HashMap<UUID, List<UUID>>();
        var motivos = new LinkedHashSet<String>();
        for (var regra : regras) {
            if (!regraAplica(regra, pessoa, destino, horarioChegada)) {
                continue;
            }
            if (regra.obrigacaoAnexo()) {
                motivos.add("A regra %s exige anexo; agendamento com anexos ainda não está disponível"
                        .formatted(regra.id()));
                continue;
            }
            for (var idTipo : regra.idsVeiculos()) {
                var tipo = porta.buscarTipoVeiculo(idTipo);
                if (!tipo.ativo()) {
                    motivos.add("O tipo de veículo %s está arquivado".formatted(tipo.nome()));
                    continue;
                }
                if (tipo.capacidade() < lugaresNecessarios) {
                    motivos.add("O tipo de veículo %s não possui lugares suficientes".formatted(tipo.nome()));
                    continue;
                }
                if (veiculosAbertos.stream()
                        .noneMatch(veiculo -> veiculo.idTipoVeiculo().equals(idTipo))) {
                    motivos.add("Nenhum veículo físico do tipo %s foi aberto para o dia".formatted(tipo.nome()));
                    continue;
                }
                if (porta.buscarToleranciaSeparacao(idLista, idTipo).isEmpty()) {
                    motivos.add("A lista não possui regra de tolerância para o tipo de veículo %s"
                            .formatted(tipo.nome()));
                    continue;
                }
                idsPorTipo.computeIfAbsent(idTipo, ignored -> new ArrayList<>()).add(regra.id());
            }
        }
        var permitidos = idsPorTipo.entrySet().stream()
                .map(entry -> {
                    var tipo = porta.buscarTipoVeiculo(entry.getKey());
                    return new VeiculoPermitido(tipo.id(), tipo.nome(), tipo.capacidade(),
                            entry.getValue().stream().distinct().toList());
                })
                .sorted(Comparator.comparing(VeiculoPermitido::nome))
                .toList();
        if (permitidos.isEmpty() && motivos.isEmpty()) {
            motivos.add("Nenhuma regra da lista aprova o horário, prioridade, cidade e tipo de veículo");
        }
        return new VerificacaoVeiculos(permitidos, List.copyOf(motivos));
    }

    private void validarRegra(RegraCadastroAgendaDados regra, PessoaAgendaDados pessoa,
            DestinoAgendaDados destino, LocalTime horarioChegada, UUID idTipoVeiculo) {
        if (regra.obrigacaoAnexo()) {
            throw new OperacaoNaoPermitidaException(
                    "A regra exige anexo, mas o agendamento ainda não aceita anexos");
        }
        if (!regra.idsVeiculos().contains(idTipoVeiculo)
                || !regraAplica(regra, pessoa, destino, horarioChegada)) {
            throw new OperacaoNaoPermitidaException(
                    "A regra selecionada não aprova este passageiro, horário, cidade ou tipo de veículo");
        }
    }

    private static boolean regraAplica(RegraCadastroAgendaDados regra, PessoaAgendaDados pessoa,
            DestinoAgendaDados destino, LocalTime horario) {
        if (!regra.prioridades().contains(pessoa.prioridade().name())) {
            return false;
        }
        if (!regra.idsCidades().isEmpty() && !regra.idsCidades().contains(destino.idCidade())) {
            return false;
        }
        if (regra.inicioHorario() == null && regra.finalHorario() == null) {
            return true;
        }
        if (regra.inicioHorario() == null) {
            return !horario.isAfter(regra.finalHorario());
        }
        if (regra.finalHorario() == null) {
            return !horario.isBefore(regra.inicioHorario());
        }
        if (regra.inicioHorario().compareTo(regra.finalHorario()) <= 0) {
            return !horario.isBefore(regra.inicioHorario()) && !horario.isAfter(regra.finalHorario());
        }
        return !horario.isBefore(regra.inicioHorario()) || !horario.isAfter(regra.finalHorario());
    }

    private static boolean capacidadeDisponivel(VeiculoAberto veiculo, List<Agendamento> ocupantes,
            Agendamento novo) {
        long ocupados = ocupantes.stream().mapToLong(Agendamento::quantidadePessoas).sum();
        return ocupados + novo.quantidadePessoas() <= veiculo.capacidade();
    }

    private static boolean veiculoCompativel(VeiculoAberto veiculo, List<Agendamento> ocupantes,
            Slot novo, Map<UUID, Slot> slotsConhecidos) {
        return capacidadeDisponivel(veiculo, ocupantes, novo.agendamento())
                && compativelComVeiculo(veiculo, ocupantes, novo, slotsConhecidos);
    }

    private static int capacidadeRestante(VeiculoAberto veiculo, List<Agendamento> ocupantes,
            Agendamento novo) {
        long ocupados = ocupantes.stream().mapToLong(Agendamento::quantidadePessoas).sum();
        return (int) (veiculo.capacidade() - ocupados - novo.quantidadePessoas());
    }

    private static boolean compativelComVeiculo(VeiculoAberto veiculo, List<Agendamento> ocupantes,
            Slot novo, Map<UUID, Slot> slotsConhecidos) {
        var novosDados = novo.agendamento();
        var novaSaidaNecessaria = novo.horarioSaidaNecessario();
        if (!veiculo.calculaHorarioSaida()) {
            if (veiculo.horarioSaida().isAfter(novaSaidaNecessaria)
                    || Duration.between(veiculo.horarioSaida(), novaSaidaNecessaria)
                            .compareTo(novosDados.getToleranciaSeparacao()) > 0) {
                return false;
            }
        }
        for (var ocupante : ocupantes) {
            var slotOcupante = slotsConhecidos.get(ocupante.getId());
            if (slotOcupante == null) {
                return false;
            }
            var saidaOcupante = slotOcupante.horarioSaidaNecessario();
            var diferenca = Duration.between(saidaOcupante, novaSaidaNecessaria).abs();
            var toleranciaOcupante = slotOcupante.agendamento().getToleranciaSeparacao();
            var toleranciaMaisRestritiva = toleranciaOcupante
                    .compareTo(novosDados.getToleranciaSeparacao()) <= 0
                            ? toleranciaOcupante
                            : novosDados.getToleranciaSeparacao();
            if (diferenca.compareTo(toleranciaMaisRestritiva) > 0) {
                return false;
            }
        }
        return true;
    }

    private List<Agendamento> agendamentosDoVeiculo(LocalDate data, UUID idVeiculo) {
        return porta.listarPorData(data).stream()
                .filter(agendamento -> idVeiculo.equals(agendamento.getIdVeiculoAberto()))
                .toList();
    }

    private static Slot criarSlot(Agendamento agendamento) {
        return new Slot(agendamento, agendamento.getHorarioChegada().minus(agendamento.getTempoViagem()));
    }

    private PessoaAgendaDados validarPessoa(UUID idPessoa) {
        var pessoa = porta.buscarPessoa(idPessoa);
        if (!pessoa.ativa()) {
            throw new OperacaoNaoPermitidaException("Não é possível agendar uma pessoa arquivada");
        }
        return pessoa;
    }

    private DestinoAgendaDados validarDestino(UUID idDestino) {
        var destino = porta.buscarDestino(idDestino);
        if (!destino.ativo()) {
            throw new OperacaoNaoPermitidaException("Não é possível agendar para um destino ou cidade arquivados");
        }
        return destino;
    }

    private void validarLista(UUID idLista) {
        if (!porta.listaAtiva(idLista)) {
            throw new OperacaoNaoPermitidaException("A lista não existe ou está arquivada");
        }
    }

    private void exigirDiaAberto(LocalDate data) {
        if (!porta.diaAberto(data)) {
            throw new EntidadeNaoEncontradoException("A agenda do dia não foi aberta: " + data);
        }
    }

    private Agendamento buscarAgendamento(UUID id) {
        return porta.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Agendamento não encontrado: " + id));
    }

    private record Slot(
            Agendamento agendamento,
            LocalTime horarioSaidaNecessario) {
    }
}
