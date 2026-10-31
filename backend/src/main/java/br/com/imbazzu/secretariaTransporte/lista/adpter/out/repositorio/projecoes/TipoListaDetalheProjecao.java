package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TipoListaDetalheProjecao {

    UUID getId();

    String getNome();

    String getDescricao();

    LocalDate getDataArquivado();

    List<RegraCadastroProjecao> getRegrasCadastro();

    List<RegraSeparacaoProjecao> getRegrasSeparacao();
}
