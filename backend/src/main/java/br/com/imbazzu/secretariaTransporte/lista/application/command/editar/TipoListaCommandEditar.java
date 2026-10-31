package br.com.imbazzu.secretariaTransporte.lista.application.command.editar;

import java.util.UUID;

public record TipoListaCommandEditar(UUID id, String nome, String descricao) {
    
}
