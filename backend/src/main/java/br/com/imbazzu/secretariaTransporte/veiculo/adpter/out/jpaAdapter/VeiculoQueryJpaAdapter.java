package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.jpaAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.VeiculoQueryRepository;
import br.com.imbazzu.secretariaTransporte.veiculo.application.query.model.VeiculoHistoricoOutput;
import br.com.imbazzu.secretariaTransporte.veiculo.core.porta.out.VeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VeiculoQueryJpaAdapter implements VeiculoQueryPorta {

    private final VeiculoQueryRepository repository;

    @Override
    public ResultadoPaginado<VeiculoHistoricoOutput> buscarHistorico(
            UUID idTipoVeiculo, int pagina, int tamanho) {
        var resumo = repository.buscarResumo(idTipoVeiculo, PageRequest.of(pagina, tamanho));
        var datas = resumo.getContent().stream().map(item -> item.getData()).toList();
        var cidadesPorData = datas.isEmpty()
                ? Map.<LocalDate, String>of()
                : repository.buscarDestinosPorDatas(idTipoVeiculo, datas).stream()
                        .collect(Collectors.groupingBy(
                                item -> item.getData(),
                                Collectors.mapping(
                                        item -> item.getCidade(),
                                        Collectors.collectingAndThen(
                                                Collectors.toCollection(java.util.TreeSet::new),
                                                cidades -> String.join(", ", cidades)))));

        var historico = resumo.map(item -> new VeiculoHistoricoOutput(
                idTipoVeiculo,
                item.getData(),
                Math.toIntExact(item.getQtdVeiculo()),
                cidadesPorData.get(item.getData()),
                Math.toIntExact(item.getTotalPassageiros())));
        return PageMapper.toDomain(historico);
    }
}
