package br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

public interface AgendamentoQueryPorta {

    boolean diaAberto(LocalDate data);

    boolean existeAgendamento(LocalDate data, UUID idPessoa);

    Optional<Agendamento> buscarAgendamento(UUID id);

    List<Agendamento> listarAgendamentos(LocalDate data);

    PessoaAgendaDados buscarPessoa(UUID id);

    DestinoAgendaDados buscarDestino(UUID id);

    boolean listaAtiva(UUID id);

    List<RegraCadastroAgendaDados> listarRegrasCadastro(UUID idLista);

    TipoVeiculoAgendaDados buscarTipoVeiculo(UUID id);

    Optional<Duration> buscarToleranciaSeparacao(UUID idLista, UUID idTipoVeiculo);

    Optional<VeiculoAberto> buscarVeiculo(UUID id);

    List<VeiculoAberto> listarVeiculos(LocalDate data);

    Map<UUID, List<UUID>> buscarDestinosPorCidadeId(LocalDate data);
}
