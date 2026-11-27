package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;

public interface VeiculoAgendaProjecao {

    UUID getId();

    LocalDate getData();

    UUID getIdTipoVeiculo();

    String getNomeTipoVeiculo();

    int getCapacidade();

    String getIdentificacao();

    LocalTime getHorarioSaida();

    boolean isHorarioSaidaCalculado();

    LocalDateTime getSaidaConfirmadaEm();

    FinalidadeAberturaVeiculo getFinalidadeAbertura();

    UUID getIdListaAbertura();
}
