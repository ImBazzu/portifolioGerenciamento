package br.com.imbazzu.secretariaTransporte.lista.lista.tipoLista;
//package br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista;
//
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaRequest;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.dto.TipoListaResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class TipoListaService {
//
//    private final TipoListaRepository repo;
//
//    private final DestinoService destinoService;
//
//    @Transactional(readOnly = true)
//    public TipoLista buscarEstancia(UUID id) {
//        return repo.findById(id)
//                .orElseThrow(
//                        () -> new EntidadeNaoEncontradoException("Tipo lista não encontrada"));
//    }
//
//    @Transactional(readOnly = true)
//    public List<TipoListaResponse> buscarTodos() {
//        return repo.findAll().stream().map(TipoListaMapper::toResponse).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public TipoListaResponse buscarPorId(UUID id) {
//        var entidade = buscarEstancia(id);
//        return TipoListaMapper.toResponse(entidade);
//    }
//
//    @Transactional
//    public void deletar(UUID id) {
//        var entidade = buscarEstancia(id);
//        if(!entidade.getListasDoDia().isEmpty()){
//            throw new OperacaoNaoPermitidaException("Existem registros vinculos");
//        }
//        try {
//            repo.delete(entidade);
//            repo.flush();
//        } catch (DataIntegrityViolationException e) {
//            throw new EntidadeNaoEncontradoException("Não foi possível remover o tipo de lista: existem registros vinculados");
//        }
//    }
//
//    @Transactional
//    public TipoListaResponse editar(UUID id, TipoListaRequest dto) {
//        var entidade = buscarEstancia(id);
//
//        if (repo.existsByNomeAndIdNot(dto.nome().toUpperCase(), id)) {
//            throw new EntidadeDuplicadaException("Já existe um tipo de lista com esse nome");
//        }
//
//        entidade.atualizar(dto.nome(), dto.descricao());
//
//        try {
//            var entidadeSalva = repo.saveAndFlush(entidade);
//            return TipoListaMapper.toResponse(entidadeSalva);
//        } catch (DataIntegrityViolationException e) {
//            throw new EntidadeDuplicadaException("Não foi possível editar o tipo de lista: dados inválidos ou duplicados");
//        }
//    }
//
//    @Transactional
//    public void editarAcesso(UUID id, boolean ativo) {
//        var entidade = buscarEstancia(id);
//        entidade.setAtivo(ativo);
//        try {
//            repo.saveAndFlush(entidade);
//        }catch (DataIntegrityViolationException e) {
//            throw new BancoDeDadosException("Erro ao realizar edição no banco de dados");
//        }
//    }
//
//    @Transactional
//    public TipoListaResponse salvar(TipoListaRequest dto) {
//        if (repo.existsByNome(dto.nome().toUpperCase())) {
//            throw new EntidadeDuplicadaException("Já existe um tipo de lista com esse nome");
//        }
//
//        try {
//            var entidade = TipoListaMapper.toEntity(dto);
//            var entidadeSalva = repo.saveAndFlush(entidade);
//            return TipoListaMapper.toResponse(entidadeSalva);
//        } catch (DataIntegrityViolationException e) {
//            throw new EntidadeDuplicadaException("Não foi possível salvar o tipo de lista: dados inválidos ou duplicados");
//        }
//    }
//
//    @Transactional
//    public TipoListaResponse adicionarDestino(UUID tipoListaId, UUID destinoId) {
//        var entidade = buscarEstancia(tipoListaId);
//        var destino =destinoService.buscarEntidadePorId(destinoId);
//        entidade.addDestino(destino);
//
//        try {
//            var entidadeSalva = repo.saveAndFlush(entidade);
//            return TipoListaMapper.toResponse(entidadeSalva);
//        } catch (DataIntegrityViolationException e) {
//            throw new EntidadeDuplicadaException("Não foi possível vincular o destino: dados inválidos ou duplicados");
//        }
//    }
//
//    @Transactional
//    public TipoListaResponse removerDestino(UUID tipoListaId, UUID destinoId) {
//        var entidade = buscarEstancia(tipoListaId);
//        var destino =destinoService.buscarEntidadePorId(destinoId);
//
//        entidade.removeDestino(destino);
//
//        try {
//            var entidadeSalva = repo.saveAndFlush(entidade);
//            return TipoListaMapper.toResponse(entidadeSalva);
//        } catch (DataIntegrityViolationException e) {
//            throw new EntidadeDuplicadaException("Não foi possível desvincular o destino: dados inválidos");
//        }
//    }
//}