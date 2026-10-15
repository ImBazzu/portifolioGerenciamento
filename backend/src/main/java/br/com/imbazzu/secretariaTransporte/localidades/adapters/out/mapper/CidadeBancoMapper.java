package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.mapper;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa.CidadeJpa;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Cidade;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.valueobject.TempoViagem;

public class CidadeBancoMapper {

    public static Cidade paraDomainCidade(CidadeJpa cidade) {

        return Cidade.reconstruir(
                cidade.getId(),
                new Nome(cidade.getNome()),
                cidade.getEstado(),
                new TempoViagem(cidade.getTempoViagem()),
                cidade.getDataArquivado());
    }

    public static CidadeJpa paraEntityCidade(Cidade cidade) {

        return new CidadeJpa(
                cidade.getId(),
                cidade.getNome().valor(),
                cidade.getEstado(),
                cidade.getTempoViagem().emDuration(),
                cidade.getDataArquivado());
    }

}
