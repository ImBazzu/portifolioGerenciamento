package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa.MotoristaJpa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Motorista;

public class MotoristaBancoMapper {

    public static MotoristaJpa paraMotoristaBanco(Motorista motorista) {

        return new MotoristaJpa(
                motorista.getId(),
                motorista.getNome().valor(),
                motorista.getCpf().valor(),
                motorista.getTelefone().valor(),
                null,
                motorista.isArquivado());
    }

    public static Motorista paraMotorista(MotoristaJpa motorista) {
        return Motorista.recriar(motorista.getId(),
                new Nome(motorista.getNome()),
                new Cpf(motorista.getCpf()),
                new Telefone(motorista.getTelefone()),
                null,
                motorista.isArquivado());
    }
}
