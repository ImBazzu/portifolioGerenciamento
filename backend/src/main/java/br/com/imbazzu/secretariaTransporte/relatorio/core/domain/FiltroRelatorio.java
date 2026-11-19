package br.com.imbazzu.secretariaTransporte.relatorio.core.domain;

import java.time.LocalDate;
import java.util.List;

public class FiltroRelatorio {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<Long> idsPessoa;
    private List<Long> idsVeiculo;
    private List<Long> idsDestino;
    private List<Long> idsEspecificacao;
    private Integer pagina;
    private Integer tamanho;

    public FiltroRelatorio(LocalDate dataInicio, LocalDate dataFim, List<Long> idsPessoa,
                          List<Long> idsVeiculo, List<Long> idsDestino, List<Long> idsEspecificacao,
                          Integer pagina, Integer tamanho) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.idsPessoa = idsPessoa;
        this.idsVeiculo = idsVeiculo;
        this.idsDestino = idsDestino;
        this.idsEspecificacao = idsEspecificacao;
        this.pagina = pagina != null ? pagina : 0;
        this.tamanho = tamanho != null ? tamanho : 20;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public List<Long> getIdsPessoa() {
        return idsPessoa;
    }

    public List<Long> getIdsVeiculo() {
        return idsVeiculo;
    }

    public List<Long> getIdsDestino() {
        return idsDestino;
    }

    public List<Long> getIdsEspecificacao() {
        return idsEspecificacao;
    }

    public Integer getPagina() {
        return pagina;
    }

    public Integer getTamanho() {
        return tamanho;
    }
}
