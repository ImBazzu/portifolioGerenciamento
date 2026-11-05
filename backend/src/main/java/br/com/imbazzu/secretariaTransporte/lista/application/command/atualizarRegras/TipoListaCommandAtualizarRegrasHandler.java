package br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.lista.core.excecoes.TipoListaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoListaCommandAtualizarRegrasHandler {

    private final TipoListaPorta porta;
    private final TipoVeiculoQueryPorta tipoVeiculoQuery;

    @Transactional
    public void handler(TipoListaCommandAtualizarRegras command) {
        var lista = porta.buscarPorId(command.id())
                .orElseThrow(() -> new TipoListaNaoEncontradoException(command.id()));

        command.regrasSeparacao().forEach(regra -> {
            var tipoVeiculo = porta.buscarPorId(regra.idTipoVeiculo())
        .orElseThrow(()-> new TipoListaNaoEncontradoException(regra.idTipoVeiculo()));
            if (tipoVeiculo) {
                throw new RegraDeNegocioException(
                        "A regra de separação só pode ser aplicada a tipos de veículo Carro.");
            }
        });

        // Os getters devolvem cópia (List.copyOf), então remover durante a iteração é seguro.
        lista.getListaRegrasCadastro().forEach(regra -> lista.removerRegraCadastro(regra.getId()));
        lista.getListaRegraSeparacao().forEach(regra -> lista.removerRegraSeparacao(regra.getId()));

        command.regrasCadastro().forEach(regra -> lista.criarRegraCadastro(
                regra.idsVeiculos(),
                regra.idsCidades(),
                regra.prioridades(),
                regra.inicioHorario(),
                regra.finalHorario(),
                regra.obrigacaoAnexo(),
                observacaoOpcional(regra.descricao())));
        command.regrasSeparacao().forEach(regra -> lista.criarRegraSeparacao(
                regra.idTipoVeiculo(),
                regra.tempoTolerancia()));

        porta.salvar(lista);
    }

    private Observacao observacaoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : new Observacao(valor);
    }
}
