// package
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper;

// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.MotoristaRequestDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.MotoristaResponseDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.MotoristaApplicationPorta.MotoristaCommandSalvarDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.MotoristaApplicationPorta.MotoristaQueryDetalhadoInfoDto;

// public class MotoristaControllerMapper {

// public static MotoristaResponseDto
// paraResponseDto(MotoristaQueryDetalhadoInfoDto dto) {
// return new MotoristaResponseDto(dto.id(),
// dto.nome(), dto.cpf(), dto.telefone(),
// null);
// }

// public static MotoristaCommandSalvarDto
// paraCommandSalvarDto(MotoristaRequestDto dto) {
// return new MotoristaCommandSalvarDto(
// dto.nome(), dto.cpf(), dto.telefone(), dto.idVeiculos());
// }
// }
