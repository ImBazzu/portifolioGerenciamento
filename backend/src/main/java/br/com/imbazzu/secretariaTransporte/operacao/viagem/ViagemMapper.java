//package br.com.imbazzu.secretariaTransporte.operacao.viagem;
//
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.PassageiroMapper;
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemResponseDto;
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.compartilhados.util.DataHoraUtil;
//
//import java.util.stream.Collectors;
//
//public class ViagemMapper {
//
//
//    public static ViagemResponseDto toResponseDto(Viagem viagem) {
//        var passageiros = viagem.getPassageiros().stream().map(PassageiroMapper::toPassageiroResponseDto).toList();
//
//        var destinos = viagem.getDestinos().stream().map(Cidade::getNome).collect(Collectors.joining("/"));
//        var hora = DataHoraUtil.horaParaTexto(viagem.getHora());
//        String motorista="";
//        String motoristaTelefone="";
//        if(viagem.getMotorista() != null) {
//            motorista = viagem.getMotorista().getNome();
//            motoristaTelefone = viagem.getMotorista().getTelefone();
//        }
//
//        return new ViagemResponseDto(viagem.getId(),passageiros, destinos,hora,motorista, motoristaTelefone);
//    }
//}
