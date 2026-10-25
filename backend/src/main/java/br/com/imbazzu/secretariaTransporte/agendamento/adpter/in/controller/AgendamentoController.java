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

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendaDiaResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.ResultadoSeparacaoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoPermitidoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.application.AgendamentoApplication;
import br.com.imbazzu.secretariaTransporte.agendamento.application.model.VeiculoPermitido;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
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

    private final AgendamentoApplication application;

    @PostMapping("/dias/{data}/abrir")
    public ResponseEntity<Void> abrirDia(@PathVariable LocalDate data) {
        application.abrirDia(data);
        return ResponseEntity.created(URI.create("/api/agendamentos/dias/%s".formatted(data))).build();
    }

    @PostMapping("/dias/{data}/veiculos")
    public ResponseEntity<VeiculoAbertoResponseDto> abrirVeiculo(
            @PathVariable LocalDate data,
            @RequestBody @Valid AbrirVeiculoRequestDto request) {
        var veiculo = application.abrirVeiculo(
                data, request.idTipoVeiculo(), request.identificacao(), request.horarioSaida());
        return ResponseEntity.created(URI.create("/api/agendamentos/dias/%s/veiculos/%s"
                .formatted(data, veiculo.id()))).body(toVeiculoResponse(veiculo, List.of()));
    }

    @PostMapping("/dias/{data}/verificar-veiculos")
    public ResponseEntity<VerificacaoVeiculosResponseDto> verificarVeiculos(
            @PathVariable LocalDate data,
            @RequestBody @Valid VerificarVeiculosRequestDto request) {
        var resultado = application.verificarVeiculos(
                data, request.idPessoa(), request.idLista(), request.idDestino(),
                request.horarioChegada(), request.acompanhantes());
        var permitidos = resultado.veiculosPermitidos().stream()
                .map(AgendamentoController::toResponse)
                .toList();
        return ResponseEntity.ok(new VerificacaoVeiculosResponseDto(permitidos, resultado.motivosReprovacao()));
    }

    @PostMapping("/dias/{data}/passageiros")
    public ResponseEntity<Void> criar(
            @PathVariable LocalDate data,
            @RequestBody @Valid AgendamentoRequestDto request) {
        var id = application.criar(
                data,
                request.idPessoa(),
                request.idLista(),
                request.idRegraCadastro(),
                request.idTipoVeiculo(),
                request.idDestino(),
                request.horarioChegada(),
                request.buscarNaResidencia(),
                request.acompanhantes());
        return ResponseEntity.created(URI.create("/api/agendamentos/%s".formatted(id))).build();
    }

    @GetMapping("/dias/{data}")
    public ResponseEntity<AgendaDiaResponseDto> buscarDia(@PathVariable LocalDate data) {
        var agendamentos = application.listarPorData(data);
        var veiculos = application.listarVeiculos(data).stream()
                .map(veiculo -> toVeiculoResponse(
                        veiculo,
                        agendamentos.stream()
                                .filter(agendamento -> veiculo.id().equals(agendamento.getIdVeiculoAberto()))
                                .toList()))
                .toList();
        var pendentes = agendamentos.stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() == null)
                .sorted(java.util.Comparator.comparing(Agendamento::getHorarioChegada))
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(new AgendaDiaResponseDto(data, veiculos, pendentes));
    }

    @GetMapping("/dias/{data}/pendentes")
    public ResponseEntity<List<AgendamentoResponseDto>> listarPendentes(@PathVariable LocalDate data) {
        return ResponseEntity.ok(application.listarPendentes(data).stream().map(this::toResponse).toList());
    }

    @PostMapping("/dias/{data}/separacao-automatica")
    public ResponseEntity<ResultadoSeparacaoResponseDto> separarAutomaticamente(@PathVariable LocalDate data) {
        var resultado = application.separarAutomaticamente(data);
        return ResponseEntity.ok(new ResultadoSeparacaoResponseDto(
                resultado.passageirosAlocados(),
                resultado.passageirosPendentes().stream()
                        .map(pendente -> new ResultadoSeparacaoResponseDto.PassageiroPendenteResponseDto(
                                pendente.idAgendamento(), pendente.motivo()))
                        .toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDto> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(application.buscar(id)));
    }

    @PutMapping("/{id}/veiculo/{idVeiculo}")
    public ResponseEntity<Void> atribuirManualmente(
            @PathVariable UUID id,
            @PathVariable UUID idVeiculo) {
        application.atribuirManualmente(id, idVeiculo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/veiculo")
    public ResponseEntity<Void> removerDoVeiculo(@PathVariable UUID id) {
        application.removerDoVeiculo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        application.remover(id);
        return ResponseEntity.noContent().build();
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
                application.horarioSaidaNecessario(agendamento),
                agendamento.isBuscarNaResidencia(),
                agendamento.getAcompanhantes(),
                agendamento.getIdVeiculoAberto());
    }

    private VeiculoAbertoResponseDto toVeiculoResponse(VeiculoAberto veiculo, List<Agendamento> passageiros) {
        return new VeiculoAbertoResponseDto(
                veiculo.id(),
                veiculo.data(),
                veiculo.idTipoVeiculo(),
                veiculo.nomeTipoVeiculo(),
                veiculo.capacidade(),
                veiculo.identificacao(),
                application.horarioSaidaDoVeiculo(veiculo),
                passageiros.stream().mapToInt(Agendamento::quantidadePessoas).sum(),
                passageiros.stream().map(this::toResponse).toList());
    }

    private static VeiculoPermitidoResponseDto toResponse(VeiculoPermitido veiculo) {
        return new VeiculoPermitidoResponseDto(
                veiculo.idTipoVeiculo(), veiculo.nome(), veiculo.capacidade(), veiculo.idsRegrasAprovadas());
    }
}
