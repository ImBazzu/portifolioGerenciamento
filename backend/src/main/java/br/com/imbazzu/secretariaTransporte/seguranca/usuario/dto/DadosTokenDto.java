package br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto;

import java.util.List;

public record DadosTokenDto(String nome, List<String> roles) {
}
