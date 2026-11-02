package br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.RegraCadastroAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandCriarHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public UUID handler(AgendamentoCommandCriar command) {
        AgendamentoValidacao.exigirDiaAberto(consultas, command.data());
        var veiculosAbertos = consultas.listarVeiculos(command.data());
        if (veiculosAbertos.isEmpty()) {
            throw new OperacaoNaoPermitidaException("Abra pelo menos um veículo antes de agendar passageiros");
        }
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
        if (veiculosAbertos.stream()
                .noneMatch(veiculo -> veiculo.idTipoVeiculo().equals(command.idTipoVeiculo()))) {
            throw new OperacaoNaoPermitidaException(
                    "Abra pelo menos um veículo físico do tipo selecionado antes de agendar");
        }
        if (lugaresNecessarios > tipo.capacidade()) {
            throw new DadosInvalidosException("A quantidade de pessoas excede a capacidade do tipo de veículo");
        }

        var regra = consultas.listarRegrasCadastro(command.idLista()).stream()
                .filter(item -> item.id().equals(command.idRegraCadastro()))
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "A regra de cadastro não pertence à lista informada"));
        RegraCadastroAgendamento.validar(
                regra, pessoa, destino, command.horarioChegada(), command.idTipoVeiculo());

        var tolerancia = consultas.buscarToleranciaSeparacao(command.idLista(), command.idTipoVeiculo())
                .orElseThrow(() -> new OperacaoNaoPermitidaException(
                        "A lista não possui regra de tolerância para o tipo de veículo selecionado"));
        var agendamento = Agendamento.criar(
                command.data(),
                command.idPessoa(),
                command.idLista(),
                command.idRegraCadastro(),
                command.idTipoVeiculo(),
                command.idDestino(),
                destino.tempoViagem(),
                tolerancia,
                command.horarioChegada(),
                command.buscarNaResidencia(),
                command.acompanhantes());
        return comandos.salvar(agendamento);
    }
}
