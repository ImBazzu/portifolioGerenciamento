package br.com.imbazzu.secretariaTransporte.lista.application.query.listarArquivado;

public record ListaQueryListarArquivado(String nome, int pagina, int tamanho) {

    public ListaQueryListarArquivado {
        nome = nome == null ? "" : nome.trim();
    }
}
