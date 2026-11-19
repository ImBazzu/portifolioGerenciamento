package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto;

import java.util.List;

public class EstatisticasVeiculosDTO {
    private Long totalVeiculos;
    private Long veiculosAtivos;
    private Long veiculosArquivados;
    private List<VeiculoUtilizacaoDTO> utilizacaoVeiculos;
    private Long totalAgendamentosVeiculos;
    private Double mediaUtilizacao;

    public EstatisticasVeiculosDTO() {}

    public EstatisticasVeiculosDTO(Long totalVeiculos, Long veiculosAtivos, Long veiculosArquivados) {
        this.totalVeiculos = totalVeiculos;
        this.veiculosAtivos = veiculosAtivos;
        this.veiculosArquivados = veiculosArquivados;
    }

    public Long getTotalVeiculos() {
        return totalVeiculos;
    }

    public void setTotalVeiculos(Long totalVeiculos) {
        this.totalVeiculos = totalVeiculos;
    }

    public Long getVeiculosAtivos() {
        return veiculosAtivos;
    }

    public void setVeiculosAtivos(Long veiculosAtivos) {
        this.veiculosAtivos = veiculosAtivos;
    }

    public Long getVeiculosArquivados() {
        return veiculosArquivados;
    }

    public void setVeiculosArquivados(Long veiculosArquivados) {
        this.veiculosArquivados = veiculosArquivados;
    }

    public List<VeiculoUtilizacaoDTO> getUtilizacaoVeiculos() {
        return utilizacaoVeiculos;
    }

    public void setUtilizacaoVeiculos(List<VeiculoUtilizacaoDTO> utilizacaoVeiculos) {
        this.utilizacaoVeiculos = utilizacaoVeiculos;
    }

    public Long getTotalAgendamentosVeiculos() {
        return totalAgendamentosVeiculos;
    }

    public void setTotalAgendamentosVeiculos(Long totalAgendamentosVeiculos) {
        this.totalAgendamentosVeiculos = totalAgendamentosVeiculos;
    }

    public Double getMediaUtilizacao() {
        return mediaUtilizacao;
    }

    public void setMediaUtilizacao(Double mediaUtilizacao) {
        this.mediaUtilizacao = mediaUtilizacao;
    }

    public static class VeiculoUtilizacaoDTO {
        private String identificacao;
        private String placa;
        private Long totalAgendamentos;
        private Double taxaUtilizacao;
        private Long capacidade;

        public VeiculoUtilizacaoDTO(String identificacao, String placa, Long totalAgendamentos,
                                   Double taxaUtilizacao, Long capacidade) {
            this.identificacao = identificacao;
            this.placa = placa;
            this.totalAgendamentos = totalAgendamentos;
            this.taxaUtilizacao = taxaUtilizacao;
            this.capacidade = capacidade;
        }

        public String getIdentificacao() {
            return identificacao;
        }

        public void setIdentificacao(String identificacao) {
            this.identificacao = identificacao;
        }

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }

        public Long getTotalAgendamentos() {
            return totalAgendamentos;
        }

        public void setTotalAgendamentos(Long totalAgendamentos) {
            this.totalAgendamentos = totalAgendamentos;
        }

        public Double getTaxaUtilizacao() {
            return taxaUtilizacao;
        }

        public void setTaxaUtilizacao(Double taxaUtilizacao) {
            this.taxaUtilizacao = taxaUtilizacao;
        }

        public Long getCapacidade() {
            return capacidade;
        }

        public void setCapacidade(Long capacidade) {
            this.capacidade = capacidade;
        }
    }
}
