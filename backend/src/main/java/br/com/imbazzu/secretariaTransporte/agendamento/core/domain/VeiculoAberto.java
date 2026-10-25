package br.com.imbazzu.secretariaTransporte.agendamento.core.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;
import java.util.Locale;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public record VeiculoAberto(
        UUID id,
        LocalDate data,
        UUID idTipoVeiculo,
        String nomeTipoVeiculo,
        int capacidade,
        String identificacao,
        LocalTime horarioSaida,
        boolean horarioSaidaCalculado) {

    public VeiculoAberto {
        Objects.requireNonNull(id, "ID do veículo é obrigatório");
        Objects.requireNonNull(data, "Data é obrigatória");
        Objects.requireNonNull(idTipoVeiculo, "Tipo de veículo é obrigatório");
        Objects.requireNonNull(nomeTipoVeiculo, "Nome do tipo de veículo é obrigatório");
        if (capacidade < 1) {
            throw new DadosInvalidosException("A capacidade do veículo deve ser positiva");
        }
    }

    public boolean calculaHorarioSaida() {
        return horarioSaidaCalculado;
    }

    public static boolean tipoCalculaHorarioSaida(String nomeTipoVeiculo) {
        return nomeTipoVeiculo.strip().toLowerCase(Locale.ROOT).startsWith("carro");
    }
}
