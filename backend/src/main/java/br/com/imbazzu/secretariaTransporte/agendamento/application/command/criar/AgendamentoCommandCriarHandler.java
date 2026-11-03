package br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.RegraCadastroAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandCriarHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public UUID handler(AgendamentoCommandCriar command) {
        AgendamentoValidacao.exigirDiaAberto(consultas, command.data());
        var veiculosAbertos = consultas.listarVeiculos(command.data());
        if (command.acompanhantes() < 0) {
            throw new DadosInvalidosException("A quantidade de acompanhantes não pode ser negativa");
        }
        long lugaresNecessarios = (long) command.acompanhantes() + 1;
        if (consultas.existeAgendamento(command.data(), command.idPessoa())) {
            throw new EntidadeDuplicadaException("A pessoa já possui agendamento nesta data");
        }

        var pessoa = AgendamentoValidacao.validarPessoa(consultas, command.idPessoa());
        var destino = AgendamentoValidacao.validarDestino(consultas, command.idDestino());
        AgendamentoValidacao.validarLista(consultas, command.idLista());
        var tipo = consultas.buscarTipoVeiculo(command.idTipoVeiculo());
        if (!tipo.ativo()) {
            throw new OperacaoNaoPermitidaException("Não é possível agendar para um tipo de veículo arquivado");
        }
        boolean tipoCarro = VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome());
        if (!tipoCarro && veiculosAbertos.stream()
                .noneMatch(veiculo -> veiculo.idTipoVeiculo().equals(command.idTipoVeiculo()))) {
            throw new OperacaoNaoPermitidaException(
                    "Abra pelo menos um veículo físico do tipo selecionado antes de agendar");
        }
        if (lugaresNecessarios > tipo.capacidade()) {
            throw new DadosInvalidosException("A quantidade de pessoas excede a capacidade do tipo de veículo");
        }

        var regrasCadastro = consultas.listarRegrasCadastro(command.idLista());
        var regra = command.idRegraCadastro() == null
                ? regrasCadastro.stream()
                        .filter(item -> !item.obrigacaoAnexo())
                        .filter(item -> item.idsVeiculos().contains(command.idTipoVeiculo()))
                        .filter(item -> RegraCadastroAgendamento.motivosNaoAplicacao(
                                item, pessoa, destino, command.horarioChegada()).isEmpty())
                        .filter(item -> !tipoCarro || consultas.buscarToleranciaSeparacao(
                                command.idLista(), command.idTipoVeiculo()).isPresent())
                        .findFirst()
                        .orElseThrow(() -> new OperacaoNaoPermitidaException(
                                "Nenhuma regra da lista aprova o tipo e os dados informados"))
                : regrasCadastro.stream()
                        .filter(item -> item.id().equals(command.idRegraCadastro()))
                        .findFirst()
                        .orElseThrow(() -> new EntidadeNaoEncontradoException(
                                "A regra de cadastro não pertence à lista informada"));
        RegraCadastroAgendamento.validar(
                regra, pessoa, destino, command.horarioChegada(), command.idTipoVeiculo());

        var tolerancia = tipoCarro
                ? consultas.buscarToleranciaSeparacao(command.idLista(), command.idTipoVeiculo())
                        .orElseThrow(() -> new OperacaoNaoPermitidaException(
                                "A lista não possui regra de tolerância para o tipo de veículo selecionado"))
                : java.time.Duration.ZERO;
        var agendamento = Agendamento.criar(
                command.data(),
                command.idPessoa(),
                command.idLista(),
                regra.id(),
                command.idTipoVeiculo(),
                command.idDestino(),
                destino.tempoViagem(),
                tolerancia,
                command.horarioChegada(),
                command.buscarNaResidencia(),
                command.acompanhantes());
        if (tipoCarro && command.idVeiculo() != null) {
            throw new OperacaoNaoPermitidaException(
                    "Passageiros de carro devem ser atribuídos pela página de carros");
        }
        if (!tipoCarro) {
            if (command.idVeiculo() == null) {
                throw new OperacaoNaoPermitidaException(
                        "Selecione o veículo físico para ônibus, van ou outro tipo não carro");
            }
            var veiculo = veiculosAbertos.stream()
                    .filter(item -> item.id().equals(command.idVeiculo()))
                    .findFirst()
                    .orElseThrow(() -> new EntidadeNaoEncontradoException(
                            "Veículo físico não encontrado para esta data"));
            if (!veiculo.idTipoVeiculo().equals(command.idTipoVeiculo())) {
                throw new DadosInvalidosException(
                        "O veículo físico selecionado não corresponde ao tipo aprovado");
            }
            var agendamentosDoDia = consultas.listarAgendamentos(command.data());
            var ocupantes = agendamentosDoDia.stream()
                    .filter(item -> command.idVeiculo().equals(item.getIdVeiculoAberto()))
                    .toList();
            var slots = new HashMap<UUID, CompatibilidadeVeiculoAgendamento.Slot>();
            agendamentosDoDia.forEach(item -> slots.put(
                    item.getId(), CompatibilidadeVeiculoAgendamento.criarSlot(item)));
            slots.put(agendamento.getId(), CompatibilidadeVeiculoAgendamento.criarSlot(agendamento));
            if (!CompatibilidadeVeiculoAgendamento.compativel(
                    veiculo, ocupantes, CompatibilidadeVeiculoAgendamento.criarSlot(agendamento), slots)) {
                throw new OperacaoNaoPermitidaException(
                        "O veículo selecionado não atende à capacidade ou aos horários deste passageiro");
            }
            agendamento.atribuirVeiculo(veiculo.id(), veiculo.idTipoVeiculo(), tolerancia);
        }
        return comandos.salvar(agendamento);
    }
}
