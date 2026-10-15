package br.com.imbazzu.secretariaTransporte.localidades.core.excecoes;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

public class DestinoDuplicadoException extends EntidadeDuplicadaException {

    public DestinoDuplicadoException(Nome nome) {
        super("Existe outro destino já cadastrado com %s".formatted(nome));

    }

}
