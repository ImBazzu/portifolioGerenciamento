package br.com.imbazzu.secretariaTransporte.lista.application;
//package br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.trajeto.adapters.out.banco.cidade.CidadeRepository;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.ConjuntoRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.ConjuntoResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.RegraViagemRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.dto.RegraViagemResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class RegraViagemService {
//
//    private final RegraViagemRepository regraRepo;
//    private final ConjuntoCidadesRepository conjuntoRepo;
//    private final CidadeRepository cidadeRepo;
//
//    // -------------------------------------------------------------------------
//    // RegraViagem — singleton
//    // -------------------------------------------------------------------------
//
//    /**
//     * Atualiza capacidade máxima e tolerância de horário.
//     */
//    @Transactional
//    public RegraViagemResponseDto atualizar(RegraViagemRequestDto dto) {
//        var regra = buscarEntidade();
//        RegraViagemMapper.atualizar(regra, dto);
//        regraRepo.saveAndFlush(regra);
//        return RegraViagemMapper.toResponse(regra);
//    }
//
//    /**
//     * Retorna a entidade gerenciada — usada internamente e pelo ViagemService.
//     */
//    @Transactional(readOnly = true)
//    public RegraViagem buscarEntidade() {
//        var regrasEncontradas =  regraRepo.findAll();
//
//        if(regrasEncontradas.isEmpty()){
//            throw new EntidadeNaoEncontradoException(
//                    "Regra de viagem não configurada. Inicialize pelo endpoint POST /regras/inicializar");
//
//        }else{
//            return  regrasEncontradas.getFirst();
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public RegraViagemResponseDto buscarPorId() {
//        return RegraViagemMapper.toResponse(buscarEntidade());
//    }
//
//    /**
//     * Inicializa a regra com valores padrão caso ainda não exista.
//     * Deve ser chamado uma única vez na configuração inicial do sistema.
//     */
//    @Transactional
//    public RegraViagemResponseDto inicializar(RegraViagemRequestDto dto) {
//        try{
//            buscarEntidade();
//            throw new DadosInvalidosException("Regra de viagem já foi inicializada. Use PUT /regras para editar.");
//        }catch (EntidadeNaoEncontradoException e){
//            var regra = RegraViagemMapper.toEntity(dto);
//            regraRepo.save(regra);
//            return RegraViagemMapper.toResponse(regra);
//        }
//
//    }
//
//    // -------------------------------------------------------------------------
//    // ConjuntoCidades
//    // -------------------------------------------------------------------------
//
//    /**
//     * Adiciona um novo conjunto de cidades à regra.
//     */
//    @Transactional
//    public RegraViagemResponseDto adicionarConjunto(
//            ConjuntoRequestDto dto) {
//
//        var regra = buscarEntidade();
//        var cidades = resolverCidades(dto.idsCidades());
//        var conjunto = regra.adicionarConjunto(dto.nome(), cidades);
//        regra = regraRepo.saveAndFlush(regra);
//        return RegraViagemMapper.toResponse(regra);
//    }
//
//    /**
//     * Remove um conjunto de cidades.
//     */
//    @Transactional
//    public void removerConjunto(UUID idConjunto) {
//        var regra = buscarEntidade();
//        var conjunto = buscarConjunto(idConjunto);
//        regra.removerConjunto(conjunto);
//        regraRepo.saveAndFlush(regra);
//    }
//
//    /**
//     * Adiciona uma cidade a um conjunto existente.
//     */
//    @Transactional
//    public ConjuntoResponseDto adicionarCidade(
//            UUID idConjunto, Set<UUID> idsCidades) {
//
//        var conjunto = buscarConjunto(idConjunto);
//        idsCidades.forEach(idCidade -> {
//            var cidade = buscarCidade(idCidade);
//            conjunto.adicionarCidade(cidade);
//        });
//        conjuntoRepo.saveAndFlush(conjunto);
//        return RegraViagemMapper.toConjuntoResponse(conjunto);
//    }
//
//    /**
//     * Remove uma cidade de um conjunto existente.
//     */
//    @Transactional
//    public ConjuntoResponseDto removerCidade(
//            UUID idConjunto, UUID idCidade) {
//
//        var conjunto = buscarConjunto(idConjunto);
//        var cidade = buscarCidade(idCidade);
//        conjunto.removerCidade(cidade);
//        conjuntoRepo.saveAndFlush(conjunto);
//        return RegraViagemMapper.toConjuntoResponse(conjunto);
//    }
//
//    // -------------------------------------------------------------------------
//    // Helpers privados
//    // -------------------------------------------------------------------------
//
//    private ConjuntoCidades buscarConjunto(UUID id) {
//        return conjuntoRepo.findById(id).orElseThrow(
//                () -> new EntidadeNaoEncontradoException("Conjunto de cidades não encontrado")
//        );
//    }
//
//    public List<ConjuntoResponseDto> buscarConjuntosPorNome(String nome) {
//        return conjuntoRepo.findAllByNome(nome).stream().map(RegraViagemMapper::toConjuntoResponse).toList();
//    }
//
//    private Cidade buscarCidade(UUID id) {
//        return cidadeRepo.findById(id).orElseThrow(
//                () -> new EntidadeNaoEncontradoException("Cidade não encontrada")
//        );
//    }
//
//    private Set<Cidade> resolverCidades(Set<UUID> ids) {
//        var cidades = cidadeRepo.findAllById(ids);
//        if (cidades.size() != ids.size()) {
//            throw new EntidadeNaoEncontradoException("Uma ou mais cidades não encontradas");
//        }
//        return new HashSet<>(cidades);
//    }
//}
