package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public class RegraCadastro {
    
    private final UUID id;
    
    private final UUID idLista;

    private List<EnumPrioridade> prioridades;

    private List<UUID> idsVeiculos;
    

    private List<UUID> idCidades;

    private boolean obrigacaoAnexo;

    private Observacao descricao;

    private LocalTime inicioHorario;

    private LocalTime finalHorario;

    private List<DayOfWeek> diasSemana;

    private Integer intervaloRepetacao;

    private RegraCadastro(UUID id, UUID idLista, List<UUID> idsVeiculos, List<UUID> idCidades,
        List<EnumPrioridade> prioridades,
        boolean obrigacaoAnexo, LocalTime inicioHorario, LocalTime finalHorario, Observacao descricao,
        List<DayOfWeek> diasSemana, Integer intervaloRepetacao
    ) {
        Objects.requireNonNull(id, "ID é obrigatório");
        Objects.requireNonNull(idLista, "Id da lista é obrigatório");
        this.id = id;
        this.idLista = idLista;
        this.idsVeiculos = copiarListaObrigatoria(idsVeiculos, "Ao menos um veículo é obrigatório");
        this.prioridades = copiarListaObrigatoria(prioridades, "Ao menos uma prioridade é obrigatória");
        this.idCidades = copiarListaOpcional(idCidades);
        definirHorario(inicioHorario, finalHorario);
        
        this.diasSemana = copiarListaDiasOpcional(diasSemana);
        this.intervaloRepetacao = validarIntervaloRepetacao(intervaloRepetacao);

        this.obrigacaoAnexo = obrigacaoAnexo;
        this.descricao = descricao;
    }

    //=====================
    //Metodos staticos
    //=====================
    
        public static RegraCadastro criar(UUID idLista, List<UUID> idsVeiculos, List<UUID> idCidades,
            List<EnumPrioridade> prioridades, LocalTime inicioHorario, LocalTime finalHorario,
            boolean obrigacaoAnexo, Observacao descricao, List<DayOfWeek> diasSemana, Integer intervaloRepetacao) {
            return new RegraCadastro(UUID.randomUUID(), idLista, idsVeiculos, idCidades, prioridades,
                obrigacaoAnexo, inicioHorario, finalHorario, descricao, diasSemana, intervaloRepetacao);
        }

        public static RegraCadastro recriar(UUID id, UUID idLista, List<UUID> idsVeiculos, List<UUID> idCidades,
            List<EnumPrioridade> prioridades, boolean obrigacaoAnexo, Observacao descricao,
            LocalTime inicioHorario, LocalTime finalHorario, List<DayOfWeek> diasSemana, Integer intervaloRepetacao) {
            return new RegraCadastro(id, idLista, idsVeiculos, idCidades, prioridades, obrigacaoAnexo,
                inicioHorario, finalHorario, descricao, diasSemana, intervaloRepetacao);
        }
    
         
    //=====================
    //Regras
    //===================== 
    
    public void editar(LocalTime inicioHorario, LocalTime finalHorario, boolean obrigacaoAnexo, Observacao descricao,
        List<DayOfWeek> diasSemana, Integer intervaloRepetacao) {
        definirHorario(inicioHorario, finalHorario);
        editarObrigacaoAnexo(obrigacaoAnexo);
        editarDescricao(descricao);
        this.diasSemana = copiarListaDiasOpcional(diasSemana);
        this.intervaloRepetacao = validarIntervaloRepetacao(intervaloRepetacao);
    } 

    public void editarPrioridades(List<EnumPrioridade> prioridades) {
        this.prioridades = copiarListaObrigatoria(prioridades, "Ao menos uma prioridade é obrigatória");
    }

    public void editarVeiculos(List<UUID> idsVeiculos) {
        this.idsVeiculos = copiarListaObrigatoria(idsVeiculos, "Ao menos um veículo é obrigatório");
    }

    public void editarCidades(List<UUID> idCidades) {
        this.idCidades = copiarListaOpcional(idCidades);
    }

    public boolean verificacaoDasRegras(EnumPrioridade prioridade, UUID idCidade, LocalTime horarioChegada,
        boolean possuiAnexo, UUID idVeiculo, DayOfWeek diaSemana) {
        return verificarHorario(horarioChegada)
            && verificarObrigacaoAnexo(possuiAnexo)
            && verificarPrioridade(prioridade)
            && verificarVeiculo(idVeiculo)
            && verificarCidade(idCidade)
            && verificarDiaSemana(diaSemana);
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
        return !this.obrigacaoAnexo || anexo;
    }

    private boolean verificarCidade(UUID idCidade) {
        return this.idCidades.isEmpty() || this.idCidades.contains(idCidade);
    }

    private boolean verificarDiaSemana(DayOfWeek diaSemana) {
        return this.diasSemana.isEmpty() || this.diasSemana.contains(diaSemana);
    }

    public int getIntervaloRepetacao() {
        return intervaloRepetacao != null ? intervaloRepetacao : 1;
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

    public List<DayOfWeek> getDiasSemana() {
        return diasSemana;
    }

    public Integer getIntervaloRepetacaoRaw() {
        return intervaloRepetacao;
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
                throw new DadosInvalidosException("Os dois horários informados devem ser diferentes");
            }
            this.inicioHorario = inicioHorario;
            this.finalHorario = finalHorario;
        } else {
            throw new DadosInvalidosException("Informe os dois horários ou deixe ambos vazios");
        }
    }

    private void editarDescricao(Observacao descricao) {
        this.descricao = descricao;
    }

    private void editarObrigacaoAnexo(boolean obrigacaoAnexo) {
        this.obrigacaoAnexo = obrigacaoAnexo;
    }

    private static <T> List<T> copiarListaObrigatoria(List<T> valores, String mensagem) {
        if (valores == null) {
            throw new DadosInvalidosException(mensagem);
        }
        validarSemNulos(valores, mensagem);
        List<T> copia = List.copyOf(valores);
        if (copia.isEmpty()) {
            throw new DadosInvalidosException(mensagem);
        }
        validarSemDuplicatas(copia, mensagem);
        return copia;
    }

    private static List<UUID> copiarListaOpcional(List<UUID> valores) {
        if (valores == null) {
            return List.of();
        }
        validarSemNulos(valores, "A lista de cidades contém um valor inválido");
        var copia = List.copyOf(valores);
        validarSemDuplicatas(copia, "A lista de cidades não pode conter duplicatas");
        return copia;
    }

    private static <T> void validarSemDuplicatas(List<T> valores, String mensagem) {
        if (new HashSet<>(valores).size() != valores.size()) {
            throw new DadosInvalidosException(mensagem);
        }
    }

    private static <T> void validarSemNulos(List<T> valores, String mensagem) {
        if (valores.stream().anyMatch(Objects::isNull)) {
            throw new DadosInvalidosException(mensagem);
        }
    }

    private static List<DayOfWeek> copiarListaDiasOpcional(List<DayOfWeek> dias) {
        if (dias == null) {
            return List.of();
        }
        validarSemNulos(dias, "A lista de dias da semana contém um valor inválido");
        var copia = List.copyOf(dias);
        validarSemDuplicatas(copia, "A lista de dias da semana não pode conter duplicatas");
        return copia;
    }

    private static Integer validarIntervaloRepetacao(Integer intervalo) {
        if (intervalo == null) {
            return 1;
        }
        if (intervalo < 1 || intervalo > 52) {
            throw new DadosInvalidosException("O intervalo de repetição deve estar entre 1 e 52 semanas");
        }
        return intervalo;
    }
}
