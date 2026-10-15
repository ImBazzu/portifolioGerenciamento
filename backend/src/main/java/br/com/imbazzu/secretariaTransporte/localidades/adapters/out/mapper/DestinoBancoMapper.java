package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.mapper;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.DestinoJpa;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Destino;

public class DestinoBancoMapper {

    public static DestinoJpa paraEntity(Destino destino) {

        return new DestinoJpa(
                destino.getId(),
                destino.getNome().valor(),
                destino.getIdCidade(),
                destino.getDataArquivado());
    }

    public static Destino paraDomain(DestinoJpa destino) {

        return Destino.reconstruir(
                destino.getId(),
                destino.getCidadeId(),
                new Nome(destino.getNome()),
                destino.getDataArquivado());
    }
}
