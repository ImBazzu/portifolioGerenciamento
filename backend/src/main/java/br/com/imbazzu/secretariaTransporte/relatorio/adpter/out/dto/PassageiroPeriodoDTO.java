package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record PassageiroPeriodoDTO(
        UUID idPessoa, String nome, String cpf, String telefone, String especificacao,
        LocalDate data, UUID idLista, String lista, UUID idCidade, String cidade,
        UUID idDestino, String destino, UUID idTipoVeiculo, String tipoVeiculo,
        LocalTime horarioChegada, long tempoViagemSegundos, int tempoViagemNanos, int acompanhantes,
        UUID idVeiculo, LocalTime horarioSaida, Boolean horarioSaidaCalculado, boolean cancelado,
        String motorista) {

    public PassageiroPeriodoDTO comHorarioSaida(LocalTime saida) {
        return new PassageiroPeriodoDTO(idPessoa, nome, cpf, telefone, especificacao, data, idLista, lista,
                idCidade, cidade, idDestino, destino, idTipoVeiculo, tipoVeiculo, horarioChegada,
                tempoViagemSegundos, tempoViagemNanos, acompanhantes, idVeiculo, saida,
                horarioSaidaCalculado, cancelado, motorista);
    }
}
