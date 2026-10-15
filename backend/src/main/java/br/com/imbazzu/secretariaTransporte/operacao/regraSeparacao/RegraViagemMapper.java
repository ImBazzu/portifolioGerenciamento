//package br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao;
//
//
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.ConjuntoResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.RegraViagemRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.RegraViagemResponseDto;
//import br.com.imbazzu.secretariaTransporte.compartilhados.util.DataHoraUtil;
//
//import java.time.Duration;
//import java.util.List;
//
//public class RegraViagemMapper {
//
//    private RegraViagemMapper() {}
//
//    public static RegraViagemResponseDto toResponse(RegraViagem regra) {
//        var tempoFormatado = DataHoraUtil.durationParaTexto(regra.getTempoTolerancia());
//        return new RegraViagemResponseDto(
//                regra.getCapacidadeMaxima(),
//                tempoFormatado,
//                regra.getConjuntos().stream().map(RegraViagemMapper::toConjuntoResponse).toList()
//        );
//    }
//
//    public static RegraViagem toEntity(RegraViagemRequestDto dto) {
//        var duracao = Duration.ofHours(dto.toleranciaHora()).plusMinutes(dto.toleranciaMinuto());
//        return new RegraViagem(dto.capacidadeMaxima(),duracao);
//    }
//
//    public static RegraViagem atualizar(RegraViagem entidade, RegraViagemRequestDto dto) {
//        var duracao = Duration.ofHours(dto.toleranciaHora()).plusMinutes(dto.toleranciaMinuto());
//        entidade.setCapacidadeMaxima(dto.capacidadeMaxima());
//        entidade.setTempoTolerancia(duracao);
//        return entidade;
//    }
//
//    public static ConjuntoResponseDto toConjuntoResponse(ConjuntoCidades conjunto) {
//        List<String> cidades = conjunto.getCidades().stream()
//                .map(Cidade::getNome)
//                .sorted()
//                .toList();
//        return new ConjuntoResponseDto(
//                conjunto.getId(),
//                conjunto.getNome(),
//                cidades
//        );
//    }
//}
