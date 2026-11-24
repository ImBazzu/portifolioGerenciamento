package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.DestinoView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaNaoEncontradaException;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;

/** Monta o mapa idPessoa -> detalhes usado nas telas e nos PDFs. */
public class MontadorDetalhesPassageiros {

    private final PessoaApplicationPorta pessoas;

    public MontadorDetalhesPassageiros(PessoaApplicationPorta pessoas) {
        this.pessoas = pessoas;
    }

    public Map<UUID, PassageiroDetalheView> montar(AgendaDiaResponseDto agenda, OpcoesAgendamento opcoes) {
        var noVeiculo = agenda.veiculos().stream().flatMap(v -> v.passageiros().stream()).toList();
        return montar(noVeiculo, agenda.passageirosPendentes(), opcoes);
    }

    public Map<UUID, PassageiroDetalheView> montar(
            List<AgendamentoResponseDto> passageiros,
            List<AgendamentoResponseDto> pendentes,
            OpcoesAgendamento opcoes) {
        var destinosPorId = opcoes.destinosPorId();
        Map<UUID, PassageiroDetalheView> detalhes = new HashMap<>();
        


        Stream.concat(passageiros.stream(), pendentes.stream()).forEach(agendamento ->
                detalhes.computeIfAbsent(agendamento.idPessoa(), idPessoa -> {
                            PessoaResponseDto buscarPessoa;
                        try{
                                buscarPessoa =pessoas.buscar(idPessoa);
                        }catch(PessoaNaoEncontradaException e){
                            buscarPessoa =pessoas.buscarArquivado(idPessoa);
                        }
                    
                    return detalhe(buscarPessoa,
                        destinosPorId.get(agendamento.idDestino()),
                        agendamento.idDestino());
                    }));

        return detalhes;
    }

    private static PassageiroDetalheView detalhe(PessoaResponseDto pessoa, DestinoView destino, UUID idDestino) {
        return new PassageiroDetalheView(
                pessoa.nome(),
                pessoa.cpf(),
                pessoa.telefone(),
                pessoa.endereco(),
                pessoa.observacao(),
                destino == null ? "Cidade não encontrada (%s)".formatted(idDestino) : destino.cidade(),
                destino == null ? "Destino não encontrado (%s)".formatted(idDestino) : destino.nome(),
                pessoa.prioridadeAcompanhantes());
    }
}
