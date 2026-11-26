package br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;

class CidadeRequestDtoTest {

    @Test
    void validaLimitesDaDuracao() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new CidadeRequestDto("Cidade", "BA", 23, 59)).isEmpty());
            assertFalse(validator.validate(new CidadeRequestDto("Cidade", "BA", 24, 0)).isEmpty());
            assertFalse(validator.validate(new CidadeRequestDto("Cidade", "BA", 0, 60)).isEmpty());
        }
    }
}
