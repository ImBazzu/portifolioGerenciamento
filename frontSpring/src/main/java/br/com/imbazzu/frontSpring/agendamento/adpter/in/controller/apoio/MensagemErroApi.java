package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import java.util.List;

import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Extrai uma mensagem legível do corpo de erro devolvido pela API. */
public class MensagemErroApi {

    private static final List<String> CAMPOS = List.of("mensagem", "message", "detail", "erro");

    private final ObjectMapper objectMapper;

    public MensagemErroApi(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String de(RestClientResponseException e) {
        try {
            var json = objectMapper.readTree(e.getResponseBodyAsString());
            if (json != null) {
                for (var campo : CAMPOS) {
                    if (json.hasNonNull(campo)) {
                        return json.get(campo).asText();
                    }
                }
            }
        } catch (JsonProcessingException ignored) {
            // cai no texto padrão abaixo
        }
        return "A API retornou HTTP %d.".formatted(e.getStatusCode().value());
    }
}
