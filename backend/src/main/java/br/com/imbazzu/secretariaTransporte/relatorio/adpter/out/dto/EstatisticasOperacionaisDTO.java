package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto;

import java.util.List;

public class EstatisticasOperacionaisDTO {
    private Double eficienciaOperacional;
    private Double taxaRealizacaoGeral;
    private Double taxaCancelamento;
    private Long agendamentosPendentes;
    private Long agendamentosCancelados;
    private List<EficienciaPorDataDTO> eficienciaPorData;
    private List<EficienciaPorDestinoDTO> eficienciaPorDestino;

    public EstatisticasOperacionaisDTO() {}

    public EstatisticasOperacionaisDTO(Double eficienciaOperacional, Double taxaRealizacaoGeral,
                                      Double taxaCancelamento, Long agendamentosPendentes,
                                      Long agendamentosCancelados) {
        this.eficienciaOperacional = eficienciaOperacional;
        this.taxaRealizacaoGeral = taxaRealizacaoGeral;
        this.taxaCancelamento = taxaCancelamento;
        this.agendamentosPendentes = agendamentosPendentes;
        this.agendamentosCancelados = agendamentosCancelados;
    }

    public Double getEficienciaOperacional() {
        return eficienciaOperacional;
    }

    public void setEficienciaOperacional(Double eficienciaOperacional) {
        this.eficienciaOperacional = eficienciaOperacional;
    }

    public Double getTaxaRealizacaoGeral() {
        return taxaRealizacaoGeral;
    }

    public void setTaxaRealizacaoGeral(Double taxaRealizacaoGeral) {
        this.taxaRealizacaoGeral = taxaRealizacaoGeral;
    }

    public Double getTaxaCancelamento() {
        return taxaCancelamento;
    }

    public void setTaxaCancelamento(Double taxaCancelamento) {
        this.taxaCancelamento = taxaCancelamento;
    }

    public Long getAgendamentosPendentes() {
        return agendamentosPendentes;
    }

    public void setAgendamentosPendentes(Long agendamentosPendentes) {
        this.agendamentosPendentes = agendamentosPendentes;
    }

    public Long getAgendamentosCancelados() {
        return agendamentosCancelados;
    }

    public void setAgendamentosCancelados(Long agendamentosCancelados) {
        this.agendamentosCancelados = agendamentosCancelados;
    }

    public List<EficienciaPorDataDTO> getEficienciaPorData() {
        return eficienciaPorData;
    }

    public void setEficienciaPorData(List<EficienciaPorDataDTO> eficienciaPorData) {
        this.eficienciaPorData = eficienciaPorData;
    }

    public List<EficienciaPorDestinoDTO> getEficienciaPorDestino() {
        return eficienciaPorDestino;
    }

    public void setEficienciaPorDestino(List<EficienciaPorDestinoDTO> eficienciaPorDestino) {
        this.eficienciaPorDestino = eficienciaPorDestino;
    }

    public static class EficienciaPorDataDTO {
        private String data;
        private Double eficiencia;
        private Long total;
        private Long realizados;

        public EficienciaPorDataDTO(String data, Double eficiencia, Long total, Long realizados) {
            this.data = data;
            this.eficiencia = eficiencia;
            this.total = total;
            this.realizados = realizados;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Double getEficiencia() {
            return eficiencia;
        }

        public void setEficiencia(Double eficiencia) {
            this.eficiencia = eficiencia;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getRealizados() {
            return realizados;
        }

        public void setRealizados(Long realizados) {
            this.realizados = realizados;
        }
    }

    public static class EficienciaPorDestinoDTO {
        private String destino;
        private Double eficiencia;
        private Long total;

        public EficienciaPorDestinoDTO(String destino, Double eficiencia, Long total) {
            this.destino = destino;
            this.eficiencia = eficiencia;
            this.total = total;
        }

        public String getDestino() {
            return destino;
        }

        public void setDestino(String destino) {
            this.destino = destino;
        }

        public Double getEficiencia() {
            return eficiencia;
        }

        public void setEficiencia(Double eficiencia) {
            this.eficiencia = eficiencia;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }
    }
}
