package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendaDiaResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.ResultadoSeparacaoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoPermitidoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.PageResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirDia.AgendamentoCommandAbrirDia;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirDia.AgendamentoCommandAbrirDiaHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.model.ResultadoSeparacaoAutomatica;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover.AgendamentoCommandRemover;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover.AgendamentoCommandRemoverHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente.AgendamentoCommandSepararAutomaticamente;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente.AgendamentoCommandSepararAutomaticamenteHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscar.AgendamentoQueryBuscar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscar.AgendamentoQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscarDia.AgendamentoQueryBuscarDia;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscarDia.AgendamentoQueryBuscarDiaHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.listarPendentes.AgendamentoQueryListarPendentes;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.listarPendentes.AgendamentoQueryListarPendentesHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.AgendamentoAgenda;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.AgendaDia;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.VeiculoAgenda;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.VeiculoPermitido;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos.AgendamentoQueryVerificarVeiculos;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.verificarVeiculos.AgendamentoQueryVerificarVeiculosHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.AgendamentoQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.HistoricoViagemProjecao;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoCommandAbrirDiaHandler abrirDiaHandler;
    private final AgendamentoQueryVerificarVeiculosHandler verificarVeiculosHandler;
    private final AgendamentoCommandCriarHandler criarHandler;
    private final AgendamentoQueryBuscarDiaHandler buscarDiaHandler;
    private final AgendamentoQueryListarPendentesHandler listarPendentesHandler;
    private final AgendamentoCommandSepararAutomaticamenteHandler separarAutomaticamenteHandler;
    private final AgendamentoQueryBuscarHandler buscarHandler;

    private final AgendamentoCommandRemoverHandler removerHandler;
    private final AgendamentoQueryRepository queryRepository;

    @PostMapping("/dias/{data}/abrir")
    public ResponseEntity<Void> abrirDia(@PathVariable LocalDate data) {
        abrirDiaHandler.handler(new AgendamentoCommandAbrirDia(data));
        return ResponseEntity.created(URI.create("/api/agendamentos/dias/%s".formatted(data))).build();
    }   

    @PostMapping("/dias/{data}/verificar-veiculos")
    public ResponseEntity<VerificacaoVeiculosResponseDto> verificarVeiculos(
            @PathVariable LocalDate data,
            @RequestBody @Valid VerificarVeiculosRequestDto request) {
        var resultado = verificarVeiculosHandler.handler(new AgendamentoQueryVerificarVeiculos(
                data,
                request.idPessoa(),
                request.idLista(),
                request.idDestino(),
                request.horarioChegada(),
                request.acompanhantes()));
        var permitidos = resultado.veiculosPermitidos().stream()
                .map(AgendamentoController::toResponse)
                .toList();
        return ResponseEntity.ok(new VerificacaoVeiculosResponseDto(permitidos, resultado.motivosReprovacao()));
    }

    @PostMapping("/dias/{data}/passageiros")
    public ResponseEntity<Void> criar(
            @PathVariable LocalDate data,
            @RequestBody @Valid AgendamentoRequestDto request,
            @AuthenticationPrincipal UUID autorId) {
        var id = criarHandler.handler(new AgendamentoCommandCriar(
                data,
                request.idPessoa(),
                request.idLista(),
                null,
                request.idTipoVeiculo(),
                request.idDestino(),
                request.horarioChegada(),
                request.buscarNaResidencia(),
                request.acompanhantes(),
                request.idVeiculo(),
                autorId));
        return ResponseEntity.created(URI.create("/api/agendamentos/%s".formatted(id))).build();
    }


    @GetMapping("/dias/{data}")
    public ResponseEntity<AgendaDiaResponseDto> buscarDia(@PathVariable LocalDate data) {
        var agenda = buscarDiaHandler.handler(new AgendamentoQueryBuscarDia(data));
        return ResponseEntity.ok(toResponse(agenda));
    }

    @GetMapping("/dias/{data}/pendentes")
    public ResponseEntity<List<AgendamentoResponseDto>> listarPendentes(@PathVariable LocalDate data) {
        return ResponseEntity.ok(listarPendentesHandler.handler(new AgendamentoQueryListarPendentes(data))
                .stream().map(this::toResponse).toList());
    }

    @PostMapping("/dias/{data}/separacao-automatica")
    public ResponseEntity<ResultadoSeparacaoResponseDto> separarAutomaticamente(@PathVariable LocalDate data) {
        var resultado = separarAutomaticamenteHandler.handler(new AgendamentoCommandSepararAutomaticamente(data));
        return ResponseEntity.ok(toResponse(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(buscarHandler.handler(new AgendamentoQueryBuscar(id))));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        removerHandler.handler(new AgendamentoCommandRemover(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pessoas/{idPessoa}/historico-viagens")
    public ResponseEntity<PageResponseDto<HistoricoViagemResponseDto>> buscarHistoricoViagens(
            @PathVariable UUID idPessoa,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "destinoBusca", defaultValue = "") String destinoBusca) {
        PageRequest pageable = PageRequest.of(page, 10);
        
        Page<HistoricoViagemProjecao> resultado = destinoBusca.isEmpty() || destinoBusca.isBlank()
                ? queryRepository.buscarHistoricoViagensPorPessoa(idPessoa, pageable)
                : queryRepository.buscarHistoricoViagensPorPessoaComBusca(idPessoa, destinoBusca, pageable);
        
        Page<HistoricoViagemResponseDto> mapped = resultado.map(this::toHistoricoViagemResponse);
        return ResponseEntity.ok(new PageResponseDto<>(mapped));
    }

    private HistoricoViagemResponseDto toHistoricoViagemResponse(HistoricoViagemProjecao projecao) {
        return new HistoricoViagemResponseDto(
                projecao.getId(),
                projecao.getData(),
                projecao.getHorarioChegada(),
                projecao.getHorarioSaidaNecessario(),
                projecao.getDestinoNome(),
                projecao.getListaDescricao(),
                projecao.getTipoVeiculoNome(),
                projecao.isBuscarNaResidencia(),
                projecao.getAcompanhantes());
    }

    private static class HistoricoViagemResponseDto {
        private UUID id;
        private LocalDate data;
        private java.time.LocalTime horarioChegada;
        private java.time.LocalTime horarioSaidaNecessario;
        private String destinoNome;
        private String listaDescricao;
        private String tipoVeiculoNome;
        private Boolean buscarNaResidencia;
        private Integer acompanhantes;

        public HistoricoViagemResponseDto(UUID id, LocalDate data, java.time.LocalTime horarioChegada,
                java.time.LocalTime horarioSaidaNecessario, String destinoNome, String listaDescricao,
                String tipoVeiculoNome, Boolean buscarNaResidencia, Integer acompanhantes) {
            this.id = id;
            this.data = data;
            this.horarioChegada = horarioChegada;
            this.horarioSaidaNecessario = horarioSaidaNecessario;
            this.destinoNome = destinoNome;
            this.listaDescricao = listaDescricao;
            this.tipoVeiculoNome = tipoVeiculoNome;
            this.buscarNaResidencia = buscarNaResidencia;
            this.acompanhantes = acompanhantes;
        }

        public UUID getId() { return id; }
        public LocalDate getData() { return data; }
        public java.time.LocalTime getHorarioChegada() { return horarioChegada; }
        public java.time.LocalTime getHorarioSaidaNecessario() { return horarioSaidaNecessario; }
        public String getDestinoNome() { return destinoNome; }
        public String getListaDescricao() { return listaDescricao; }
        public String getTipoVeiculoNome() { return tipoVeiculoNome; }
        public Boolean getBuscarNaResidencia() { return buscarNaResidencia; }
        public Integer getAcompanhantes() { return acompanhantes; }
    }

    private AgendaDiaResponseDto toResponse(AgendaDia agenda) {
        return new AgendaDiaResponseDto(
                agenda.data(),
                agenda.veiculos().stream().map(this::toResponse).toList(),
                agenda.passageirosPendentes().stream().map(this::toResponse).toList());
    }

    private VeiculoAbertoResponseDto toResponse(VeiculoAgenda veiculo) {
        return new VeiculoAbertoResponseDto(
                veiculo.id(),
                veiculo.data(),
                veiculo.idTipoVeiculo(),
                veiculo.nomeTipoVeiculo(),
                veiculo.capacidade(),
                veiculo.identificacao(),
                veiculo.horarioSaida(),
                veiculo.assentosOcupados(),
                veiculo.passageiros().stream().map(this::toResponse).toList());
    }

    private AgendamentoResponseDto toResponse(AgendamentoAgenda agendamento) {
        return new AgendamentoResponseDto(
                agendamento.id(),
                agendamento.data(),
                agendamento.idPessoa(),
                agendamento.idLista(),
                agendamento.idRegraCadastro(),
                agendamento.idTipoVeiculo(),
                agendamento.idDestino(),
                agendamento.horarioChegada(),
                agendamento.horarioSaidaNecessario(),
                agendamento.buscarNaResidencia(),
                agendamento.acompanhantes(),
                agendamento.idVeiculoAberto(),
                agendamento.idsVeiculosCompativeis(),
                agendamento.autorNome());
    }

    private AgendamentoResponseDto toResponse(Agendamento agendamento) {
        return new AgendamentoResponseDto(
                agendamento.getId(),
                agendamento.getData(),
                agendamento.getIdPessoa(),
                agendamento.getIdLista(),
                agendamento.getIdRegraCadastro(),
                agendamento.getIdTipoVeiculo(),
                agendamento.getIdDestino(),
                agendamento.getHorarioChegada(),
                CompatibilidadeVeiculoAgendamento.horarioSaidaNecessario(agendamento),
                agendamento.isBuscarNaResidencia(),
                agendamento.getAcompanhantes(),
                agendamento.getIdVeiculoAberto(),
                List.of(),
                agendamento.getAutorNome());
    }



    private ResultadoSeparacaoResponseDto toResponse(ResultadoSeparacaoAutomatica resultado) {
        return new ResultadoSeparacaoResponseDto(
                resultado.passageirosAlocados(),
                resultado.passageirosPendentes().stream()
                        .map(pendente -> new ResultadoSeparacaoResponseDto.PassageiroPendenteResponseDto(
                                pendente.idAgendamento(), pendente.motivo()))
                        .toList());
    }

    private static VeiculoPermitidoResponseDto toResponse(VeiculoPermitido veiculo) {
        return new VeiculoPermitidoResponseDto(
                veiculo.idTipoVeiculo(), veiculo.nome(), veiculo.capacidade(), veiculo.idsRegrasAprovadas());
    }
}
