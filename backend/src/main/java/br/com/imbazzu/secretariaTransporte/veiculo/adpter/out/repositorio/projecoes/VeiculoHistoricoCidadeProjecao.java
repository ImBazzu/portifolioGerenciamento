package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.projecoes;

import java.time.LocalDate;

public interface VeiculoHistoricoCidadeProjecao {

    LocalDate getData();

    String getCidade();
}
