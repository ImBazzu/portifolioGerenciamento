package br.com.imbazzu.frontspring.veiculo.application;

import br.com.imbazzu.frontspring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontspring.veiculo.core.porta.out.VeiculoApiPorta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoApplication implements VeiculoApplicationPorta{

    private final VeiculoApiPorta api;

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
        var conteudo = api.listarArquivados(nome,pagina,7);
        System.err.println(conteudo.conteudo());
        return conteudo;
    }

    @Override
    public TipoVeiculoResponseDto buscar(UUID id) {
        return api.buscar(id);
    }

    @Override
    public TipoVeiculoArquivadoResponseDto buscarArquivado(UUID id) {return api.buscarArquivado(id);}

    @Override
    public List<TipoVeiculoHistoricoResponseDto> buscarHistorico(UUID id) {
        return List.of();
    }
}
