package br.com.imbazzu.secretariaTransporte.lista.application.query.listarAtivos;

public record ListaQueryListarAtivos(String nome, int pagina, int tamanho) {

    public ListaQueryListarAtivos {
        nome = nome == null ? "" : nome.trim();
    }
}
