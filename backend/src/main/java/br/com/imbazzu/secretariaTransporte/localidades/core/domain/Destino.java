package br.com.imbazzu.secretariaTransporte.localidades.core.domain;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Destino {

    /**
     * Identificador único
     */
    private final UUID id;

    /**
     * Nome do lugar
     */
    private Nome nome;

    /**
     * Cidade dona de destino
     */
    private final UUID cidadeId;

    /**
     * arquivado
     */
    private LocalDate dataArquivado;

    // ====================================================================================
    // Construtores
    // ====================================================================================

    /**
     * Construtor para o mapper
     *
     * @param nome nome do destino
     */
    private Destino(UUID id, UUID cidadeId, Nome nome, LocalDate dataArquivado) {
        Objects.requireNonNull(id, "ID é obrigatório");
        Objects.requireNonNull(cidadeId, "Id cidade é obrigatório");
        Objects.requireNonNull(nome, "Nome é obrigatório");

        this.id = id;
        this.nome = nome;
        this.cidadeId = cidadeId;
        this.dataArquivado = dataArquivado;
    }

    /**
     * Construtor para a criação do destino gerenciado pela cidade
     * 
     * @param nome nome da cidade
     */
    public static Destino criar(UUID cidadeId, Nome nome) {
        return new Destino(UUID.randomUUID(), cidadeId, nome, null);
    }

    public static Destino reconstruir(UUID id, UUID cidadeId, Nome nome, LocalDate dataArquivado) {
        return new Destino(id, cidadeId, nome, dataArquivado);
    }

    // ====================================================================================
    // Regras de Negócios
    // ====================================================================================

    public void alterarNome(Nome novoNome) {
        Objects.requireNonNull(novoNome, "Nome é obrigatório");

        this.nome = novoNome;
    }

    public void arquivar() {

        this.dataArquivado = LocalDate.now();
    }

    public void desarquivar() {

        this.dataArquivado = null;
    }

    // ====================================================================================
    // Getters
    // ====================================================================================

    public UUID getId() {
        return id;
    }

    public Nome getNome() {
        return nome;
    }

    public UUID getIdCidade() {
        return cidadeId;
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }
}
