//package br.com.imbazzu.secretariaTransporte.operacao.passageiro;
//
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.dto.PassageiroResponseDto;
//
//public class PassageiroMapper {
//
//
//    public static PassageiroResponseDto toPassageiroResponseDto(Passageiro passageiro) {
//        var paciente = passageiro.getPessoa();
//        var destino =  passageiro.getDestino();
//        return new PassageiroResponseDto(passageiro.getId(), paciente.getNome(), paciente.getTelefone(),
//                passageiro.getAcompanhantes(), passageiro.getHoraChegada(),
//                destino.getNome(), passageiro.getEndereco());
//    }
//
//}
