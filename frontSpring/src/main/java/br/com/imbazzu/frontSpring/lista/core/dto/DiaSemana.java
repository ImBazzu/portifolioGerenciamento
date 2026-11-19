package br.com.imbazzu.frontSpring.lista.core.dto;

public enum DiaSemana {
    MONDAY("Segunda", "Seg"),
    TUESDAY("Terça", "Ter"),
    WEDNESDAY("Quarta", "Qua"),
    THURSDAY("Quinta", "Qui"),
    FRIDAY("Sexta", "Sex"),
    SATURDAY("Sábado", "Sab"),
    SUNDAY("Domingo", "Dom");

    private final String label;
    private final String labelCurto;

    DiaSemana(String label, String labelCurto) {
        this.label = label;
        this.labelCurto = labelCurto;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelCurto() {
        return labelCurto;
    }
}
