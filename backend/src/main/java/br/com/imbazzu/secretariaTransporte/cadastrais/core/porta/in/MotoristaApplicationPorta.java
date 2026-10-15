// package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in;

// import
// br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;

// import java.util.List;
// import java.util.UUID;

// public interface MotoristaApplicationPorta {
// // =====================
// // Command
// // =====================

// UUID salvar(MotoristaCommandSalvarDto dto);

// void editar(UUID id, MotoristaCommandSalvarDto dto);

// void arquivar(UUID uuid);

// void desarquivar(UUID uuid);

// // =====================
// // Query
// // =====================

// MotoristaQueryDetalhadoInfoDto buscarPorId(UUID id);

// ResultadoPaginado<MotoristaQueryDetalhadoInfoDto> buscarPorNome(String nome,
// int pagina, int tamanho);

// // =====================
// // Record
// // =====================

// record MotoristaCommandSalvarDto(String nome, String cpf, String telefone,
// List<UUID> idVeiculos) {
// }

// // record MotoristaQueryDetalhadoInfoDto(
// // UUID id,
// // String nome,
// // String telefone,
// // String cpf,
// // List<VeiculoQueryDto> tipoVeiculo) {
// // }

// }
