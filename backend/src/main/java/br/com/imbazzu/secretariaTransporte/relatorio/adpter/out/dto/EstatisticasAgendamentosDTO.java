package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EstatisticasAgendamentosDTO {
    private Long totalAgendamentos;
    private Long agendamentosRealizados;
    private Long agendamentosCancelados;
    private Long agendamentosPendentes;
    private Double taxaRealizacao;
    private List<AgendamentoPorDataDTO> agendamentosPorData;
    private List<AgendamentoPorDestinoDTO> agendamentosPorDestino;
    private List<AgendamentoPorVeiculoDTO> agendamentosPorVeiculo;
    private Map<String, Long> agendamentosPorEspecificacao;

    public EstatisticasAgendamentosDTO() {}

    public EstatisticasAgendamentosDTO(Long totalAgendamentos, Long agendamentosRealizados,
                                      Long agendamentosCancelados, Long agendamentosPendentes,
                                      Double taxaRealizacao) {
        this.totalAgendamentos = totalAgendamentos;
        this.agendamentosRealizados = agendamentosRealizados;
        this.agendamentosCancelados = agendamentosCancelados;
        this.agendamentosPendentes = agendamentosPendentes;
        this.taxaRealizacao = taxaRealizacao;
    }

    public Long getTotalAgendamentos() {
        return totalAgendamentos;
    }

    public void setTotalAgendamentos(Long totalAgendamentos) {
        this.totalAgendamentos = totalAgendamentos;
    }

    public Long getAgendamentosRealizados() {
        return agendamentosRealizados;
    }

    public void setAgendamentosRealizados(Long agendamentosRealizados) {
        this.agendamentosRealizados = agendamentosRealizados;
    }

    public Long getAgendamentosCancelados() {
        return agendamentosCancelados;
    }

    public void setAgendamentosCancelados(Long agendamentosCancelados) {
        this.agendamentosCancelados = agendamentosCancelados;
    }

    public Long getAgendamentosPendentes() {
        return agendamentosPendentes;
    }

    public void setAgendamentosPendentes(Long agendamentosPendentes) {
        this.agendamentosPendentes = agendamentosPendentes;
    }

    public Double getTaxaRealizacao() {
        return taxaRealizacao;
    }

    public void setTaxaRealizacao(Double taxaRealizacao) {
        this.taxaRealizacao = taxaRealizacao;
    }

    public List<AgendamentoPorDataDTO> getAgendamentosPorData() {
        return agendamentosPorData;
    }

    public void setAgendamentosPorData(List<AgendamentoPorDataDTO> agendamentosPorData) {
        this.agendamentosPorData = agendamentosPorData;
    }

    public List<AgendamentoPorDestinoDTO> getAgendamentosPorDestino() {
        return agendamentosPorDestino;
    }

    public void setAgendamentosPorDestino(List<AgendamentoPorDestinoDTO> agendamentosPorDestino) {
        this.agendamentosPorDestino = agendamentosPorDestino;
    }

    public List<AgendamentoPorVeiculoDTO> getAgendamentosPorVeiculo() {
        return agendamentosPorVeiculo;
    }

    public void setAgendamentosPorVeiculo(List<AgendamentoPorVeiculoDTO> agendamentosPorVeiculo) {
        this.agendamentosPorVeiculo = agendamentosPorVeiculo;
    }

    public Map<String, Long> getAgendamentosPorEspecificacao() {
        return agendamentosPorEspecificacao;
    }

    public void setAgendamentosPorEspecificacao(Map<String, Long> agendamentosPorEspecificacao) {
        this.agendamentosPorEspecificacao = agendamentosPorEspecificacao;
    }

    public static class AgendamentoPorDataDTO {
        private LocalDate data;
        private Long quantidade;
        private Long realizados;
        private Long cancelados;

        public AgendamentoPorDataDTO(LocalDate data, Long quantidade, Long realizados, Long cancelados) {
            this.data = data;
            this.quantidade = quantidade;
            this.realizados = realizados;
            this.cancelados = cancelados;
        }

        public LocalDate getData() {
            return data;
        }

        public void setData(LocalDate data) {
            this.data = data;
        }

        public Long getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Long quantidade) {
            this.quantidade = quantidade;
        }

        public Long getRealizados() {
            return realizados;
        }

        public void setRealizados(Long realizados) {
            this.realizados = realizados;
        }

        public Long getCancelados() {
            return cancelados;
        }

        public void setCancelados(Long cancelados) {
            this.cancelados = cancelados;
        }
    }

    public static class AgendamentoPorDestinoDTO {
        private String destino;
        private Long quantidade;

        public AgendamentoPorDestinoDTO(String destino, Long quantidade) {
            this.destino = destino;
            this.quantidade = quantidade;
        }

        public String getDestino() {
            return destino;
        }

        public void setDestino(String destino) {
            this.destino = destino;
        }

        public Long getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Long quantidade) {
            this.quantidade = quantidade;
        }
    }

    public static class AgendamentoPorVeiculoDTO {
        private String veiculo;
        private Long quantidade;
        private Double taxaUtilizacao;

        public AgendamentoPorVeiculoDTO(String veiculo, Long quantidade, Double taxaUtilizacao) {
            this.veiculo = veiculo;
            this.quantidade = quantidade;
            this.taxaUtilizacao = taxaUtilizacao;
        }

        public String getVeiculo() {
            return veiculo;
        }

        public void setVeiculo(String veiculo) {
            this.veiculo = veiculo;
        }

        public Long getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Long quantidade) {
            this.quantidade = quantidade;
        }

        public Double getTaxaUtilizacao() {
            return taxaUtilizacao;
        }

        public void setTaxaUtilizacao(Double taxaUtilizacao) {
            this.taxaUtilizacao = taxaUtilizacao;
        }
    }
}
