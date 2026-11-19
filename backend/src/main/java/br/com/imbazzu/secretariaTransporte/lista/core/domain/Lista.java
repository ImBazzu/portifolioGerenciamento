package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

/**Lista responsável pela as regras de cadastro */
public final class Lista {

    //Identificador da Lista
    private final UUID id;

    //Nome da Lista
    private Nome nome;

    //Descrição
    private Observacao descricao;

    //Regras de cadastro de passageiro
    private final List<RegraCadastro> listaRegrasCadastro = new ArrayList<>();

    //Regras para unir e organizar os passageiros criados com a pendencias de veiculo
    private final List<RegraSeparacao> listaRegraSeparacao = new ArrayList<>();

    //Data do arquivamento, caso seja null então lista não está arquivada
    private LocalDate dataArquivado;

    //Privado para se ter um maior controle do Construtor
    private Lista(UUID id, Nome nome, Observacao descricao, List<RegraCadastro> listaRegrasCadastro,
            List<RegraSeparacao> listaRegraSeparacao, LocalDate dataArquivado) {
        //Itens obrigatório
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        
        //Itens opcionais
        this.descricao = descricao;
        this.dataArquivado = dataArquivado;

        //Inserindo as regras criadas
        listaRegrasCadastro.forEach(this::adicionarRegraCadastro);
        listaRegraSeparacao.forEach(this::adicionarRegraSeparacao);
    }

    /**
     * Criar uma lista
     * @param nome Titulo - obrigatorio
     * @param descricao opcional
     * @return Lista vazia
     */
    public static Lista criar(Nome nome, Observacao descricao) {
        return new Lista(UUID.randomUUID(), nome, descricao, List.of(), List.of(), null);
    }

    /**
     * Regrar lista com os dados obtido no banco de dados
     * @param id obrigatório
     * @param nome obrigatório
     * @param descricao 
     * @param listaRegrasCadastro
     * @param listaRegraSeparacao
     * @param dataArquivado
     * @return Lista recriada com os dados obtido do banco
     */
    public static Lista recriar(UUID id, Nome nome, Observacao descricao,
            List<RegraCadastro> listaRegrasCadastro, List<RegraSeparacao> listaRegraSeparacao,
            LocalDate dataArquivado) {
        //Dados obrigatório
        Objects.requireNonNull(listaRegrasCadastro, "Regras de cadastro são obrigatórias");
        Objects.requireNonNull(listaRegraSeparacao, "Regras de separação são obrigatórias");
        return new Lista(id, nome, descricao, listaRegrasCadastro, listaRegraSeparacao, dataArquivado);
    }

    /**
     * edição de dados da lista
     * @param nome obrigatório
     * @param descricao
     */
    public void editar(Nome nome, Observacao descricao) {
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.descricao = descricao;
    }

    private void adicionarRegraCadastro(RegraCadastro regra) {
        Objects.requireNonNull(regra, "Regra de cadastro é obrigatória");
        validarPertencimento(regra.getIdLista());
        if (listaRegrasCadastro.stream().anyMatch(atual -> atual.getId().equals(regra.getId()))) {
            throw new RegraDeNegocioException("Regra de cadastro já adicionada");
        }
        listaRegrasCadastro.add(regra);
    }

    public RegraCadastro criarRegraCadastro(List<UUID> idsVeiculos, List<UUID> idCidades,
            List<EnumPrioridade> prioridades, LocalTime inicioHorario, LocalTime finalHorario,
            boolean obrigacaoAnexo, Observacao descricaoRegra, List<java.time.DayOfWeek> diasSemana, Integer intervaloRepetacao) {
        RegraCadastro regra = RegraCadastro.criar(id, idsVeiculos, idCidades, prioridades,
                inicioHorario, finalHorario, obrigacaoAnexo, descricaoRegra, diasSemana, intervaloRepetacao);
        adicionarRegraCadastro(regra);
        return regra;
    }

    public void removerRegraCadastro(UUID idRegra) {
        Objects.requireNonNull(idRegra, "ID da regra de cadastro é obrigatório");
        boolean removida = listaRegrasCadastro.removeIf(regra -> regra.getId().equals(idRegra));
        if (!removida) {
            throw new RegraDeNegocioException("Regra de cadastro não pertence a este tipo de lista");
        }
    }

    public void adicionarRegraSeparacao(RegraSeparacao regra) {
        Objects.requireNonNull(regra, "Regra de separação é obrigatória");
        validarPertencimento(regra.getIdLista());
        if (listaRegraSeparacao.stream().anyMatch(atual -> atual.getId().equals(regra.getId()))) {
            throw new RegraDeNegocioException("Regra de separação já adicionada");
        }
        listaRegraSeparacao.add(regra);
    }

    public RegraSeparacao criarRegraSeparacao(UUID idTipoVeiculo, Duration tempoTolerancia) {
        RegraSeparacao regra = RegraSeparacao.criar(id, idTipoVeiculo, tempoTolerancia);
        adicionarRegraSeparacao(regra);
        return regra;
    }

    public void removerRegraSeparacao(UUID idRegra) {
        Objects.requireNonNull(idRegra, "ID da regra de separação é obrigatório");
        boolean removida = listaRegraSeparacao.removeIf(regra -> regra.getId().equals(idRegra));
        if (!removida) {
            throw new RegraDeNegocioException("Regra de separação não pertence a este tipo de lista");
        }
    }

    public void arquivar() {
        if (dataArquivado == null) {
            dataArquivado = LocalDate.now();
        }
    }

    public void desarquivar() {
        dataArquivado = null;
    }

    public boolean isArquivado() {
        return dataArquivado != null;
    }

    public UUID getId() {
        return id;
    }

    public Nome getNome() {
        return nome;
    }

    public Observacao getDescricao() {
        return descricao;
    }

    public List<RegraCadastro> getListaRegrasCadastro() {
        return List.copyOf(listaRegrasCadastro);
    }

    public List<RegraSeparacao> getListaRegraSeparacao() {
        return List.copyOf(listaRegraSeparacao);
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }

    private void validarPertencimento(UUID idLista) {
        if (!id.equals(idLista)) {
            throw new RegraDeNegocioException("A regra deve pertencer a este tipo de lista");
        }
    }
}
