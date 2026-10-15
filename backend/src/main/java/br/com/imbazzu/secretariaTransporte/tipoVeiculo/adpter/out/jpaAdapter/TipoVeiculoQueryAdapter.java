package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoQueryRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoArquivadoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoHistoricoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TipoVeiculoQueryAdapter implements TipoVeiculoQueryPorta {

    private final TipoVeiculoQueryRepository repo;

    @Override
    public ResultadoPaginado<TipoVeiculoOutput> listar(String nome, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var paginado = repo.listarAtivo(nome, pageable)
                .map(v -> new TipoVeiculoOutput(v.getId(), v.getNome(), v.getCapacidade()));
        return PageMapper.toDomain(paginado);
    }

    @Override
    public ResultadoPaginado<TipoVeiculoArquivadoOutput> listarArquivado(String nome, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var paginado = repo.listarArquivado(nome, pageable)
                .map(v -> new TipoVeiculoArquivadoOutput(v.getId(), v.getNome(), v.getCapacidade(),
                        v.getDataArquivado()));
        return PageMapper.toDomain(paginado);
    }

    @Override
    public TipoVeiculoOutput buscarAtivo(UUID id) {
        var tipoVeiculo = repo.buscarAtivoPorId(id).orElseThrow(
                () -> new TipoVeiculoNaoEncontradoException(id));
        return new TipoVeiculoOutput(tipoVeiculo.getId(), tipoVeiculo.getNome(), tipoVeiculo.getCapacidade());
    }

    @Override
    public ResultadoPaginado<TipoVeiculoHistoricoOutput> listarHistorico(UUID idTipoVeiculo, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var paginado = repo.buscarHistorico(idTipoVeiculo, pageable)
                .map(v -> new TipoVeiculoHistoricoOutput(v.getId(), v.getData(), v.getQtdVeiculo(), v.getDestinos(),
                        v.getTotalPassageiros()));
        return PageMapper.toDomain(paginado);
    }

    @Override
    public TipoVeiculoArquivadoOutput buscarArquivado(UUID id) {
        var tipoVeiculoArquivado = repo.buscarArquivadoPorId(id)
                .orElseThrow(() -> new TipoVeiculoNaoEncontradoException(id));
        System.err.println(tipoVeiculoArquivado);
        return new TipoVeiculoArquivadoOutput(
                tipoVeiculoArquivado.getId(),
                tipoVeiculoArquivado.getNome(),
                tipoVeiculoArquivado.getCapacidade(),
                tipoVeiculoArquivado.getDataArquivado());
    }

}
