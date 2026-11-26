package br.com.imbazzu.secretariaTransporte.pessoa.application.query.listar;

import java.util.UUID;

public record PessoaQueryListar(String nome, UUID especificacaoId, int pagina, int tamanho) {
}
