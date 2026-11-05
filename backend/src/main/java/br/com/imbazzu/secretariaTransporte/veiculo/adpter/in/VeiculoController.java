package br.com.imbazzu.secretariaTransporte.veiculo.adpter.in;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoAtualizarRequestDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
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
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
import jakarta.validation.Valid;

public class VeiculoController {
        private final AgendamentoCommandAbrirVeiculoHandler abrirVeiculoHandler;
    private final AgendamentoCommandAtualizarVeiculoHandler atualizarVeiculoHandler;
    private final AgendamentoCommandRemoverVeiculoAbertoHandler removerVeiculoAbertoHandler;
    private final AgendamentoCommandAtualizarHandler atualizarHandler;

        private final AgendamentoCommandAtribuirVeiculoHandler atribuirVeiculoHandler;
    private final AgendamentoCommandRemoverVeiculoHandler removerVeiculoHandler;

    
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

    @PutMapping("/veiculos/{id}")
    public ResponseEntity<VeiculoAbertoResponseDto> atualizarVeiculo(
            @PathVariable UUID id,
            @RequestBody @Valid VeiculoAbertoAtualizarRequestDto request) {
        var veiculo = atualizarVeiculoHandler.handler(new AgendamentoCommandAtualizarVeiculo(
                id, request.identificacao(), request.horarioSaida()));
        return ResponseEntity.ok(toResponse(veiculo, List.of()));
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
}
