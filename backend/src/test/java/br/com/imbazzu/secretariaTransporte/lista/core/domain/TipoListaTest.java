package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

class TipoListaTest {

    @Test
    void regraDeCadastroAceitaAnexoQuandoNaoObrigatorioEAplicaIntervaloNoturno() {
        var tipoLista = TipoLista.criar(new Nome("Lista teste"), null);
        var cidade = UUID.randomUUID();
        var veiculo = UUID.randomUUID();
        var regra = tipoLista.criarRegraCadastro(
                List.of(veiculo),
                List.of(cidade),
                List.of(EnumPrioridade.ALTA),
                LocalTime.of(22, 0),
                LocalTime.of(5, 0),
                false,
                null);

        assertTrue(regra.verificacaoDasRegras(
                EnumPrioridade.ALTA, cidade, LocalTime.of(23, 0), true, veiculo));
        assertTrue(regra.verificacaoDasRegras(
                EnumPrioridade.ALTA, cidade, LocalTime.of(3, 0), false, veiculo));
        assertFalse(regra.verificacaoDasRegras(
                EnumPrioridade.ALTA, cidade, LocalTime.of(12, 0), false, veiculo));
    }

    @Test
    void regraDeCadastroExigeAnexoQuandoConfigurado() {
        var tipoLista = TipoLista.criar(new Nome("Lista teste"), null);
        var regra = tipoLista.criarRegraCadastro(
                List.of(UUID.randomUUID()),
                null,
                List.of(EnumPrioridade.URGENTE),
                null,
                null,
                true,
                null);

        assertFalse(regra.verificacaoDasRegras(
                EnumPrioridade.URGENTE, UUID.randomUUID(), LocalTime.NOON, false, regra.getIdsVeiculos().getFirst()));
        assertTrue(regra.verificacaoDasRegras(
                EnumPrioridade.URGENTE, UUID.randomUUID(), LocalTime.NOON, true, regra.getIdsVeiculos().getFirst()));
    }

    @Test
    void regraDeCadastroRejeitaHorarioIncompleto() {
        var tipoLista = TipoLista.criar(new Nome("Lista teste"), null);

        assertThrows(DadosInvalidosException.class, () -> tipoLista.criarRegraCadastro(
                List.of(UUID.randomUUID()),
                null,
                List.of(EnumPrioridade.BAIXA),
                LocalTime.NOON,
                null,
                false,
                null));
    }

    @Test
    void regraDeCadastroRejeitaValoresDuplicados() {
        var tipoLista = TipoLista.criar(new Nome("Lista teste"), null);
        var veiculo = UUID.randomUUID();

        assertThrows(DadosInvalidosException.class, () -> tipoLista.criarRegraCadastro(
                List.of(veiculo, veiculo),
                null,
                List.of(EnumPrioridade.BAIXA),
                null,
                null,
                false,
                null));
    }
}
