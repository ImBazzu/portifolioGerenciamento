package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

public record PassageiroDetalheView(
        String nome,
        String cpf,
        String telefone,
        String endereco,
        String observacao,
        String cidade,
        String destino) {
}
