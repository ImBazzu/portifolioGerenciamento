package br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.dto;

import java.util.List;
import java.util.Map;

public class EstatisticasPessoasDTO {
    private Long totalPessoas;
    private Long pessoasAtivas;
    private Long pessoasArquivadas;
    private List<PessoaMaisFrequenteDTO> pessoasMaisFrequentes;
    private List<PessoaPorEspecificacaoDTO> pessoasPorEspecificacao;
    private Map<String, Long> pessoasPorLocalidade;
    private Double mediaAcompanhantes;

    public EstatisticasPessoasDTO() {}

    public EstatisticasPessoasDTO(Long totalPessoas, Long pessoasAtivas, Long pessoasArquivadas) {
        this.totalPessoas = totalPessoas;
        this.pessoasAtivas = pessoasAtivas;
        this.pessoasArquivadas = pessoasArquivadas;
    }

    public Long getTotalPessoas() {
        return totalPessoas;
    }

    public void setTotalPessoas(Long totalPessoas) {
        this.totalPessoas = totalPessoas;
    }

    public Long getPessoasAtivas() {
        return pessoasAtivas;
    }

    public void setPessoasAtivas(Long pessoasAtivas) {
        this.pessoasAtivas = pessoasAtivas;
    }

    public Long getPessoasArquivadas() {
        return pessoasArquivadas;
    }

    public void setPessoasArquivadas(Long pessoasArquivadas) {
        this.pessoasArquivadas = pessoasArquivadas;
    }

    public List<PessoaMaisFrequenteDTO> getPessoasMaisFrequentes() {
        return pessoasMaisFrequentes;
    }

    public void setPessoasMaisFrequentes(List<PessoaMaisFrequenteDTO> pessoasMaisFrequentes) {
        this.pessoasMaisFrequentes = pessoasMaisFrequentes;
    }

    public List<PessoaPorEspecificacaoDTO> getPessoasPorEspecificacao() {
        return pessoasPorEspecificacao;
    }

    public void setPessoasPorEspecificacao(List<PessoaPorEspecificacaoDTO> pessoasPorEspecificacao) {
        this.pessoasPorEspecificacao = pessoasPorEspecificacao;
    }

    public Map<String, Long> getPessoasPorLocalidade() {
        return pessoasPorLocalidade;
    }

    public void setPessoasPorLocalidade(Map<String, Long> pessoasPorLocalidade) {
        this.pessoasPorLocalidade = pessoasPorLocalidade;
    }

    public Double getMediaAcompanhantes() {
        return mediaAcompanhantes;
    }

    public void setMediaAcompanhantes(Double mediaAcompanhantes) {
        this.mediaAcompanhantes = mediaAcompanhantes;
    }

    public static class PessoaMaisFrequenteDTO {
        private String nome;
        private Long totalAgendamentos;
        private String especificacao;

        public PessoaMaisFrequenteDTO(String nome, Long totalAgendamentos, String especificacao) {
            this.nome = nome;
            this.totalAgendamentos = totalAgendamentos;
            this.especificacao = especificacao;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Long getTotalAgendamentos() {
            return totalAgendamentos;
        }

        public void setTotalAgendamentos(Long totalAgendamentos) {
            this.totalAgendamentos = totalAgendamentos;
        }

        public String getEspecificacao() {
            return especificacao;
        }

        public void setEspecificacao(String especificacao) {
            this.especificacao = especificacao;
        }
    }

    public static class PessoaPorEspecificacaoDTO {
        private String especificacao;
        private Long quantidade;

        public PessoaPorEspecificacaoDTO(String especificacao, Long quantidade) {
            this.especificacao = especificacao;
            this.quantidade = quantidade;
        }

        public String getEspecificacao() {
            return especificacao;
        }

        public void setEspecificacao(String especificacao) {
            this.especificacao = especificacao;
        }

        public Long getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Long quantidade) {
            this.quantidade = quantidade;
        }
    }
}
