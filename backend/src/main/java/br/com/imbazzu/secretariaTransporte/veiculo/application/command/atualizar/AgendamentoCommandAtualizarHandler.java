package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizar;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.RegraCadastroAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandAtualizarHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandAtualizar command) {
        var atual = AgendamentoValidacao.buscarAgendamento(consultas, command.id());
        AgendamentoValidacao.exigirDiaAberto(consultas, atual.getData());
        if (command.acompanhantes() < 0) {
            throw new DadosInvalidosException("A quantidade de acompanhantes não pode ser negativa");
        }

        var pessoa = AgendamentoValidacao.validarPessoa(consultas, atual.getIdPessoa());
        var destino = AgendamentoValidacao.validarDestino(consultas, command.idDestino());
        AgendamentoValidacao.validarLista(consultas, atual.getIdLista());
        var tipo = consultas.buscarTipoVeiculo(command.idTipoVeiculo());
        if (!tipo.ativo()) {
            throw new OperacaoNaoPermitidaException("Não é possível agendar para um tipo de veículo arquivado");
        }
        if ((long) command.acompanhantes() + 1 > tipo.capacidade()) {
            throw new DadosInvalidosException("A quantidade de pessoas excede a capacidade do tipo de veículo");
        }

        var regra = consultas.listarRegrasCadastro(atual.getIdLista()).stream()
                .filter(item -> !item.obrigacaoAnexo())
                .filter(item -> item.idsVeiculos().contains(command.idTipoVeiculo()))
                .filter(item -> RegraCadastroAgendamento.motivosNaoAplicacao(
                        item, pessoa, destino, command.horarioChegada()).isEmpty())
                .findFirst()
                .orElseThrow(() -> new OperacaoNaoPermitidaException(
                        "Nenhuma regra da lista aprova o tipo e os dados informados"));

        boolean tipoCarro = VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome());
        var tolerancia = tipoCarro
                ? consultas.buscarToleranciaSeparacao(atual.getIdLista(), command.idTipoVeiculo())
                        .orElseThrow(() -> new OperacaoNaoPermitidaException(
                                "A lista não possui regra de tolerância para o tipo de veículo selecionado"))
                : java.time.Duration.ZERO;
        var atualizado = Agendamento.recriar(
                atual.getId(),
                atual.getData(),
                atual.getIdPessoa(),
                atual.getIdLista(),
                regra.id(),
                tipo.id(),
                command.idDestino(),
                destino.tempoViagem(),
                tolerancia,
                command.horarioChegada(),
                command.buscarNaResidencia(),
                command.acompanhantes(),
                null);

        var veiculos = consultas.listarVeiculos(atual.getData());
        if (tipoCarro) {
            if (command.idVeiculo() != null) {
                var veiculo = buscarVeiculo(command.idVeiculo(), veiculos);
                if (!VeiculoAberto.tipoCalculaHorarioSaida(veiculo.nomeTipoVeiculo())
                        || !veiculo.idTipoVeiculo().equals(tipo.id())) {
                    throw new DadosInvalidosException("Selecione um carro compatível com o tipo escolhido");
                }
                validarCompatibilidade(atual, atualizado, veiculo, tolerancia);
                atualizado.atribuirVeiculo(veiculo.id(), veiculo.idTipoVeiculo(), tolerancia);
            }
        } else {
            if (command.idVeiculo() == null) {
                throw new OperacaoNaoPermitidaException(
                        "Selecione o veículo físico para ônibus, van ou outro tipo não carro");
            }
            var veiculo = buscarVeiculo(command.idVeiculo(), veiculos);
            if (VeiculoAberto.tipoCalculaHorarioSaida(veiculo.nomeTipoVeiculo())
                    || !veiculo.idTipoVeiculo().equals(tipo.id())) {
                throw new DadosInvalidosException("O veículo físico selecionado não corresponde ao tipo aprovado");
            }
            validarCompatibilidade(atual, atualizado, veiculo, tolerancia);
            atualizado.atribuirVeiculo(veiculo.id(), veiculo.idTipoVeiculo(), tolerancia);
        }
        comandos.salvar(atualizado);
    }

    private static VeiculoAberto buscarVeiculo(UUID idVeiculo, java.util.List<VeiculoAberto> veiculos) {
        return veiculos.stream()
                .filter(veiculo -> veiculo.id().equals(idVeiculo))
                .findFirst()
                .orElseThrow(() -> new DadosInvalidosException(
                        "O veículo físico selecionado não está aberto para esta data"));
    }

    private void validarCompatibilidade(
            Agendamento atual, Agendamento atualizado, VeiculoAberto veiculo,
            java.time.Duration tolerancia) {
        var agendamentosDoDia = consultas.listarAgendamentos(atual.getData());
        var ocupantes = agendamentosDoDia.stream()
                .filter(item -> veiculo.id().equals(item.getIdVeiculoAberto()))
                .filter(item -> !item.getId().equals(atual.getId()))
                .toList();
        var slots = new HashMap<UUID, CompatibilidadeVeiculoAgendamento.Slot>();
        agendamentosDoDia.stream()
                .filter(item -> !item.getId().equals(atual.getId()))
                .forEach(item -> slots.put(
                        item.getId(), CompatibilidadeVeiculoAgendamento.criarSlot(item)));
        var novoSlot = CompatibilidadeVeiculoAgendamento.criarSlot(atualizado, tolerancia);
        slots.put(atualizado.getId(), novoSlot);
        if (!CompatibilidadeVeiculoAgendamento.compativel(
                veiculo, ocupantes, novoSlot, slots)) {
            throw new OperacaoNaoPermitidaException(
                    "O veículo selecionado não atende à capacidade ou aos horários deste passageiro");
        }
    }
}
