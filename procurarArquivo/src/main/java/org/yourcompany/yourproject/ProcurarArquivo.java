package org.yourcompany.yourproject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class ProcurarArquivo {
    private static final String TERMO_PADRAO = "PSF";
    private static final int LIMITE_TEXTO_EXTRAIDO = 1_000_000;
    private static final long TAMANHO_MAXIMO_ARQUIVO = 100L * 1024 * 1024;

    private ProcurarArquivo() {
    }

    public static List<Path> buscar() {
        return buscarArquivos(TERMO_PADRAO, Path.of("."));
    }

    public static List<Path> buscarArquivos(String termo, Path raiz) {
        if (termo == null || termo.isBlank()) {
            throw new IllegalArgumentException("O termo de busca não pode estar vazio.");
        }
        if (raiz == null || !Files.isDirectory(raiz)) {
            throw new IllegalArgumentException("O caminho informado não é uma pasta acessível: " + raiz);
        }

        String termoNormalizado = termo.toLowerCase(Locale.ROOT);
        Path raizAbsoluta = raiz.toAbsolutePath().normalize();
        List<Path> resultados = new ArrayList<>();

        try {
            Files.walkFileTree(raizAbsoluta, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path arquivo, BasicFileAttributes atributos) {
                    if (!atributos.isRegularFile()) {
                        return FileVisitResult.CONTINUE;
                    }

                    String nome = arquivo.getFileName().toString().toLowerCase(Locale.ROOT);
                    if (nome.contains(termoNormalizado)
                            || (atributos.size() <= TAMANHO_MAXIMO_ARQUIVO
                            && conteudoContem(arquivo, termoNormalizado))) {
                        resultados.add(arquivo.toAbsolutePath().normalize());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path arquivo, IOException erro) {
                    informarFalha(arquivo, erro);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path diretorio, IOException erro) {
                    if (erro != null) {
                        informarFalha(diretorio, erro);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException | SecurityException erro) {
            throw new IllegalStateException("Falha ao percorrer a pasta " + raizAbsoluta, erro);
        }

        resultados.sort(Path::compareTo);
        return resultados;
    }

    private static boolean conteudoContem(Path arquivo, String termoNormalizado) {
        BodyContentHandler handler = new BodyContentHandler(LIMITE_TEXTO_EXTRAIDO);

        try (InputStream entrada = Files.newInputStream(arquivo)) {
            new AutoDetectParser().parse(entrada, handler, new Metadata());
            return handler.toString().toLowerCase(Locale.ROOT).contains(termoNormalizado);
        } catch (SAXException erro) {
            if (!WriteLimitReachedException.isWriteLimitReached(erro)) {
                informarFalha(arquivo, erro);
            }
            return handler.toString().toLowerCase(Locale.ROOT).contains(termoNormalizado);
        } catch (IOException | TikaException | SecurityException erro) {
            informarFalha(arquivo, erro);
            return handler.toString().toLowerCase(Locale.ROOT).contains(termoNormalizado);
        }
    }

    private static void informarFalha(Path arquivo, Exception erro) {
        String detalhe = erro.getMessage();
        System.err.println("Não foi possível ler " + arquivo
                + (detalhe == null || detalhe.isBlank() ? "." : ": " + detalhe));
    }

    public static void main(String[] args) {
        if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            imprimirUso();
            return;
        }
        if (args.length > 2) {
            imprimirUso();
            return;
        }
        String termo = args.length >= 1 ? args[0] : TERMO_PADRAO;
        try {
            Path raiz = args.length >= 2 ? Path.of(args[1]) : Path.of(".");
            List<Path> arquivos = buscarArquivos(termo, raiz);
            System.out.println("Resultados para \"" + termo + "\" em "
                    + raiz.toAbsolutePath().normalize() + ":");
            arquivos.forEach(System.out::println);
            System.out.println("\nTotal de arquivos encontrados: " + arquivos.size());
        } catch (IllegalArgumentException | IllegalStateException erro) {
            System.err.println("Erro: " + erro.getMessage());
            imprimirUso();
        }
    }

    private static void imprimirUso() {
        System.err.println("Uso: java -jar procurarArquivo-1.0-SNAPSHOT.jar [termo] [pasta]");
        System.err.println("Sem argumentos, procura por \"PSF\" na pasta atual.");
    }
}
