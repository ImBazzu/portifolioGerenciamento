//package br.com.imbazzu.secretariaTransporte.operacao.viagem;
//
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
//import br.com.imbazzu.secretariaTransporte.frota.core.domain.motorista.Motorista;
//import br.com.imbazzu.secretariaTransporte.frota.adapters.out.motorista.command.MotoristaBancoCommandRepository;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.Passageiro;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.PassageiroRepository;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.RegraViagem;
//import br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao.RegraViagemRepository;
//import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
//import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemPorPeriodoResponseDto;
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemRequestDto;
//import br.com.imbazzu.secretariaTransporte.operacao.viagem.dto.ViagemResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ViagemService {
//
//    private final ViagemRepository repo;
//    private final MotoristaBancoCommandRepository motoristaBancoCommandRepository;
//    private final PassageiroRepository passageiroRepository;
//    private final RegraViagemRepository regraViagemRepository;
//
//    // -------------------------------------------------------------------------
//    // CRUD
//    // -------------------------------------------------------------------------
//
//    @Transactional
//    public ViagemResponseDto salvar(ViagemRequestDto dto) {
//        var viagem = new Viagem(dto.data(), dto.horaSaida());
//
//        dto.idsPassageiros().forEach(id -> viagem.adicionarPassageiro(buscarPassageiro(id)));
//
//        if (dto.idMotorista() != null) {
//            viagem.setMotorista(buscarMotorista(dto.idMotorista()));
//        }
//
//        return ViagemMapper.toResponseDto(repo.save(viagem));
//    }
//
//    @Transactional
//    public ViagemResponseDto editar(UUID id, ViagemRequestDto dto) {
//        var viagem = buscarEntidadePorId(id);
//
//        viagem.setHora(dto.horaSaida());
//        viagem.limparPassageiros();
//        dto.idsPassageiros().forEach(idPassageiro -> viagem.adicionarPassageiro(buscarPassageiro(idPassageiro)));
//        viagem.setMotorista(dto.idMotorista() != null ? buscarMotorista(dto.idMotorista()) : null);
//
//        return ViagemMapper.toResponseDto(viagem);
//    }
//
//    @Transactional(readOnly = true)
//    protected Viagem buscarEntidadePorId(UUID id) {
//        return repo.findById(id).orElseThrow(
//                () -> new EntidadeNaoEncontradoException("Viagem não encontrada")
//        );
//    }
//
//    @Transactional(readOnly = true)
//    public ViagemResponseDto buscarPorId(UUID id) {
//        return ViagemMapper.toResponseDto(buscarEntidadePorId(id));
//    }
//
//    @Transactional
//    public void deletarViagem(UUID id) {
//        repo.delete(buscarEntidadePorId(id));
//    }
//
//    // -------------------------------------------------------------------------
//    // Movimentação de passageiros
//    // -------------------------------------------------------------------------
//
//    @Transactional
//    public ViagemResponseDto moverPassageiro(UUID idViagemDestino, UUID idPassageiro) {
//        var passageiro = buscarPassageiro(idPassageiro);
//        var destino = buscarEntidadePorId(idViagemDestino);
//
//        var origem = passageiro.getViagem();
//        if (origem != null) {
//            origem.removerPassageiro(passageiro);
//        }
//
//        destino.adicionarPassageiro(passageiro);
//        return ViagemMapper.toResponseDto(destino);
//    }
//
//    @Transactional
//    public ViagemResponseDto removerPassageiro(UUID idViagem, UUID idPassageiro) {
//        var viagem = buscarEntidadePorId(idViagem);
//        var passageiro = buscarPassageiro(idPassageiro);
//        viagem.removerPassageiro(passageiro);
//        return ViagemMapper.toResponseDto(viagem);
//    }
//
//    // -------------------------------------------------------------------------
//    // Consultas por data / período
//    // -------------------------------------------------------------------------
//
//    @Transactional(readOnly = true)
//    public List<Viagem> buscarViagensDoDia(LocalDate data) {
//        return repo.findAllByData(data);
//    }
//
//    @Transactional(readOnly = true)
//    public ViagemPorPeriodoResponseDto buscarViagemSeparadoPorHorario(LocalDate dia) {
//        var viagens = buscarViagensDoDia(dia);
//        var antesDas5  = filtrarViagensPorHorario(viagens, LocalTime.MIDNIGHT,     LocalTime.of(5, 29, 59))
//                .stream().map(ViagemMapper::toResponseDto).toList();
//        var noturnas   = filtrarViagensPorHorario(viagens, LocalTime.of(19, 0, 1), LocalTime.of(23, 59, 59))
//                .stream().map(ViagemMapper::toResponseDto).toList();
//        var depoisDas5 = filtrarViagensPorHorario(viagens, LocalTime.of(5, 30),    LocalTime.of(19, 0))
//                .stream().map(ViagemMapper::toResponseDto).toList();
//
//        var antesMaisNoturnas = new ArrayList<>(antesDas5);
//        antesMaisNoturnas.addAll(noturnas);
//
//        return new ViagemPorPeriodoResponseDto(antesMaisNoturnas, depoisDas5);
//    }
//
//
//    public ResultadoPaginado<ViagemResponseDto>
//    buscarViagensPorMotorista(UUID idMotorista, LocalDate inicioProcura,
//                              LocalDate finalProcura, Pageable pagina){
//        var viagens = repo.findAllByMotorista(idMotorista,inicioProcura,finalProcura,pagina);
//
//        return PageMapper.toDomain(viagens,ViagemMapper::toResponseDto);
//    }
//
//    // -------------------------------------------------------------------------
//    // Geração automática de viagens
//    // -------------------------------------------------------------------------
//
//    /**
//     * Distribui passageiros em viagens de carro automaticamente, respeitando:
//     * <ul>
//     *   <li>Agrupamento por ConjuntoCidades (ou pela própria cidade se não houver conjunto)</li>
//     *   <li>Capacidade máxima configurada na RegraViagem</li>
//     *   <li>Tolerância de horário: diferença entre o passageiro âncora e o novo não pode ultrapassar o limite</li>
//     * </ul>
//     *
//     * @param data           data das viagens a serem criadas
//     * @param idsPassageiros ids dos passageiros a distribuir
//     * @return lista de viagens geradas
//     */
//    @Transactional
//    public ViagemPorPeriodoResponseDto separarViagens(LocalDate data, List<UUID> idsPassageiros) {
//        var regra = buscarRegra();
//
//        // 1. Buscar todos os passageiros e ordenar por hora de saída ideal (mais cedo primeiro)
//        var passageiros = idsPassageiros.stream()
//                .map(this::buscarPassageiro)
//                .sorted(Comparator.comparing(Passageiro::calcularHoraSaida))
//                .toList();
//
//        var viagens = new ArrayList<Viagem>();
//
//        // 2. Para cada passageiro, encontrar uma viagem compatível ou abrir uma nova
//        for (var passageiro : passageiros) {
//            var viagemCompativel = viagens.stream()
//                    .filter(v -> compativelComViagem(v, passageiro, regra))
//                    .findFirst();
//
//            if (viagemCompativel.isPresent()) {
//                viagemCompativel.get().adicionarPassageiro(passageiro);
//            } else {
//                var nova = new Viagem(data, passageiro.calcularHoraSaida());
//                nova.adicionarPassageiro(passageiro);
//                viagens.add(nova);
//            }
//        }
//
//        repo.saveAll(viagens);
//        return buscarViagemSeparadoPorHorario(data);
//    }
//
//    // -------------------------------------------------------------------------
//    // Lógica de compatibilidade
//    // -------------------------------------------------------------------------
//
//    /**
//     * Verifica se um passageiro pode entrar em uma viagem existente segundo três critérios:
//     * capacidade, tolerância de horário e compatibilidade de destino por conjuntos de cidades.
//     */
//    private boolean compativelComViagem(Viagem viagem, Passageiro passageiro, RegraViagem regra) {
//        // 1. Capacidade
//        if (viagem.lugaresOcupados() + passageiro.quantidadeTotalPassageiros() > regra.getCapacidadeMaxima()) {
//            return false;
//        }
//
//        // 2. Tolerância de horário — compara com a âncora (hora de saída da viagem, que é o primeiro passageiro)
//        if (!regra.horariosCompativeis(viagem.getHora(), passageiro.calcularHoraSaida())) {
//            return false;
//        }
//
//        // 3. Compatibilidade de destino
//        return destinosCompativeis(viagem, passageiro, regra);
//    }
//
//    /**
//     * Verifica se a cidade do novo passageiro é compatível com todas as cidades já presentes na viagem.
//     *
//     * <p>Regra: deve existir ao menos um ConjuntoCidades que contenha simultaneamente
//     * a cidade do novo passageiro e todas as cidades já na viagem.
//     *
//     * <p>Fallback: cidades sem nenhum conjunto só agrupam com a própria cidade.
//     */
//    private boolean destinosCompativeis(Viagem viagem, Passageiro passageiro, RegraViagem regra) {
//        var cidadeNova = passageiro.getDestino().getCidade();
//        var cidadesDaViagem = viagem.getPassageiros().stream()
//                .map(p -> p.getDestino().getCidade())
//                .collect(Collectors.toSet());
//
//        // Cidade sem conjunto: só agrupa com a própria cidade
//        boolean cidadeNovaTemConjunto = regra.getConjuntos().stream()
//                .anyMatch(c -> c.contemCidade(cidadeNova));
//
//        if (!cidadeNovaTemConjunto) {
//            return cidadesDaViagem.stream().allMatch(c -> c.equals(cidadeNova));
//        }
//
//        // Monta o conjunto total de cidades (existentes + nova)
//        var todasCidades = new HashSet<>(cidadesDaViagem);
//        todasCidades.add(cidadeNova);
//
//        // Deve existir ao menos um conjunto que contenha TODAS simultaneamente
//        return regra.getConjuntos().stream()
//                .anyMatch(c -> todasCidades.stream().allMatch(c::contemCidade));
//    }
//
//    // -------------------------------------------------------------------------
//    // Helpers privados
//    // -------------------------------------------------------------------------
//
//    private List<Viagem> filtrarViagensPorHorario(List<Viagem> viagens, LocalTime inicio, LocalTime fim) {
//        return viagens.stream()
//                .filter(v -> !v.getHora().isBefore(inicio) && !v.getHora().isAfter(fim))
//                .collect(Collectors.toList());
//    }
//
//    private RegraViagem buscarRegra() {
//        var regrasEncontradas =  regraViagemRepository.findAll();
//        if(regrasEncontradas.isEmpty()) {
//            throw new EntidadeNaoEncontradoException(
//                    "Regra de viagem não configurada. Inicialize pelo endpoint POST /regras/inicializar");
//        }else{
//            return regrasEncontradas.getFirst();
//        }
//    }
//
//    private Passageiro buscarPassageiro(UUID id) {
//        return passageiroRepository.findById(id).orElseThrow(
//                () -> new EntidadeNaoEncontradoException("Passageiro não encontrado")
//        );
//    }
//
//    private Motorista buscarMotorista(UUID id) {
//        return motoristaBancoCommandRepository.findById(id).orElseThrow(
//                () -> new EntidadeNaoEncontradoException("Motorista não encontrado")
//        );
//    }
//}