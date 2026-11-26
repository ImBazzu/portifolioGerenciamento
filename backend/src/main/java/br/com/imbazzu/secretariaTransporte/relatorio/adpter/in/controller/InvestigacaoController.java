package br.com.imbazzu.secretariaTransporte.relatorio.adpter.in.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.relatorio.adpter.out.InvestigacaoRepository;

@RestController
@RequestMapping("/relatorios/investigacao")
@Transactional(readOnly = true)
public class InvestigacaoController {
    private static final int TAMANHO = 20;
    private final InvestigacaoRepository repositorio;

    public InvestigacaoController(InvestigacaoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public record Pagina<T>(List<T> content, long totalElements, int totalPages, int number) {
        static <A, B> Pagina<B> de(Page<A> pagina, Function<A, B> converter) {
            return new Pagina<>(pagina.getContent().stream().map(converter).toList(),
                    pagina.getTotalElements(), pagina.getTotalPages(), pagina.getNumber());
        }
    }
    public record Pessoa(UUID id, String nome, String cpf, String telefone, String especificacao, LocalDate arquivadaEm) {}
    public record Agendamento(UUID id, LocalDate data, LocalTime chegada, String cidade, String destino,
            String lista, String tipoVeiculo, String identificacao, String motorista, UUID veiculoId,
            int acompanhantes, boolean buscarNaResidencia, String autor) {}
    public record PessoaDetalhe(Pessoa pessoa, Pagina<Agendamento> agendamentos) {}
    public record Motorista(UUID id, String nome, String categoria, long veiculos, long passageiros) {}
    public record Resumo(long motoristas, long veiculos, long passageiros) {}
    public record Passageiro(String nome, String cidade, String destino, LocalTime chegada, int acompanhantes) {}
    public record Veiculo(UUID id, LocalDate data, String motorista, String categoria, String tipo,
            String identificacao, LocalTime saida, boolean saidaCalculada, List<Passageiro> passageiros) {}
    public record Motoristas(Resumo resumo, Pagina<Motorista> porMotorista, Pagina<Veiculo> veiculos) {}

    @GetMapping("/pessoas")
    public ResponseEntity<Pagina<Pessoa>> pessoas(@RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int pagina) {
        var resultado = repositorio.buscarPessoas(nome.strip(), pagina(pagina));
        return semCache(Pagina.de(resultado, InvestigacaoController::pessoa));
    }

    @GetMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDetalhe> pessoa(@PathVariable UUID id,
            @RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fim,
            @RequestParam(defaultValue = "") String cidade, @RequestParam(defaultValue = "") String destino,
            @RequestParam(defaultValue = "TODOS") String situacao, @RequestParam(defaultValue = "0") int pagina) {
        validar(inicio, fim);
        if (!List.of("TODOS", "ALOCADO", "PENDENTE").contains(situacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Situação inválida");
        }
        var pessoa = repositorio.buscarPessoa(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada"));
        var agendamentos = repositorio.historico(id, inicio, fim, cidade.strip(), destino.strip(), situacao, pagina(pagina));
        return semCache(new PessoaDetalhe(pessoa(pessoa), Pagina.de(agendamentos, a ->
                new Agendamento(a.getId(), a.getData(), a.getChegada(), a.getCidade(), a.getDestino(),
                        a.getLista(), a.getTipoVeiculo(), a.getIdentificacao(), a.getMotorista(), a.getVeiculoId(),
                        a.getAcompanhantes(), a.getBuscarNaResidencia(), a.getAutor()))));
    }

    @GetMapping("/motoristas")
    public ResponseEntity<Motoristas> motoristas(@RequestParam(defaultValue = "") String nome,
            @RequestParam(required = false) UUID motorista,
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fim,
            @RequestParam(defaultValue = "") String cidade, @RequestParam(defaultValue = "") String destino,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "0") int resumoPagina) {
        validar(inicio, fim);
        nome = nome.strip();
        categoria = categoria.strip();
        cidade = cidade.strip();
        destino = destino.strip();
        var resumo = repositorio.resumoGeral(nome, motorista, categoria, inicio, fim, cidade, destino);
        var motoristas = repositorio.resumoMotoristas(nome, motorista, categoria, inicio, fim, cidade, destino, pagina(resumoPagina));
        var veiculos = repositorio.veiculos(nome, motorista, categoria, inicio, fim, cidade, destino, pagina(pagina));
        var ids = veiculos.getContent().stream().map(InvestigacaoRepository.Veiculo::getId).toList();
        Map<UUID, List<InvestigacaoRepository.Passageiro>> passageiros = ids.isEmpty() ? Map.of()
                : repositorio.passageiros(ids).stream().collect(Collectors.groupingBy(InvestigacaoRepository.Passageiro::getVeiculoId));
        return semCache(new Motoristas(new Resumo(resumo.getMotoristas(), resumo.getVeiculos(), resumo.getPassageiros()),
                Pagina.de(motoristas, m -> new Motorista(m.getId(), m.getNome(), m.getCategoria(), m.getVeiculos(), m.getPassageiros())),
                Pagina.de(veiculos, v -> {
                    var ocupantes = passageiros.getOrDefault(v.getId(), List.of());
                    var saida = v.getSaidaCalculada()
                            ? ocupantes.stream().map(p -> CompatibilidadeVeiculoAgendamento.horarioSaidaNecessario(
                                    p.getChegada(), Duration.ofSeconds(p.getSegundos(), p.getNanos())))
                                    .min(LocalTime::compareTo).orElse(null)
                            : v.getSaida();
                    return new Veiculo(v.getId(), v.getData(), v.getMotorista(), v.getCategoria(), v.getTipo(),
                            v.getIdentificacao(), saida, v.getSaidaCalculada(), ocupantes.stream()
                                    .map(p -> new Passageiro(p.getNome(), p.getCidade(), p.getDestino(),
                                            p.getChegada(), p.getAcompanhantes())).toList());
                })));
    }

    private static Pessoa pessoa(InvestigacaoRepository.Pessoa p) {
        return new Pessoa(p.getId(), p.getNome(), p.getCpf(), p.getTelefone(), p.getEspecificacao(), p.getArquivadaEm());
    }

    private static PageRequest pagina(int pagina) {
        if (pagina < 0 || pagina > 100000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Página inválida");
        return PageRequest.of(pagina, TAMANHO);
    }

    private static void validar(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inicial posterior à data final");
        }
    }

    private static <T> ResponseEntity<T> semCache(T corpo) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(corpo);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> erroDeConsulta(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).cacheControl(CacheControl.noStore())
                .body(Map.of("mensagem", erro.getReason()));
    }
}
