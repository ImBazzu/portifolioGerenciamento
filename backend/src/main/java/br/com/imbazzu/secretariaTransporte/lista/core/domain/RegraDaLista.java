package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.LocalTime;
import java.util.UUID;

public class RegraDaLista {
    
    private UUID id;
    
    private UUID idLista;

    private EnumPrioridade prioridade;

    private UUID idVeiculo;

    private boolean obrigacaoAnexo;

    private String descricao;

    private LocalTime inicioHorario;

    private LocalTime finalHorario;

    private RegraDaLista(UUID id, UUID idLista, UUID idVeiculo, EnumPrioridade prioridade,
        boolean obrigacaoAnexo, LocalTime inicioHorario, LocalTime finalHorario, String descricao
    )

}
