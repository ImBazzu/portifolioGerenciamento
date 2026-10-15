package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageRequestMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.DestinoRepository;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoQueryPorta;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DestinoQueryAdapter implements DestinoQueryPorta {

    private final DestinoRepository destinoRepository;

    @Override
    public ResultadoPaginado<DestinoOutputArquivado> listarPorCidadeArquivado(
            UUID idCidade, String nome, int pagina, int tamanho,
            List<OrdemPaginacao> ordenacao) {
        var pageable = PageRequestMapper.toPageRequest(pagina, tamanho, ordenacao);
        var projecoesDestino = destinoRepository.listarPorCidadeArquivado(idCidade, nome, pageable);
        var resultadoPaginado = projecoesDestino.map(
                destino -> {
                    return new DestinoOutputArquivado(destino.getId(), destino.getNome(), destino.getDataArquivado(),
                            destino.getQtdViagens());
                });
        return PageMapper.toDomain(resultadoPaginado);
    }

    @Override
    public ResultadoPaginado<DestinoOutput> listarPorCidade(
            UUID idCidade, String nome, int pagina, int tamanho,
            List<OrdemPaginacao> ordenacao) {
        var pageable = PageRequestMapper.toPageRequest(pagina, tamanho, ordenacao);
        var paginaDestinoProjecao = destinoRepository.listarPorCidade(idCidade, nome, pageable);
        var resultadoPaginado = paginaDestinoProjecao.map(
                destino -> {
                    return new DestinoOutput(destino.getId(), destino.getNome(), destino.getQtdPassageiros());
                });
        return PageMapper.toDomain(resultadoPaginado);
    }

    @Override
    public boolean verificarDuplicidade(UUID idCidade, Nome nome) {
        return destinoRepository.existsByCidadeIdAndNome(idCidade, nome.valor());
    }

    @Override
    public boolean verificarDuplicidadeIgnorandoIdDestino(UUID idCidade, Nome nome, UUID idDestino) {
        return destinoRepository.existsByCidadeIdAndNomeAndIdNot(idCidade, nome.valor(), idDestino);
    }

    @Override
    public ResultadoPaginado<DestinoHistoricoOutput> buscarHistorico(UUID id, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var retorno = destinoRepository.buscarHistorico(id, pageable)
                .map(destino -> new DestinoHistoricoOutput(destino.getData(), destino.getQuantidade()));
        return PageMapper.toDomain(retorno);
    }

    @Override
    public Optional<DestinoOutput> buscarPorId(UUID idDestino) {
        return destinoRepository.buscarPorId(idDestino)
                .map(destino -> new DestinoOutput(destino.getId(),
                        destino.getNome(),
                        destino.getQtdPassageiros()));
    }

    @Override
    public Optional<DestinoOutputArquivado> buscarPorIdArquivado(UUID idDestino) {
        return destinoRepository.buscarPorIdArquivado(idDestino)
                .map(destino -> new DestinoOutputArquivado(destino.getId(),
                        destino.getNome(),
                        destino.getDataArquivado(),
                        destino.getQtdViagens()));
    }

    @Override
    public boolean verificarExistenciaPorId(UUID idDestino) {
        return destinoRepository.findById(idDestino).isPresent();
    }

}
