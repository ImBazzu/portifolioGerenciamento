package br.com.imbazzu.frontSpring.veiculo.core;

import java.util.Locale;

public final class TipoVeiculoUtil {

    private TipoVeiculoUtil() {
    }

    public static boolean ehCarro(String nome) {
        return nome != null && nome.strip().toLowerCase(Locale.ROOT).startsWith("carro");
    }
}
