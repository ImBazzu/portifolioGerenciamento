package br.com.imbazzu.secretariaTransporte.localidades.core.excecoes;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;

public class CidadeDuplicadoException extends EntidadeDuplicadaException {

    public CidadeDuplicadoException(CidadeEstadoEnum estado, Nome nome) {
        super("No estado %s existe outra cidade já cadastrada com %s".formatted(estado, nome));
    }

}
