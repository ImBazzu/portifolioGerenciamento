package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.util.List;
import java.util.UUID;

public class RegrasDaLista {
    
    private UUID id;
    
    private UUID idLista;

    private List<UUID> idVeiculos;

    private List<EnumPrioridadePessoa> listasDePrioridades;

    private String descricao;
}
