// package br.com.imbazzu.secretariaTransporte.cadastrais.application;

// import
// br.com.imbazzu.secretariaTransporte.cadastrais.application.mapper.MotoristaApplicationMapper;
// import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Motorista;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.MotoristaApplicationPorta;
// import
// br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.MotoristaRepositorioPorta;
// import
// br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
// import
// br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
// import
// br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
// import
// br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;

// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.UUID;

// @Transactional
// public class MotoristaApplication implements MotoristaApplicationPorta {

// private final MotoristaRepositorioPorta banco;
// private final TipoVeiculoCommandPorta veiculoRepositorio;

// public MotoristaApplication(MotoristaRepositorioPorta banco,
// TipoVeiculoCommandPorta veiculoRepositorio) {
// this.banco = banco;
// this.veiculoRepositorio = veiculoRepositorio;
// }

// // =================
// // Command
// // =================

// /**
// * Salva o novo motorista
// *
// * @param dto dados do novo motorista
// * @return identificador do motorista salvo
// */
// @Override
// public UUID salvar(MotoristaCommandSalvarDto dto) {
// // Verifica já existe algum motorista cadastrado com o CPF
// if (banco.verificarExistenciaCpf(dto.cpf())) {
// throw new EntidadeDuplicadaException(
// "Motorista já cadastrado.%nCPF: %s".formatted(dto.cpf()));
// }
// // Converte o dto em entidade
// var motorista = MotoristaApplicationMapper.paraDomain(dto);
// // Salva a entidade no banco
// return banco.salvar(motorista);
// }

// /**
// * Edita as informações do motorista
// *
// * @param id identificador do motorista
// * @param dto novos dados do motorista
// */
// @Override
// public void editar(UUID id, MotoristaCommandSalvarDto dto) {
// // Busca a entidade
// var motorista = buscarEntidade(id);
// // Realiza a atualização da entidade
// MotoristaApplicationMapper.atualizar(motorista, dto);
// // Verifica se o novo CPF informado pertence algum outro motorista cadastrado
// if (banco.verificarExistenciaCpfIgnorandoId(motorista.getCpf().valor(), id))
// {
// // Lança a exceção de motorista duplicado
// throw new EntidadeDuplicadaException(
// "Motorista duplicado.%nCPF: %s".formatted(motorista.getCpf().valor()));
// }
// // Salva a alteração no banco
// banco.salvar(motorista);
// }

// /**
// * Arquiva o motorista
// *
// * @param uuid identificador do motorista arquivado
// */
// @Override
// public void arquivar(UUID uuid) {
// // Busca o motorista
// var motorista = buscarEntidade(uuid);
// // Arquiva o motorista
// motorista.arquivar();
// // salva a alteração no banco
// banco.salvar(motorista);
// }

// /**
// * Desarquiva o motorista
// *
// * @param uuid identificador do motorista arquivado
// */
// @Override
// public void desarquivar(UUID uuid) {
// // Busca o motorista
// var motorista = buscarEntidade(uuid);
// // Desarquiva o motorista
// motorista.desarquivar();
// // Salva a alteração no banco
// banco.salvar(motorista);
// }

// // =================
// // Query
// // =================

// @Transactional(readOnly = true)
// @Override
// public MotoristaQueryDetalhadoInfoDto buscarPorId(UUID id) {
// // Busca o motorista
// var motorista = buscarEntidade(id);
// // Busca a lista dos veículos
// var listaVeiculoDto = veiculoRepositorio.buscarPorIdMotorista(id);
// // retorna o dto detalhado
// return MotoristaApplicationMapper.paraInfo(motorista, listaVeiculoDto);

// }

// /**
// * Busca uma lista paginada de motorista por nome
// *
// * @param nome nome procurado
// * @param pagina pagina
// * @param tamanho quantidade itens retornado por consulta
// * @return lista paginada dos motoristas correspondente
// */
// @Transactional(readOnly = true)
// @Override
// public ResultadoPaginado<MotoristaQueryDetalhadoInfoDto> buscarPorNome(String
// nome, int pagina, int tamanho) {
// // Busca paginada do motoristaDtoSimples
// var motoristas = banco.buscarPorNome(nome, pagina, tamanho);

// var idsMotorista =
// motoristas.conteudo().stream().map(Motorista::getId).toList();
// // Converte a busca motoristaDtoSimples para o motoristaDetalhado

// var listaVeiculosPorCidade =
// veiculoRepositorio.buscarVeiculosPorMotorista(idsMotorista);

// return motoristas.map(motorista -> {
// // Retorna o motorista detalhado
// return MotoristaApplicationMapper
// .paraInfo(motorista, listaVeiculosPorCidade.getOrDefault(motorista.getId(),
// List.of()));
// });
// }

// // =================
// // Métodos privados
// // =================

// /**
// * Busca a entidade motorista pelo o id
// *
// * @param id identificado do motorista
// * @return entidade motorista
// */
// protected Motorista buscarEntidade(UUID id) {
// // Busca a entidade motorista
// return banco.buscarPorId(id).orElseThrow(
// // Caso não encontre, lança a exceção
// () -> new EntidadeNaoEncontradoException(
// "Motorista não encontrado.%nID: %s".formatted(id)));
// }
// }
