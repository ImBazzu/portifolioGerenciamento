package br.com.imbazzu.frontSpring.relatorio.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record PassageiroPeriodo(
        UUID idPessoa, String nome, String cpf, String telefone, String especificacao,
        LocalDate data, UUID idLista, String lista, UUID idCidade, String cidade,
        UUID idDestino, String destino, UUID idTipoVeiculo, String tipoVeiculo,
        LocalTime horarioChegada, long tempoViagemSegundos, int tempoViagemNanos, int acompanhantes,
        UUID idVeiculo, LocalTime horarioSaida, Boolean horarioSaidaCalculado) {
}
