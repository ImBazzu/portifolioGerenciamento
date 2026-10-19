package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller.dto;

import java.util.List;

// -------------------------------------------------------------------------
// RegraViagem
// -------------------------------------------------------------------------
    /**
     * Resposta com as configurações atuais.
     */
    public record RegraViagemResponseDto(
            int capacidadeMaxima,
            String tempoTolerancia,
            List<ConjuntoResponseDto> conjuntos
    ) {}

