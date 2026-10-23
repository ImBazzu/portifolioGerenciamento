package br.com.imbazzu.frontSpring.lista.core.dto;

import java.util.List;
import java.util.UUID;

public record ListaResponseDto(
        UUID id,
        String nome,
        String descricao,
        long totalRegras,
        long totalCidades,
        boolean arquivado,
        List<String> regras,
        List<String> cidades,
        List<ListaDetalheApiDto.RegraCadastro> regrasCadastroDados,
        List<ListaDetalheApiDto.RegraSeparacao> regrasSeparacaoDados
) {
}
