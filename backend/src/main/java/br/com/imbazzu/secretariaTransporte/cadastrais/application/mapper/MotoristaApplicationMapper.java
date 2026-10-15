// package br.com.imbazzu.secretariaTransporte.cadastrais.application.mapper;

// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
// import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
// import
// br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.dto.VeiculoQueryDto;
// import
// br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
// import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Motorista;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.MotoristaApplicationPorta.MotoristaQueryDetalhadoInfoDto;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.MotoristaApplicationPorta.MotoristaCommandSalvarDto;

// import java.util.List;

// public class MotoristaApplicationMapper {

// public static Motorista paraDomain(MotoristaCommandSalvarDto dto) {
// return Motorista.criar(new Nome(dto.nome()), new Cpf(dto.cpf()), new
// Telefone(dto.telefone()),
// dto.idVeiculos());
// }

// public static MotoristaQueryDetalhadoInfoDto paraInfo(Motorista motorista,
// List<TipoVeiculo> listaVeiculos) {
// var lista = listaVeiculos.stream().map(
// veiculo -> new VeiculoQueryDto(veiculo.getId(),
// veiculo.getNome().valor())).toList();

// return new MotoristaQueryDetalhadoInfoDto(
// motorista.getId(),
// motorista.getNome().valor(),
// motorista.getTelefone().valor(),
// motorista.getCpf().valor(),
// lista);
// }

// public static void atualizar(Motorista motorista, MotoristaCommandSalvarDto
// dto) {
// motorista.editar(new Nome(dto.nome()), new Cpf(dto.cpf()), new
// Telefone(dto.telefone()),
// dto.idVeiculos());
// }

// }
