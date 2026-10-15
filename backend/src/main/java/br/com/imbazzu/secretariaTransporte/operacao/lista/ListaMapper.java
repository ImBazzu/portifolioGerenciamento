//package br.com.imbazzu.secretariaTransporte.operacao.lista;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.dto.ListaRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.dto.ListaResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.PassageiroMapper;
//
//public class ListaMapper {
//
//    public static ListaDoDia toEntity(ListaRequestDto dto){
//        return new ListaDoDia(dto.titulo(), dto.descricao(), dto.Data(), dto.tipo());
//    }
//
//    public static ListaResponseDto toResponse(ListaDoDia listaDoDia){
//        var listaCidades = listaDoDia.getCidades().stream().map(Cidade::getNome).toList();
//        var listaPassageiros = listaDoDia.getPassageiros().stream().map(PassageiroMapper::toPassageiroResponseDto).toList();
//        return new ListaResponseDto(listaDoDia.getId(), listaDoDia.getTitulo(),
//                listaDoDia.getDescricao(), listaDoDia.getData(),
//                listaDoDia.getListaTipo().name(),listaCidades,listaPassageiros);
//    }
//
//    public static ListaDoDia editar(ListaDoDia antiga, ListaRequestDto nova){
//        antiga.setTitulo(nova.titulo());
//        antiga.setDescricao(nova.descricao());
//        antiga.setListaTipo(nova.tipo());
//        return antiga;
//    }
//}
