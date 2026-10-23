package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

public final class TipoLista {

    private final UUID id;

    private Nome nome;

    private Observacao descricao;

    private final List<RegraCadastro> listaRegrasCadastro;

    private final List<RegraSeparacao> listaRegraSeparacao;

    private LocalDate dataArquivado;

    private TipoLista(UUID id, Nome nome, Observacao descricao, List<RegraCadastro> listaRegrasCadastro,
            List<RegraSeparacao> listaRegraSeparacao, LocalDate dataArquivado) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.descricao = descricao;
        this.listaRegrasCadastro = new ArrayList<>();
        this.listaRegraSeparacao = new ArrayList<>();
        this.dataArquivado = dataArquivado;

        listaRegrasCadastro.forEach(this::adicionarRegraCadastro);
        listaRegraSeparacao.forEach(this::adicionarRegraSeparacao);
    }

    public static TipoLista criar(Nome nome, Observacao descricao) {
        return new TipoLista(UUID.randomUUID(), nome, descricao, List.of(), List.of(), null);
    }

    public static TipoLista recriar(UUID id, Nome nome, Observacao descricao,
            List<RegraCadastro> listaRegrasCadastro, List<RegraSeparacao> listaRegraSeparacao,
            LocalDate dataArquivado) {
        Objects.requireNonNull(listaRegrasCadastro, "Regras de cadastro são obrigatórias");
        Objects.requireNonNull(listaRegraSeparacao, "Regras de separação são obrigatórias");
        return new TipoLista(id, nome, descricao, listaRegrasCadastro, listaRegraSeparacao, dataArquivado);
    }

    public void editar(Nome nome, Observacao descricao) {
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.descricao = descricao;
    }

    public void adicionarRegraCadastro(RegraCadastro regra) {
        Objects.requireNonNull(regra, "Regra de cadastro é obrigatória");
        validarPertencimento(regra.getIdLista());
        if (listaRegrasCadastro.stream().anyMatch(atual -> atual.getId().equals(regra.getId()))) {
            throw new IllegalArgumentException("Regra de cadastro já adicionada");
        }
        listaRegrasCadastro.add(regra);
    }

    public RegraCadastro criarRegraCadastro(List<UUID> idsVeiculos, List<UUID> idCidades,
            List<EnumPrioridade> prioridades, LocalTime inicioHorario, LocalTime finalHorario,
            boolean obrigacaoAnexo, Observacao descricaoRegra) {
        RegraCadastro regra = RegraCadastro.criar(id, idsVeiculos, idCidades, prioridades,
                inicioHorario, finalHorario, obrigacaoAnexo, descricaoRegra);
        adicionarRegraCadastro(regra);
        return regra;
    }

    public void removerRegraCadastro(UUID idRegra) {
        Objects.requireNonNull(idRegra, "ID da regra de cadastro é obrigatório");
        boolean removida = listaRegrasCadastro.removeIf(regra -> regra.getId().equals(idRegra));
        if (!removida) {
            throw new IllegalArgumentException("Regra de cadastro não pertence a este tipo de lista");
        }
    }

    public void adicionarRegraSeparacao(RegraSeparacao regra) {
        Objects.requireNonNull(regra, "Regra de separação é obrigatória");
        validarPertencimento(regra.getIdLista());
        if (listaRegraSeparacao.stream().anyMatch(atual -> atual.getId().equals(regra.getId()))) {
            throw new IllegalArgumentException("Regra de separação já adicionada");
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
            throw new IllegalArgumentException("Regra de separação não pertence a este tipo de lista");
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
            throw new IllegalArgumentException("A regra deve pertencer a este tipo de lista");
        }
    }
}
