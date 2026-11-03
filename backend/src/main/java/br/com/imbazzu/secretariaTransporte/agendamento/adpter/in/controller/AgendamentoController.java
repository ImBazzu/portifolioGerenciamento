package br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendaDiaResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.ResultadoSeparacaoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoAtualizarRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoPermitidoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirDia.AgendamentoCommandAbrirDia;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirDia.AgendamentoCommandAbrirDiaHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirVeiculo.AgendamentoCommandAbrirVeiculo;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirVeiculo.AgendamentoCommandAbrirVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizarVeiculo.AgendamentoCommandAtualizarVeiculo;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizarVeiculo.AgendamentoCommandAtualizarVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo.AgendamentoCommandAtribuirVeiculo;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo.AgendamentoCommandAtribuirVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.criar.AgendamentoCommandCriarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizar.AgendamentoCommandAtualizar;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.atualizar.AgendamentoCommandAtualizarHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.model.ResultadoSeparacaoAutomatica;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover.AgendamentoCommandRemover;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover.AgendamentoCommandRemoverHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculo.AgendamentoCommandRemoverVeiculo;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculo.AgendamentoCommandRemoverVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculoAberto.AgendamentoCommandRemoverVeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculoAberto.AgendamentoCommandRemoverVeiculoAbertoHandler;
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
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Agendamentos", description = "Abertura diária de veículos, agendamentos e separação de passageiros")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoCommandAbrirDiaHandler abrirDiaHandler;
    private final AgendamentoCommandAbrirVeiculoHandler abrirVeiculoHandler;
    private final AgendamentoCommandAtualizarVeiculoHandler atualizarVeiculoHandler;
    private final AgendamentoCommandRemoverVeiculoAbertoHandler removerVeiculoAbertoHandler;
    private final AgendamentoQueryVerificarVeiculosHandler verificarVeiculosHandler;
    private final AgendamentoCommandCriarHandler criarHandler;
    private final AgendamentoCommandAtualizarHandler atualizarHandler;
    private final AgendamentoQueryBuscarDiaHandler buscarDiaHandler;
    private final AgendamentoQueryListarPendentesHandler listarPendentesHandler;
    private final AgendamentoCommandSepararAutomaticamenteHandler separarAutomaticamenteHandler;
    private final AgendamentoQueryBuscarHandler buscarHandler;
    private final AgendamentoCommandAtribuirVeiculoHandler atribuirVeiculoHandler;
    private final AgendamentoCommandRemoverVeiculoHandler removerVeiculoHandler;
    private final AgendamentoCommandRemoverHandler removerHandler;

    @PostMapping("/dias/{data}/abrir")
    public ResponseEntity<Void> abrirDia(@PathVariable LocalDate data) {
        abrirDiaHandler.handler(new AgendamentoCommandAbrirDia(data));
        return ResponseEntity.created(URI.create("/api/agendamentos/dias/%s".formatted(data))).build();
    }

    @PostMapping("/dias/{data}/veiculos")
    public ResponseEntity<VeiculoAbertoResponseDto> abrirVeiculo(
            @PathVariable LocalDate data,
            @RequestBody @Valid AbrirVeiculoRequestDto request) {
        var veiculo = abrirVeiculoHandler.handler(new AgendamentoCommandAbrirVeiculo(
                data, request.idTipoVeiculo(), request.identificacao(), request.horarioSaida()));
        return ResponseEntity.created(URI.create("/api/agendamentos/dias/%s/veiculos/%s"
                .formatted(data, veiculo.id()))).body(toResponse(veiculo, List.of()));
    }

    @PutMapping("/veiculos/{id}")
    public ResponseEntity<VeiculoAbertoResponseDto> atualizarVeiculo(
            @PathVariable UUID id,
            @RequestBody @Valid VeiculoAbertoAtualizarRequestDto request) {
        var veiculo = atualizarVeiculoHandler.handler(new AgendamentoCommandAtualizarVeiculo(
                id, request.identificacao(), request.horarioSaida()));
        return ResponseEntity.ok(toResponse(veiculo, List.of()));
    }

    @DeleteMapping("/veiculos/{id}")
    public ResponseEntity<Void> removerVeiculoAberto(@PathVariable UUID id) {
        removerVeiculoAbertoHandler.handler(new AgendamentoCommandRemoverVeiculoAberto(id));
        return ResponseEntity.noContent().build();
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
            @RequestBody @Valid AgendamentoRequestDto request) {
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
                request.idVeiculo()));
        return ResponseEntity.created(URI.create("/api/agendamentos/%s".formatted(id))).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AgendamentoAtualizarRequestDto request) {
        atualizarHandler.handler(new AgendamentoCommandAtualizar(
                id,
                request.idTipoVeiculo(),
                request.idDestino(),
                request.horarioChegada(),
                request.buscarNaResidencia(),
                request.acompanhantes(),
                request.idVeiculo()));
        return ResponseEntity.noContent().build();
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

    @PutMapping("/{id}/veiculo/{idVeiculo}")
    public ResponseEntity<Void> atribuirManualmente(
            @PathVariable UUID id,
            @PathVariable UUID idVeiculo) {
        atribuirVeiculoHandler.handler(new AgendamentoCommandAtribuirVeiculo(id, idVeiculo));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/veiculo")
    public ResponseEntity<Void> removerDoVeiculo(@PathVariable UUID id) {
        removerVeiculoHandler.handler(new AgendamentoCommandRemoverVeiculo(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        removerHandler.handler(new AgendamentoCommandRemover(id));
        return ResponseEntity.noContent().build();
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
                agendamento.idsVeiculosCompativeis());
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
                List.of());
    }

    private VeiculoAbertoResponseDto toResponse(VeiculoAberto veiculo, List<Agendamento> passageiros) {
        return new VeiculoAbertoResponseDto(
                veiculo.id(),
                veiculo.data(),
                veiculo.idTipoVeiculo(),
                veiculo.nomeTipoVeiculo(),
                veiculo.capacidade(),
                veiculo.identificacao(),
                CompatibilidadeVeiculoAgendamento.horarioSaidaDoVeiculo(veiculo, passageiros),
                passageiros.stream().mapToInt(Agendamento::quantidadePessoas).sum(),
                passageiros.stream().map(this::toResponse).toList());
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
