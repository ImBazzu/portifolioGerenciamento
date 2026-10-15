package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.salvar;

public record CidadeCommandSalvar(
    String nome,
    String estado,
    Integer horas,
    Integer minutos
) {
    
}
