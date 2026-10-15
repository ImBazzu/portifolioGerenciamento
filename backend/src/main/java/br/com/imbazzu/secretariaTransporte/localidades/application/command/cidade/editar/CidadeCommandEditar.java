package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.editar;

import java.util.UUID;

public record CidadeCommandEditar(UUID id, String nome, String estado, Integer horas, Integer minutos) {

}
