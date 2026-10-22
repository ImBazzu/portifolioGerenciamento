package org.yourcompany.yourproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProcurarArquivoTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void buscaPorNomeEConteudoSemDiferenciarMaiusculas() throws IOException {
        Path porNome = Files.writeString(pastaTemporaria.resolve("relatorio-PSF.pdf"), "sem correspondencia");
        Path porConteudo = Files.writeString(pastaTemporaria.resolve("anotacoes.txt"), "Referencia: psf-204");
        Files.writeString(pastaTemporaria.resolve("ignorar.txt"), "nenhum resultado");

        List<Path> resultados = ProcurarArquivo.buscarArquivos("PsF", pastaTemporaria);

        assertEquals(List.of(porNome, porConteudo).stream().sorted().toList(), resultados);
    }

    @Test
    void rejeitaTermoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> ProcurarArquivo.buscarArquivos("  ", pastaTemporaria));
    }

    @Test
    void rejeitaCaminhoQueNaoSejaPasta() {
        Path arquivo = pastaTemporaria.resolve("arquivo.txt");

        assertThrows(IllegalArgumentException.class,
                () -> ProcurarArquivo.buscarArquivos("psf", arquivo));
    }
}
