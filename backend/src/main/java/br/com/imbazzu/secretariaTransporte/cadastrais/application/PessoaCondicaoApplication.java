package br.com.imbazzu.secretariaTransporte.cadastrais.application;


import br.com.imbazzu.secretariaTransporte.cadastrais.application.mapper.PessoaCondicaoApplicationMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.PessoaCondicao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaCondicaoApplicationPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.PessoaCondicaoRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
public class PessoaCondicaoApplication implements PessoaCondicaoApplicationPorta {

    private final PessoaCondicaoRepositorioPorta banco;

    public PessoaCondicaoApplication(PessoaCondicaoRepositorioPorta banco) {
        this.banco = banco;
    }


    //=================
    //Command
    //=================

    /**
     * Salva a nova condição no banco
     * @param dto novas informações da condição
     * @return identificador da nova condição criada
     */
    @Override
    public UUID salvar(PessoaCondicaoSalvarInputDto dto) {
        //Cria a nova condição
        var pessoaCondicao = PessoaCondicaoApplicationMapper.paraDomain(dto);
        //Verifica de duplicidade
        if (banco.existePorNome(pessoaCondicao.getNome().valor())) {
            //Lança a exceção de duplicidade
            throw new EntidadeDuplicadaException(
                    "Condição já cadastrada.%n Nome: %s".formatted(pessoaCondicao.getNome().valor()));
        }
        //Salva a exceção e retorna o ID
        return banco.salvar(pessoaCondicao);
    }

    /**
     * Editar a condição no banco
     * @param id identificador da condição
     * @param dto dados da condição
     */
    @Override
    public void editar(UUID id, PessoaCondicaoSalvarInputDto dto) {
        //Busca a entidade
        var pessoaCondicao = buscarEntidade(id);
        //Edita com as informações novas passadas
        PessoaCondicaoApplicationMapper.atualizar(pessoaCondicao,dto);
        //Verifica duplicidade
        if(banco.existePorNomeIgnorandoId(pessoaCondicao.getNome().valor(), pessoaCondicao.getId())) {
            //Lança a exceção da duplicidade
            throw new EntidadeDuplicadaException(
                    "Condição já cadastrada.%n Nome: %s".formatted(pessoaCondicao.getNome().valor()));
        }
        //salva a alteração no banco
        banco.salvar(pessoaCondicao);
    }

    /**
     * Arquiva a condição
     * @param idCondicao identificador da condição
     */
    @Override
    public void arquivar(UUID idCondicao) {
        //Busca a entidade
        var entidade =  buscarEntidade(idCondicao);
        //Arquiva
        entidade.arquivar();
        //Salva a alteração
        banco.salvar(entidade);
    }

    /**
     * Desarquiva a condição
     * @param idCondicao identificador da condição
     */
    @Override
    public void desarquivar(UUID idCondicao) {
        //Busca a entidade
        var entidade =   buscarEntidade(idCondicao);
        //Desarquiva a condição
        entidade.desarquivar();
        //Salva a alteração no banco
        banco.salvar(entidade);
    }
    //=================
    //Query
    //=================

    /**
     * Busca as informações da condição pelo id
     * @param id identificador da condição
     * @return informações da condição
     */
    @Transactional(readOnly = true)
    @Override
    public PessoaCondicaoInfoOutputDto buscarPorId(UUID id){
        //Busca a condição pelo id, e realiza o mapeio para o retorno
        return PessoaCondicaoApplicationMapper.paraInfo(buscarEntidade(id));
    }

    /**
     * Lista paginada da condição com base no nome passado
     * @param nome nome procurado
     * @param pagina pagina
     * @param tamanho quantidade de itens retornado
     * @return lista paginada das condições
     */
    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<PessoaCondicaoInfoOutputDto>
    buscarPorNome(String nome, int pagina, int tamanho) {
        //busca a lista paginada
        var result = banco.buscarPorNome(nome,pagina,tamanho);
        //realiza o mapeio para o retorno
        return result.map(PessoaCondicaoApplicationMapper::paraInfo);
    }


    //=================
    //metodos privados
    //=================

    /**
     * Busca a entidade pelo o identificador
     * @param id identificador
     * @return entidade pessoaCondição
     */
    protected PessoaCondicao buscarEntidade(UUID id){
        //Busca a entidade pelo id e retorna erro caso não tenha sido encontrada
        return banco.buscarPorId(id).orElseThrow(
                ()->new EntidadeNaoEncontradoException(
                        "Condição não encontrada.%nID: %s".formatted(id))
        );
    }

}
