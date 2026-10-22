package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class RegraDaLista {
    
    private UUID id;
    
    private UUID idLista;

    private EnumPrioridade prioridade;

    private UUID idVeiculo;

    private boolean obrigacaoAnexo;

    private Observacao descricao;

    private LocalTime inicioHorario;

    private LocalTime finalHorario;

    private RegraDaLista(UUID id, UUID idLista, UUID idVeiculo, EnumPrioridade prioridade,
        boolean obrigacaoAnexo, LocalTime inicioHorario, LocalTime finalHorario, Observacao descricao
    ){
        Objects.requireNonNull(id, "ID é obrigatório");
        Objects.requireNonNull(idLista, "Id da lista é obrigatório");
        Objects.requireNonNull(idVeiculo,"Id do veículo é obrigatório");
        Objects.requireNonNull(prioridade,"Prioridade é obrigatório");

        if((inicioHorario == null) && (finalHorario == null)){
            throw new IllegalArgumentException("Informe os dois horários ou deixe ambos vazio");
        }

        this.id = id;
        this.idLista = idLista;
        this.idVeiculo = idVeiculo;
        this.prioridade = prioridade;
        this.inicioHorario = inicioHorario;
        this.finalHorario = finalHorario;
        this.obrigacaoAnexo = obrigacaoAnexo;
        this.descricao = descricao;

    }

    public static RegraDaLista criar(UUID idLista, UUID idVeiculo, EnumPrioridade prioridade, boolean obrigacaoAnexo, Observacao descricao){
        return new RegraDaLista(UUID.randomUUID(), idLista, idVeiculo, prioridade, 
        obrigacaoAnexo, null, null, descricao);
        
    }

    public static RegraDaLista criar(UUID idLista, UUID idVeiculo, EnumPrioridade prioridade, boolean obrigacaoAnexo,
        Observacao descricao, LocalTime InicioHorario, LocalTime finalHorario){

            return new RegraDaLista(idVeiculo, idLista, idVeiculo, prioridade, obrigacaoAnexo, InicioHorario, finalHorario, descricao);
        }

    public static RegraDaLista recriar(UUID id, UUID idLista, UUID idVeiculo, EnumPrioridade prioridade, boolean obrigacaoAnexo,
        Observacao descricao, LocalTime InicioHorario, LocalTime finalHorario){
            return new RegraDaLista(id, idLista, idVeiculo, prioridade, obrigacaoAnexo, InicioHorario, finalHorario, descricao)
        }
    
    
    public void editar(LocalTime inicioHorario, LocalTime finalHorario, boolean obrigacaoAnexo, String descricao){

    } 



    //Verifica se o horario passado está entre os horarios permitidos
    public boolean horarioPermitido(LocalTime horario){

        Objects.requireNonNull(horario,"Horário é obrigatório");

        //Caso não a regra nao possuo horario definido, retorna true
        if(inicioHorario == null && finalHorario == null){
            return true;
        }


        boolean depoisOuIgualAoInicio = horario.compareTo(inicioHorario) >=0;
        boolean antesOuIgualAoFinal = horario.compareTo(finalHorario)<=0;
        
        //Caso o horario de inicio definido na regra seja mais tarde que o horario final
        //Exemplo: inicio=19:00 e final: 06:00
        //Então os horarios permitido são de 19:00 -> 00:00 e  00:01 -> 06:00
        if(inicioHorario.compareTo(finalHorario)<=0){
            return depoisOuIgualAoInicio && antesOuIgualAoFinal;
        }

        //Caso o horario de inicio definido na regra seja mais cedo que o horario final
        //Exemplo: inicio: 07:00 e final: 16:00
        //Então os horário permitidos são de 07:00 -> 16:00
        return depoisOuIgualAoInicio || antesOuIgualAoFinal;
    }

    public Observacao getDescricao() {
        return descricao;
    }

    public LocalTime getFinalHorario() {
        return finalHorario;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdLista() {
        return idLista;
    }

    public UUID getIdVeiculo() {
        return idVeiculo;
    }

    public LocalTime getInicioHorario() {
        return inicioHorario;
    }

    public EnumPrioridade getPrioridade() {
        return prioridade;
    }

}
