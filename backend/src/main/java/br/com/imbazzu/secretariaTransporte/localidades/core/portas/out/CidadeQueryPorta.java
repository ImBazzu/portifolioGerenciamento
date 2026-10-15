package br.com.imbazzu.secretariaTransporte.localidades.core.portas.out;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Cidade;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;

public interface CidadeQueryPorta {

    ResultadoPaginado<Cidade> listar(String nome, int pagina, int tamanho);

    boolean existeCidade(UUID idCidade);

    boolean verificarCidadeCadastrada(String nome, CidadeEstadoEnum estado);

    boolean verificarCidadeCadastradaIgnorandoId(UUID idCidade, String nomeCidade, CidadeEstadoEnum estado);

}
