//package br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.core.destino.domain.Destino;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaCidadesResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaDestinosResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaRequest;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaResponse;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class TipoListaMapper {
//
//    public static TipoLista toEntity(TipoListaRequest dto) {
//        return new TipoLista(dto.nome(), dto.descricao());
//    }
//
//    public static TipoListaResponse toResponse(TipoLista entidade) {
//        Map<UUID, List<TipoListaDestinosResponseDto>> destinosPorCidade = new LinkedHashMap<>();
//        Map<UUID, String> nomeCidadePorId = new LinkedHashMap<>();
//
//        for (Destino d : entidade.getDestinosPermitidos()) {
//            var cidade = d.getCidade();
//            nomeCidadePorId.putIfAbsent(cidade.getId(), cidade.getNome());
//            destinosPorCidade
//                    .computeIfAbsent(cidade.getId(), id -> new ArrayList<>())
//                    .add(new TipoListaDestinosResponseDto(d.getId(), d.getNome()));
//        }
//
//        List<TipoListaCidadesResponseDto> cidades = destinosPorCidade.entrySet().stream()
//                .map(entry -> new TipoListaCidadesResponseDto(
//                        entry.getKey(),
//                        nomeCidadePorId.get(entry.getKey()),
//                        entry.getValue()))
//                .collect(Collectors.toList());
//
//        return new TipoListaResponse(
//                entidade.getNome(),
//                entidade.getDescricao(),
//                cidades
//        );
//    }
//}