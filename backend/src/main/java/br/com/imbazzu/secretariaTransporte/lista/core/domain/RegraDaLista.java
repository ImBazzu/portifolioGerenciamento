package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;

public class RegraDaLista {
    
    private UUID id;
    
    private UUID idLista;

    private EnumPrioridade prioridade;

    private UUID idVeiculo;

    private List<UUID> idCidades;

    private boolean obrigacaoAnexo;

    private Observacao descricao;

    private LocalTime inicioHorario;

    private LocalTime finalHorario;

    private RegraDaLista(UUID id, UUID idLista, UUID idVeiculo, List<UUID> idCidades, EnumPrioridade prioridade,
        boolean obrigacaoAnexo, LocalTime inicioHorario, LocalTime finalHorario, Observacao descricao
    ){
        Objects.requireNonNull(id, "ID é obrigatório");
        Objects.requireNonNull(idLista, "Id da lista é obrigatório");
        Objects.requireNonNull(idVeiculo,"Id do veículo é obrigatório");
        Objects.requireNonNull(prioridade,"Prioridade é obrigatório");

      

        this.id = id;
        this.idLista = idLista;
        this.idVeiculo = idVeiculo;
        this.prioridade = prioridade;
        this.idCidades = idCidades;

        definirHorario(inicioHorario, finalHorario);

        this.obrigacaoAnexo = obrigacaoAnexo;
        this.descricao = descricao;

    }

    //=====================
    //Metodos staticos
    //=====================
    
    public static RegraDaLista criar(UUID idLista, UUID idVeiculo, List<UUID> idCidades, EnumPrioridade prioridade, LocalTime inicioHorario, LocalTime finalHorario, 
        boolean obrigacaoAnexo, Observacao descricao){
            return new RegraDaLista(UUID.randomUUID(), idLista, idVeiculo, idCidades, prioridade, obrigacaoAnexo, inicioHorario, finalHorario, descricao);
        }

    public static RegraDaLista recriar(UUID id, UUID idLista, UUID idVeiculo, List<UUID> idCidades, EnumPrioridade prioridade, boolean obrigacaoAnexo,
        Observacao descricao, LocalTime InicioHorario, LocalTime finalHorario){
            return new RegraDaLista(id, idLista, idVeiculo,idCidades, prioridade, obrigacaoAnexo, InicioHorario, finalHorario, descricao);
        }
    
         
    //=====================
    //Regras
    //===================== 
    
    public void editar(LocalTime inicioHorario, LocalTime finalHorario, boolean obrigacaoAnexo, Observacao descricao){
        definirHorario(inicioHorario, finalHorario);

        editarObrigacaoAnexo(obrigacaoAnexo);

        editarDescricao(descricao);
    } 

    public boolean verificacaoDasRegras(EnumPrioridade prioridade, UUID idCidade, LocalTime horarioChegada, 
        boolean possuiAnexo, UUID idVeiculo){
        var verificarPrioridade = verificarPrioridade(prioridade);
        var verificarObrigacaoAnexo = verificarObrigacaoAnexo(possuiAnexo);
        var verificarHorario = verificarHorario(horarioChegada);
        var verificarVeiculo = verificarVeiculo(idVeiculo);
        var verificarCidade = verificarCidade(idCidade);
        return verificarHorario && verificarObrigacaoAnexo && verificarPrioridade && verificarVeiculo && verificarCidade;
    }

    private boolean verificarVeiculo(UUID idVeiculo){
        return this.idVeiculo.equals(idVeiculo);
    }

    //Verifica se o horario passado está entre os horarios permitidos
    private boolean verificarHorario(LocalTime horario){

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

    private boolean verificarPrioridade(EnumPrioridade prioridade){
        return this.prioridade == prioridade;
    }

    private boolean verificarObrigacaoAnexo(boolean anexo){
        return this.obrigacaoAnexo == anexo;
    }

    private boolean verificarCidade(UUID idCidade){
        return this.idCidades.isEmpty() || this.idCidades.contains(idCidade);
    }

    
    //=====================
    //Getters
    //=====================

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

    public List<UUID> listaCidades(){
        return List.copyOf(this.idCidades);
    }

    public EnumPrioridade getPrioridade() {
        return prioridade;
    }

    public boolean isObrigacaoAnexo() {
        return obrigacaoAnexo;
    }
    
    //=====================
    //Metodos para alteração
    //=====================

    private void definirHorario(LocalTime inicioHorario, LocalTime finalHorario){
        if((inicioHorario ==null)&&(finalHorario == null)){   
                this.inicioHorario = null;
                this.finalHorario = null;
        }else if ((inicioHorario != null)&& (finalHorario != null)){
            this.inicioHorario = inicioHorario;
            this.finalHorario = finalHorario;
        }else{
            throw new IllegalArgumentException("Informe os dois horários ou deixe ambos vazio");
        }
    }

    private void editarDescricao(Observacao descricao){
        this.descricao = descricao;
    }

    private void editarObrigacaoAnexo(boolean obrigacaoAnexo){
        this.obrigacaoAnexo = obrigacaoAnexo;
    }

}
