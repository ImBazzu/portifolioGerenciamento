package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponseDto<T> {
    private List<T> conteudo;
    private int paginaAtual;
    private int tamanhoPagina;
    private long totalElementos;
    private int totalPaginas;

    public PageResponseDto(Page<T> page) {
        this.conteudo = page.getContent();
        this.paginaAtual = page.getNumber();
        this.tamanhoPagina = page.getSize();
        this.totalElementos = page.getTotalElements();
        this.totalPaginas = page.getTotalPages();
    }

    // Getters
    public List<T> getConteudo() {
        return conteudo;
    }

    public int getPaginaAtual() {
        return paginaAtual;
    }

    public int getTamanhoPagina() {
        return tamanhoPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}
