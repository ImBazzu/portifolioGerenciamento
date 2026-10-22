package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;

public class RegraDaLista {
    
    private final UUID id;
    
    private final UUID idLista;

    private List<EnumPrioridade> prioridades;

    private List<UUID> idsVeiculos;

    private List<UUID> idCidades;

    private boolean obrigacaoAnexo;

    private Observacao descricao;

    private LocalTime inicioHorario;

    private LocalTime finalHorario;

    private RegraDaLista(UUID id, UUID idLista, List<UUID> idsVeiculos, List<UUID> idCidades,
        List<EnumPrioridade> prioridades,
        boolean obrigacaoAnexo, LocalTime inicioHorario, LocalTime finalHorario, Observacao descricao
    ) {
        Objects.requireNonNull(id, "ID é obrigatório");
        Objects.requireNonNull(idLista, "Id da lista é obrigatório");
        this.id = id;
        this.idLista = idLista;
        this.idsVeiculos = copiarListaObrigatoria(idsVeiculos, "Ao menos um veículo é obrigatório");
        this.prioridades = copiarListaObrigatoria(prioridades, "Ao menos uma prioridade é obrigatória");
        this.idCidades = idCidades == null ? List.of() : List.copyOf(idCidades);
        definirHorario(inicioHorario, finalHorario);

        this.obrigacaoAnexo = obrigacaoAnexo;
        this.descricao = descricao;
    }

    //=====================
    //Metodos staticos
    //=====================
    
        public static RegraDaLista criar(UUID idLista, List<UUID> idsVeiculos, List<UUID> idCidades,
            List<EnumPrioridade> prioridades, LocalTime inicioHorario, LocalTime finalHorario,
            boolean obrigacaoAnexo, Observacao descricao) {
            return new RegraDaLista(UUID.randomUUID(), idLista, idsVeiculos, idCidades, prioridades,
                obrigacaoAnexo, inicioHorario, finalHorario, descricao);
        }

        public static RegraDaLista recriar(UUID id, UUID idLista, List<UUID> idsVeiculos, List<UUID> idCidades,
            List<EnumPrioridade> prioridades, boolean obrigacaoAnexo, Observacao descricao,
            LocalTime inicioHorario, LocalTime finalHorario) {
            return new RegraDaLista(id, idLista, idsVeiculos, idCidades, prioridades, obrigacaoAnexo,
                inicioHorario, finalHorario, descricao);
        }
    
         
    //=====================
    //Regras
    //===================== 
    
    public void editar(LocalTime inicioHorario, LocalTime finalHorario, boolean obrigacaoAnexo, Observacao descricao) {
        definirHorario(inicioHorario, finalHorario);
        editarObrigacaoAnexo(obrigacaoAnexo);
        editarDescricao(descricao);
    } 

    public void editarPrioridades(List<EnumPrioridade> prioridades) {
        this.prioridades = copiarListaObrigatoria(prioridades, "Ao menos uma prioridade é obrigatória");
    }

    public void editarVeiculos(List<UUID> idsVeiculos) {
        this.idsVeiculos = copiarListaObrigatoria(idsVeiculos, "Ao menos um veículo é obrigatório");
    }

    public void editarCidades(List<UUID> idCidades) {
        this.idCidades = idCidades == null ? List.of() : List.copyOf(idCidades);
    }

    public boolean verificacaoDasRegras(EnumPrioridade prioridade, UUID idCidade, LocalTime horarioChegada,
        boolean possuiAnexo, UUID idVeiculo) {
        return verificarHorario(horarioChegada)
            && verificarObrigacaoAnexo(possuiAnexo)
            && verificarPrioridade(prioridade)
            && verificarVeiculo(idVeiculo)
            && verificarCidade(idCidade);
    }

    private boolean verificarVeiculo(UUID idVeiculo) {
        return idsVeiculos.contains(idVeiculo);
    }

    //Verifica se o horario passado está entre os horarios permitidos
    private boolean verificarHorario(LocalTime horario) {
        Objects.requireNonNull(horario, "Horário é obrigatório");

        //Sem horário configurado, não há restrição de horário.
        if (inicioHorario == null && finalHorario == null) {
            return true;
        }

        boolean depoisOuIgualAoInicio = horario.compareTo(inicioHorario) >= 0;
        boolean antesOuIgualAoFinal = horario.compareTo(finalHorario) <= 0;
        
        //Quando início e fim estão no mesmo dia, ambos os limites são inclusivos.
        if (inicioHorario.compareTo(finalHorario) <= 0) {
            return depoisOuIgualAoInicio && antesOuIgualAoFinal;
        }

        //Quando o intervalo cruza a meia-noite, o horário está após o início ou antes do fim.
        return depoisOuIgualAoInicio || antesOuIgualAoFinal;
    }

    private boolean verificarPrioridade(EnumPrioridade prioridade) {
        return prioridades.contains(prioridade);
    }

    private boolean verificarObrigacaoAnexo(boolean anexo) {
        return this.obrigacaoAnexo == anexo;
    }

    private boolean verificarCidade(UUID idCidade) {
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

    public List<UUID> getIdsVeiculos() {
        return idsVeiculos;
    }

    public LocalTime getInicioHorario() {
        return inicioHorario;
    }

    public List<UUID> listaCidades() {
        return idCidades;
    }

    public List<EnumPrioridade> getPrioridades() {
        return prioridades;
    }

    public boolean isObrigacaoAnexo() {
        return obrigacaoAnexo;
    }
    
    //=====================
    //Metodos para alteração
    //=====================

    private void definirHorario(LocalTime inicioHorario, LocalTime finalHorario) {
        if (inicioHorario == null && finalHorario == null) {
            this.inicioHorario = null;
            this.finalHorario = null;
        } else if (inicioHorario != null && finalHorario != null) {
            if(inicioHorario.compareTo(finalHorario)==0){
                throw new IllegalArgumentException("Os dois horários informados devem ser diferentes");
            }
            this.inicioHorario = inicioHorario;
            this.finalHorario = finalHorario;
        } else {
            throw new IllegalArgumentException("Informe os dois horários ou deixe ambos vazio");
        }
    }

    private void editarDescricao(Observacao descricao) {
        this.descricao = descricao;
    }

    private void editarObrigacaoAnexo(boolean obrigacaoAnexo) {
        this.obrigacaoAnexo = obrigacaoAnexo;
    }

    private static <T> List<T> copiarListaObrigatoria(List<T> valores, String mensagem) {
        Objects.requireNonNull(valores, mensagem);
        List<T> copia = List.copyOf(valores);
        if (copia.isEmpty()) {
            throw new IllegalArgumentException(mensagem);
        }
        return copia;
    }
}
