package br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.VeiculoPermitido;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.VerificacaoVeiculos;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.RegraCadastroAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryVerificarVeiculosHandler {

    private final AgendamentoQueryPorta consultas;

    @Transactional(readOnly = true)
    public VerificacaoVeiculos handler(AgendamentoQueryVerificarVeiculos query) {
        AgendamentoValidacao.exigirDiaAberto(consultas, query.data());
        var veiculosAbertos = consultas.listarVeiculos(query.data());
        if (query.acompanhantes() < 0) {
            throw new DadosInvalidosException("A quantidade de acompanhantes não pode ser negativa");
        }
        long lugaresNecessarios = (long) query.acompanhantes() + 1;
        var pessoa = AgendamentoValidacao.validarPessoa(consultas, query.idPessoa());
        var destino = AgendamentoValidacao.validarDestino(consultas, query.idDestino());
        AgendamentoValidacao.validarLista(consultas, query.idLista());
        var idsPorTipo = new HashMap<java.util.UUID, List<java.util.UUID>>();
        var motivos = new LinkedHashSet<String>();
        var regras = consultas.listarRegrasCadastro(query.idLista());
        if (regras.isEmpty()) {
            motivos.add("A lista selecionada não possui regras de cadastro configuradas");
        }
        for (var regra : regras) {
            var motivosRegra = RegraCadastroAgendamento.motivosNaoAplicacao(
                    regra, pessoa, destino, query.horarioChegada());
            if (!motivosRegra.isEmpty()) {
                motivos.add("Regra %s: %s".formatted(regra.id(), String.join("; ", motivosRegra)));
                continue;
            }
            if (regra.obrigacaoAnexo()) {
                motivos.add("A regra %s exige anexo; agendamento com anexos ainda não está disponível"
                        .formatted(regra.id()));
                continue;
            }
            if (regra.idsVeiculos().isEmpty()) {
                motivos.add("A regra %s não possui tipos de veículo vinculados".formatted(regra.id()));
                continue;
            }
            for (var idTipo : regra.idsVeiculos()) {
                var tipo = consultas.buscarTipoVeiculo(idTipo);
                if (!tipo.ativo()) {
                    motivos.add("O tipo de veículo %s está arquivado".formatted(tipo.nome()));
                    continue;
                }
                if (tipo.capacidade() < lugaresNecessarios) {
                    motivos.add("O tipo de veículo %s não possui lugares suficientes".formatted(tipo.nome()));
                    continue;
                }
                if (!VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome())
                        && veiculosAbertos.stream().noneMatch(veiculo -> veiculo.idTipoVeiculo().equals(idTipo))) {
                    motivos.add("Nenhum veículo físico do tipo %s foi aberto para o dia".formatted(tipo.nome()));
                    continue;
                }
                if (VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome())
                        && consultas.buscarToleranciaSeparacao(query.idLista(), idTipo).isEmpty()) {
                    motivos.add("A lista não possui regra de tolerância para o tipo de veículo %s"
                            .formatted(tipo.nome()));
                    continue;
                }
                idsPorTipo.computeIfAbsent(idTipo, ignored -> new ArrayList<>()).add(regra.id());
            }
        }
        var permitidos = idsPorTipo.entrySet().stream()
                .map(entry -> {
                    var tipo = consultas.buscarTipoVeiculo(entry.getKey());
                    return new VeiculoPermitido(tipo.id(), tipo.nome(), tipo.capacidade(),
                            entry.getValue().stream().distinct().toList());
                })
                .sorted(java.util.Comparator.comparing(VeiculoPermitido::nome))
                .toList();
        if (permitidos.isEmpty() && motivos.isEmpty()) {
            motivos.add("Nenhuma regra da lista aprova o horário, prioridade, cidade e tipo de veículo");
        }
        return new VerificacaoVeiculos(permitidos, List.copyOf(motivos));
    }
}
