package br.com.imbazzu.frontSpring.pessoa.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EnumPrioridade {
    @JsonProperty("BAIXA")
    BAIXA("Baixa"),
    @JsonProperty("MODERADA")
    MODERADA("Moderada"),
    @JsonProperty("ALTA")
    ALTA("Alta"),
    @JsonProperty("URGENTE")
    URGENTE("Urgente");

    private final String label;

    EnumPrioridade(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
