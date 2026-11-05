package br.com.imbazzu.secretariaTransporte.veiculo.adpter.in;

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
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoAtualizarRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.abrirVeiculo.AgendamentoCommandAbrirVeiculo;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.abrirVeiculo.AgendamentoCommandAbrirVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.atribuirVeiculo.AgendamentoCommandAtribuirVeiculo;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.atribuirVeiculo.AgendamentoCommandAtribuirVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizar.AgendamentoCommandAtualizar;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizar.AgendamentoCommandAtualizarHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizarVeiculo.AgendamentoCommandAtualizarVeiculo;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizarVeiculo.AgendamentoCommandAtualizarVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.removerVeiculo.AgendamentoCommandRemoverVeiculo;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.removerVeiculo.AgendamentoCommandRemoverVeiculoHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.removerVeiculoAberto.AgendamentoCommandRemoverVeiculoAberto;
import br.com.imbazzu.secretariaTransporte.veiculo.application.command.removerVeiculoAberto.AgendamentoCommandRemoverVeiculoAbertoHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.application.query.listarVeiculos.AgendamentoQueryListarVeiculos;
import br.com.imbazzu.secretariaTransporte.veiculo.application.query.listarVeiculos.AgendamentoQueryListarVeiculosHandler;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Veículos do agendamento", description = "Abertura e gerenciamento dos veículos disponíveis por dia")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class VeiculoController {

    private final AgendamentoCommandAbrirVeiculoHandler abrirVeiculoHandler;
    private final AgendamentoCommandAtualizarVeiculoHandler atualizarVeiculoHandler;
    private final AgendamentoCommandRemoverVeiculoAbertoHandler removerVeiculoAbertoHandler;
    private final AgendamentoCommandAtualizarHandler atualizarHandler;
    private final AgendamentoCommandAtribuirVeiculoHandler atribuirVeiculoHandler;
    private final AgendamentoCommandRemoverVeiculoHandler removerVeiculoHandler;
    private final AgendamentoQueryListarVeiculosHandler listarVeiculosHandler;
    private final AgendamentoQueryPorta consultas;

    @DeleteMapping("/veiculos/{id}")
    public ResponseEntity<Void> removerVeiculoAberto(@PathVariable UUID id) {
        removerVeiculoAbertoHandler.handler(new AgendamentoCommandRemoverVeiculoAberto(id));
        return ResponseEntity.noContent().build();
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

    @GetMapping("/dias/{data}/veiculos")
    public ResponseEntity<List<VeiculoAbertoResponseDto>> listarVeiculos(@PathVariable LocalDate data) {
        var veiculos = listarVeiculosHandler.handler(new AgendamentoQueryListarVeiculos(data));
        var agendamentos = consultas.listarAgendamentos(data);
        var response = veiculos.stream()
                .map(veiculo -> toResponse(veiculo, agendamentos))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/veiculos/{id}")
    public ResponseEntity<VeiculoAbertoResponseDto> atualizarVeiculo(
            @PathVariable UUID id,
            @RequestBody @Valid VeiculoAbertoAtualizarRequestDto request) {
        var veiculo = atualizarVeiculoHandler.handler(new AgendamentoCommandAtualizarVeiculo(
                id, request.identificacao(), request.horarioSaida()));
        return ResponseEntity.ok(toResponse(veiculo, consultas.listarAgendamentos(veiculo.data())));
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

    private VeiculoAbertoResponseDto toResponse(VeiculoAberto veiculo, List<Agendamento> agendamentos) {
        var passageiros = agendamentos.stream()
                .filter(agendamento -> veiculo.id().equals(agendamento.getIdVeiculoAberto()))
                .toList();
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
}
