package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes;

import java.time.LocalDate;

public interface VeiculoHistoricoResumoProjecao {

    LocalDate getData();

    Long getQtdVeiculo();

    Long getTotalPassageiros();
}
