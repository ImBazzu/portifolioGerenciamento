package br.com.imbazzu.secretariaTransporte.localidades.core.excecoes;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

public class CidadeDuplicadoException extends EntidadeDuplicadaException {

    public CidadeDuplicadoException(Nome nome) {
        super("Existe outra cidade já cadastrada com %s".formatted(nome));
    }

}
