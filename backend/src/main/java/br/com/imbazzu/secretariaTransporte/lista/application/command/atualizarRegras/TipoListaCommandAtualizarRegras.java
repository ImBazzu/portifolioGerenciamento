package br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras;

import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.adpter.model.RegraCadastroInput;
import br.com.imbazzu.secretariaTransporte.lista.adpter.model.RegraSeparacaoInput;

public record TipoListaCommandAtualizarRegras(
        UUID id,
        List<RegraCadastroInput> regrasCadastro,
        List<RegraSeparacaoInput> regrasSeparacao) {
}
