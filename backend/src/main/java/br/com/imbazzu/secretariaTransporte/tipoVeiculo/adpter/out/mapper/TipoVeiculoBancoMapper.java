package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.mapper;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;

public class TipoVeiculoBancoMapper {

    public static TipoVeiculoJpa paraJpa(TipoVeiculo veiculo) {
        return new TipoVeiculoJpa(
                veiculo.getId(),
                veiculo.getNome().toString(),
                veiculo.getCapacidade(),
                veiculo.getDataArquivado());
    }

    public static TipoVeiculo paraDomain(TipoVeiculoJpa jpa) {
        return TipoVeiculo.recriar(jpa.getId(),
                new NomeVeiculo(jpa.getNome()),
                jpa.getCapacidade(),
                jpa.getDataArquivado());
    }

}
