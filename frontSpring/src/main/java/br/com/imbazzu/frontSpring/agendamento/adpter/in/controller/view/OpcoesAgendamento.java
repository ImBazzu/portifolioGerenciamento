package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;

/** Dados de apoio dos formulários de agendamento, carregados uma vez por requisição. */
public record OpcoesAgendamento(
        List<ListaResponseDto> listas,
        List<TipoVeiculoResponseDto> tiposVeiculo,
        List<CidadeResponseDto> cidades,
        List<DestinoView> destinos) {

    public List<TipoVeiculoResponseDto> tiposNaoCarro() {
        return tiposVeiculo.stream().filter(t -> !TipoVeiculoUtil.ehCarro(t.nome())).toList();
    }

    public List<TipoVeiculoResponseDto> tiposCarro() {
        return tiposVeiculo.stream().filter(t -> TipoVeiculoUtil.ehCarro(t.nome())).toList();
    }

    public Optional<TipoVeiculoResponseDto> carroQuatroLugares() {
        return tiposVeiculo.stream()
                .filter(t -> "carro".equalsIgnoreCase(t.nome().strip()) && t.capacidade() == 4)
                .findFirst();
    }

    public Optional<ListaResponseDto> listaEncaixe() {
        return listas.stream().filter(lista -> "ENCAIXE".equals(lista.finalidadeEspecial())).findFirst();
    }

    public Map<UUID, String> nomesTipoVeiculo() {
        return tiposVeiculo.stream()
                .collect(Collectors.toMap(TipoVeiculoResponseDto::id, TipoVeiculoResponseDto::nome));
    }

    public Map<UUID, DestinoView> destinosPorId() {
        return destinos.stream().collect(Collectors.toMap(DestinoView::id, Function.identity()));
    }

    public Optional<DestinoView> destino(UUID id) {
        return destinos.stream().filter(d -> d.id().equals(id)).findFirst();
    }

    public Optional<CidadeResponseDto> cidade(UUID id) {
        return cidades.stream().filter(c -> c.id().equals(id)).findFirst();
    }
}
