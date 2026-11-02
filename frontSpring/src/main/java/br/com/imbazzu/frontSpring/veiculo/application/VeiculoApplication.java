package br.com.imbazzu.frontSpring.veiculo.application;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.porta.out.VeiculoApiPorta;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VeiculoApplication implements VeiculoApplicationPorta{

    private final VeiculoApiPorta api;

    public VeiculoApplication(VeiculoApiPorta api){
        this.api = api;
    }

    @Override
    public void arquivar(UUID id) {
        api.arquivar(id);
    }

    @Override
    public void desarquivar(UUID id) {
        api.desarquivar(id);
    }

    @Override
    public void editar(UUID id, TipoVeiculoRequestDto dto) {
        api.editar(id, dto);
    }

    @Override
    public void salvar(TipoVeiculoRequestDto dto) {
        api.salvar(dto);
    }

    @Override
    public void deletar(UUID id) {
        api.deletar(id);
    }

    @Override
    public ResultadoPaginado<TipoVeiculoResponseDto> listar(String nome, int pagina) {
        return api.listar(nome,pagina,7);
    }

    @Override
    public ResultadoPaginado<TipoVeiculoArquivadoResponseDto> listarArquivados(String nome, int pagina) {
        return api.listarArquivados(nome,pagina,7);
    }

    @Override
    public TipoVeiculoResponseDto buscar(UUID id) {
        return api.buscar(id);
    }

    @Override
    public TipoVeiculoArquivadoResponseDto buscarArquivado(UUID id) {return api.buscarArquivado(id);}

    @Override
    public List<TipoVeiculoHistoricoResponseDto> buscarHistorico(UUID id) {
        final int tamanhoPagina = 100;
        var primeiraPagina = api.listarHistorico(id, 0, tamanhoPagina);
        var historico = new ArrayList<>(primeiraPagina.conteudo());
        for (int pagina = 1; pagina < primeiraPagina.totalPaginas(); pagina++) {
            historico.addAll(api.listarHistorico(id, pagina, tamanhoPagina).conteudo());
        }
        return List.copyOf(historico);
    }
}
