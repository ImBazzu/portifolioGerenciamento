package br.com.imbazzu.secretariaTransporte.pessoa.application.query.listarArquivado;

import java.util.UUID;

public record PessoaQueryListarArquivado(String nome, UUID especificacaoId, Boolean validada, int pagina, int tamanho) {
}
