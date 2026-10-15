//package br.com.imbazzu.secretariaTransporte.operacao.lista;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.core.destino.domain.Destino;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeEmUsoException;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.dto.ListaRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.dto.ListaResponseDto;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.Passageiro;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.PassageiroMapper;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.PassageiroRepository;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.dto.PassageiroRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.dto.PassageiroResponseDto;
//import br.com.imbazzu.secretariaTransporte.pessoa.core.pessoa.domain.Pessoa;
//import br.com.imbazzu.secretariaTransporte.pessoa.adapters.out.banco.pessoa.PessoaRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class ListaService {
//
//  private final ListaRepository repo;
//
//  //A classe service lista tem acesso aos repositórios, pois é proibido que um
//  // service tenha acesso a outro service
//
//    /**
//     * Repositório passageiro
//     */
//  private final PassageiroRepository passageiroRepo;
//
//    /**
//     * Repositório pessoa
//     */
//  private final PessoaRepository pessoaBancoRepository;
//
//    /**
//     * Repositório destino
//     */
////  private final DestinoRepository destinoRepository;
//
//    /**
//     * Adiciona passageiro na lista
//     * @param idLista identificador para selecionar a lista
//     * @param dto informações do passageiro
//     * @return Passageiro cadastrado
//     */
//  @Transactional()
//  public ListaResponseDto adicionarPassageiro(UUID idLista,
//                                        PassageiroRequestDto dto) {
//      //Busca a lista
//      var lista = buscarEntidade(idLista);
//
//      //Busca a pessoa e o destino que irá viajar
//      var pessoa = buscarPessoa(dto.idPessoa());
//      var destino = buscarDestino(dto.idDestino());
//
//      //Cria e cadastrada na lista o passageiro
//      lista.adicionarPassageiro(pessoa,destino,
//            dto.acompanhante(),dto.horaChegada(),dto.buscar());
//      try{
//
//          lista = repo.saveAndFlush(lista);
//
//          //Retorna a lista atualizada
//          return ListaMapper.toResponse(lista);
//      }catch (DataIntegrityViolationException e){
//          throw new BancoDeDadosException(e.getMessage());
//      }
//  }
//
//  @Transactional(readOnly = true)
//  public ListaResponseDto buscarPorId(UUID idLista){
//      return ListaMapper.toResponse(buscarEntidade(idLista));
//  }
//
//    /**
//     * Busca a entidade da lista
//     * @param id seleciona a lista
//     * @return retorna informações da lista
//     */
//  @Transactional(readOnly = true)
//  protected ListaDoDia buscarEntidade(UUID id){
//      //Busca a lista
//      return repo.findById(id).orElseThrow(
//              //Caso não encontrado, retorna erro
//              ()->new EntidadeNaoEncontradoException("ListaDoDia nao encontrada")
//      );
//  }
//
//    /**
//     * Busca o passageiro
//     * @param idPassageiro identificador do passageiro
//     * @return passageiro
//     */
//  @Transactional(readOnly = true)
//  protected Passageiro buscarPassageiro(UUID idPassageiro){
//      //Busca o passageiro
//      return passageiroRepo.findById(idPassageiro).orElseThrow(
//              //Caso não encontrado retorna o erro
//              ()->new EntidadeNaoEncontradoException("Passageiro não encontrado")
//      );
//  }
//
//    /**
//     * ListaDoDia todos os passageiros da lista
//     * @param idLista seleciona a lista
//     * @return lista de passageiros
//     */
//    @Transactional(readOnly = true)
//    public List<PassageiroResponseDto> buscarTodosPassageiros(UUID idLista) {
//        //Busca a lista
//        var lista = buscarEntidade(idLista);
//        //Retorna todos os passageiros
//        return lista.getPassageiros().stream().map(PassageiroMapper::toPassageiroResponseDto).toList();
//    }
//
//    /**
//     * ListaDoDia todos os passageiros da lista
//     * @param idLista seleciona a lista
//     * @return lista de passageiros
//     */
//    @Transactional(readOnly = true)
//    public List<PassageiroResponseDto> buscarTodosPassageirosSemCarro(UUID idLista) {
//        //Busca a lista
//        var lista = buscarEntidade(idLista);
//
//        //Verifica se a lista é do tipo carro
//        if(lista.getListaTipo()!=ListaTipoEnum.CARRO){
//            //Retorna erro caso não seja
//            throw new DadosInvalidosException("Deve-se selecionar a lista do tipo Carro");
//        }
//
//        //Busca todos os passageiros
//        var passageiros = lista.getPassageiros().stream()
//                //Filtra todos os passageiros sem carro
//                .filter(passageiro -> passageiro.getViagem()==null).map(PassageiroMapper::toPassageiroResponseDto)
//                .toList();
//
//        //Verifica se existem passageiros na lista
//        if(passageiros.isEmpty()){
//            //caso a lista seja vazio, retorna erro
//            throw new EntidadeNaoEncontradoException("ListaDoDia vazia");
//        }
//
//        //Retorna todos os passageiros sem carro definido
//        return  passageiros;
//    }
//
//    /**
//     * Busca a pessoa no banco de dados
//     * @param id identificador da pessoa
//     * @return pessoa
//     */
//  @Transactional(readOnly = true)
//  protected Pessoa buscarPessoa(UUID id){
//      //Busca pessoa no repositório
//      return  pessoaBancoRepository.findById(id).orElseThrow(
//              //Caso não seja encontrado, retorna erro
//              ()-> new EntidadeNaoEncontradoException("Pessoa nao encontrada")
//      );
//  }
//
//    /**
//     * Busca o destino cadastrado no banco de dados
//     * @param id identificador do destino
//     * @return destino
//     */
//  @Transactional(readOnly = true)
//  protected Destino buscarDestino(UUID id){
//      //Busca o destino
//      return  destinoRepository.findById(id).orElseThrow(
//              //Caso não seja encontrado, retorna erro
//              ()->new EntidadeNaoEncontradoException("Destino nao encontrada")
//      );
//  }
//
//
//    /**
//     * Busca todas as lista da data passada
//     * @param data data procurada
//     * @return listas encontradas
//     */
//  @Transactional(readOnly = true)
//  public List<ListaResponseDto> buscarListaPorData(LocalDate data){
//      //Todas as listas encontradas
//      var listas =  repo.findAllByData(data);
//      //Verifica se alguma lista foi encontrada
//      if(listas.isEmpty()){
//          //Erro caso a lista esteja vazia
//          throw new EntidadeNaoEncontradoException("Nenhuma lista encontrada na data procurada");
//      }
//      //Retorna as listas encontrada
//      return  listas.stream().map(ListaMapper::toResponse).toList();
//  }
//
//    /**
//     * Deleta a lista selecionada
//     * @param id identificador da lista
//     */
//  @Transactional
//  public void deletarLista(UUID id) {
//      //Procura a lista
//      var lista = buscarEntidade(id);
//
//      var existePassageiroEmViagem=lista.getPassageiros().stream().anyMatch(
//              passageiro -> passageiro.getViagem()!=null
//      );
//      if(existePassageiroEmViagem){
//          throw new EntidadeEmUsoException("Não é possível excluir uma lista com registros vinculados.");
//      }
//
//          repo.delete(lista);
//  }
//
//    /**
//     * Deleta o passageiro da lista
//     * @param idLista seleciona a lista
//     * @param idPassageiro seleciona o passageiro cadastrado
//     */
//  @Transactional
//  public void deletarPassageiro(UUID idLista, UUID idPassageiro) {
//      //Busca a lista
//      var lista = buscarEntidade(idLista);
//      //Busca o passageiro cadastrado
//      var passageiro = buscarPassageiro(idPassageiro);
//      //Remove o passageiro da lista
//      lista.removerPassageiro(passageiro);
//      //salva a lista atualizada
//      repo.saveAndFlush(lista);
//  }
//
//    /**
//     * Edita a lista
//     * @param id identificador da lista
//     * @param dto novos dados
//     * @return retorna a lista editada
//     */
//  @Transactional
//  public ListaResponseDto editar(UUID id, ListaRequestDto dto){
//      //Seleciona a lista
//      var lista = buscarEntidade(id);
//      //Edita a lista
//      ListaMapper.editar(lista, dto);
//      //Salva e atualiza as informações no banco de dados
//      repo.saveAndFlush(lista);
//      //Retorna a lista atualizada
//      return ListaMapper.toResponse(lista);
//  }
//
//    /**
//     * Move passageiro de lista
//     * @param idPassageiro passageiro a ser movido
//     * @param idListaDestino nova lista
//     * @return lista destino atualizada
//     */
//  @Transactional
//  public ListaResponseDto moverPassageiro(UUID idPassageiro, UUID idListaDestino){
//      //Busca o passageiro
//      var passageiro = buscarPassageiro(idPassageiro);
//      //Busca a lista antiga do passageiro
//      var listaOrigem = passageiro.getListaDoDia();
//      //Busca nova lista do passageiro
//      var listaDestino = buscarEntidade(idListaDestino);
//      //Somente pode ser movido se a lista for do mesmo dia
//      if(!listaOrigem.getData().equals(listaDestino.getData())){
//          throw new DadosInvalidosException("A nova lista deve ser da mesma data da antiga para mover");
//      }
//      //Remove o passageiro da antiga lista
//      listaOrigem.removerPassageiro(passageiro);
//      //Adiciona na nova lista
//      listaDestino.adicionarPassageiro(passageiro);
//      //Caso o passageiro estivesse num carro, e a nova lista não seja do tipo carro
//      //remove o passageiro do carro
//      if(listaOrigem.getListaTipo()==ListaTipoEnum.CARRO){
//          if(!(listaDestino.getListaTipo()==ListaTipoEnum.CARRO)){
//             passageiro.setViagem(null);
//          }
//      }
//      //Salva as listas atualizada
//      repo.save(listaOrigem);
//      repo.save(listaDestino);
//      //Retorna a lista
//      return  ListaMapper.toResponse(listaDestino);
//  }
//
//    /**
//     * Salva a nova lista no banco
//     * @param dto dados da nova lista
//     * @return lista cadastrada
//     */
//  @Transactional
//  public ListaResponseDto salvar(ListaRequestDto dto) {
//      //Converte o dto em entidade
//      var lista = ListaMapper.toEntity(dto);
//      //Salva a lista no banco de dados
//      var listaSalva = repo.save(lista);
//      //Transforma a lista salva em dto e retorna
//      return ListaMapper.toResponse(listaSalva);
//
//  }
//
//  @Transactional(readOnly = true)
//  public ListaResponseDto buscarPorNome(String titulo) {
//      var lista = repo.findByTitulo(titulo.toUpperCase()).orElseThrow(
//              ()->new EntidadeNaoEncontradoException("ListaDoDia não encontrada")
//      );
//
//      return  ListaMapper.toResponse(lista);
//  }
//
//}
