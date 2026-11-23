package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import java.time.LocalTime;

import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;

public record PassageiroVerificadoView(
        String nome,
        String telefone,
        String cidade,
        String destino,
        LocalTime horarioChegada,
        boolean buscarNaResidencia,
        int acompanhantes,
        int maxAcompanhantes) {

    /** Monta a view; destino pode ser null (não encontrado). */
    public static PassageiroVerificadoView de(
            PessoaResponseDto pessoa,
            DestinoView destino,
            LocalTime horarioChegada,
            boolean buscarNaResidencia,
            int acompanhantes) {
        return new PassageiroVerificadoView(
                pessoa.nome(),
                pessoa.telefone(),
                destino == null ? "Cidade não encontrada" : destino.cidade(),
                destino == null ? "Destino não encontrado" : destino.nome(),
                horarioChegada,
                buscarNaResidencia,
                acompanhantes,
                pessoa.prioridadeAcompanhantes());
    }
}
