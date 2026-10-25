package br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out;

import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;

public interface AgendamentoPorta {

    boolean diaAberto(LocalDate data);

    void abrirDia(LocalDate data);

    boolean existeAgendamento(LocalDate data, UUID idPessoa);

    UUID salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(UUID id);

    void deletar(UUID id);

    List<Agendamento> listarPorData(LocalDate data);

    PessoaAgendaDados buscarPessoa(UUID id);

    DestinoAgendaDados buscarDestino(UUID id);

    boolean listaAtiva(UUID id);

    List<RegraCadastroAgendaDados> listarRegrasCadastro(UUID idLista);

    TipoVeiculoAgendaDados buscarTipoVeiculo(UUID id);

    Optional<Duration> buscarToleranciaSeparacao(UUID idLista, UUID idTipoVeiculo);

    VeiculoAberto salvar(VeiculoAberto veiculo);

    Optional<VeiculoAberto> buscarVeiculo(UUID id);

    List<VeiculoAberto> listarVeiculos(LocalDate data);
}
