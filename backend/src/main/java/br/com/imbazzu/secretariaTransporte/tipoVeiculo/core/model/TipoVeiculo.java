package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa o tipo de veículo na secretaria de transporte.
 */
public class TipoVeiculo {
    
    //Atributos
    /** Identificador único do tipo de veículo. */
    private UUID id;

    //** Nome do tipo de veículo. */
    private NomeVeiculo nome;

    //** Capacidade do tipo de veículo. */
    private int capacidade;

    //** Data em que o tipo de veículo foi arquivado. */
    private LocalDate dataArquivado;

    /**
     * Construtor privado para criar uma instância de TipoVeiculo.
     * @param id
     * @param nome
     * @param capacidade
     * @param dataArquivado
     */
    private TipoVeiculo(UUID id, NomeVeiculo nome, int capacidade, LocalDate dataArquivado) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.dataArquivado = dataArquivado;
    }

    /**
     * Cria uma nova instância de TipoVeiculo.
     * @param nome
     * @param capacidade
     * @return
     */
    public static TipoVeiculo criar(NomeVeiculo nome, int capacidade) {
        // Validações de nulidade e capacidade
        Objects.requireNonNull(nome, "Nome do veiculo é obrigatório");
        // Validação de capacidade
        if (capacidade <= 0) {
            throw new DadosInvalidosException("Capacidade informada não pode ser igual ou menor que zero");
        }
        // Cria uma nova instância de TipoVeiculo com um UUID gerado aleatoriamente
        return new TipoVeiculo(UUID.randomUUID(), nome, capacidade, null);
    }

    /**
     * Recria uma instância de TipoVeiculo.
     * @param id
     * @param nome
     * @param capacidade
     * @param dataArquivado
     * @return
     */
    public static TipoVeiculo recriar(UUID id, NomeVeiculo nome, int capacidade, LocalDate dataArquivado) {
        // Validações de nulidade e capacidade
        Objects.requireNonNull(id, "Id do veiculo é obrigatório");
        Objects.requireNonNull(nome, "Nome do veiculo é obrigatório");
        // Validação de capacidade
        if (capacidade <= 0) {
            throw new DadosInvalidosException("Capacidade informada não pode ser igual ou menor que zero");
        }
        // Cria uma nova instância de TipoVeiculo com os valores fornecidos
        return new TipoVeiculo(id, nome, capacidade, dataArquivado);
    }

    //===============================
    // Métodos de negócio
    //===============================
    public void editarVeiculo(NomeVeiculo nome, int capacidade) {
        // Valida se a operação é permitida antes de alterar os atributos
        validarOperacaoPermitida();
        // Altera os atributos do tipo de veículo
        alterarCapacidade(capacidade);
        alterarNome(nome);
    }

    public void alterarNome(NomeVeiculo nome) {
        // Validações de nulidade
        Objects.requireNonNull(nome, "Nome do veiculo é obrigatório");
        this.nome = nome;
    }

    public void alterarCapacidade(int capacidade) {
        // Validação de capacidade
        if (capacidade <= 0) {
            throw new DadosInvalidosException("Capacidade informada não pode ser igual ou menor que zero");
        }
        this.capacidade = capacidade;
    }

    public void arquivar() {
        // Valida se a operação é permitida antes de arquivar o tipo de veículo
        validarOperacaoPermitida();
        // Define a data de arquivamento como a data atual
        this.dataArquivado = LocalDate.now();
    }

    public void desarquivar() {
        // Valida se a operação é permitida antes de desarquivar o tipo de veículo
        validarOperacaoPermitida();
        // Define a data de arquivamento como null, indicando que o tipo de veículo não está mais arquivado
        this.dataArquivado = null;
    }

    public void validarExclusao() {
        // Valida se a operação é permitida antes de excluir o tipo de veículo
        validarOperacaoPermitida();
        // Valida se o tipo de veículo está arquivado antes de permitir a exclusão
        if(dataArquivado == null) {
            throw new DadosInvalidosException("Não é permitido excluir um tipo de veículo que não esteja arquivado");
        }
    }

    public void validarOperacaoPermitida() {
        // Valida se o nome do tipo de veículo é "Carro" (ignorando maiúsculas/minúsculas) e lança uma exceção se for o caso
        if ("carro".equalsIgnoreCase(nome.valor())){
            throw new DadosInvalidosException("Não é permitido realizar esta operação no tipo de veículo 'Carro'");
        }
    }

    //===============================
    // Getters
    //===============================

    public UUID getId() {
        return id;
    }

    public NomeVeiculo getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }
}
