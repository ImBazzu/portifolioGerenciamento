package br.com.imbazzu.secretariaTransporte.cadastrais.application;

import br.com.imbazzu.secretariaTransporte.cadastrais.application.mapper.PessoaApplicationMapper;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Pessoa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.PessoaRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaRequestDto;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Classe responsável pela lógica envolvendo com Paciente
 */
@Transactional
public class PessoaApplication implements PessoaApplicationPorta {

    private final PessoaRepositorioPorta banco;


    public PessoaApplication(PessoaRepositorioPorta banco) {
        this.banco = banco;
    }

    //=================
    //Command
    //=================
    /**
     * Salva no banco de dados um novo paciente
     * As informações são obtidas pelo o {@link PessoaRequestDto}
     *
     * @return O novo Paciente salvo no banco de dados
     */
    @Override
    public UUID salvar(PessoaSalvarInputDto dto) {
        // Transforma o DTO em entidade
        var pessoa = PessoaApplicationMapper.paraDomain(dto);
        //Verifica se pessoa já está cadastrada
        if (banco.verificarExistenciaCpf(pessoa.getCpf().valor())) {
            //Exceção para caso em duplicidade
            throw new EntidadeDuplicadaException(
                    "Pessoa já cadastrada.%nCPF: %s".formatted(pessoa.getCpf().valor()));
        }
        //Salva a entidade no Banco e retorna o id
        return  banco.salvar(pessoa);
    }
    /**
     * Edita um paciente no banco de dados.
     * <p>
     * Buscar um paciente pelo ‘Id’ usando o metodo @{buscarPorId}
     */
    @Override
    public void editar(UUID id, PessoaSalvarInputDto dto) {
        //Busca a entidade pessoa
        var pessoa = buscarEntidade(id);
        //Altera as informações de pessoa
        PessoaApplicationMapper.atualizar(pessoa, dto);
        //Verificação de duplicidade
        if(banco.verificarExistenciaCpfIgnorandoId(pessoa.getCpf().valor(),id)){
            //Exceção de duplicidade
            throw new EntidadeDuplicadaException(
                    "Pessoa já cadastrada.%nCPF: %s".formatted(pessoa.getCpf().valor()));
        }
        //Salva a alteração no banco
        banco.salvar(pessoa);
    }

    /**
     * Arquiva a pessoa
     * @param idPessoa identificador de pessoa
     */
    @Override
    public void arquivar(UUID idPessoa) {
        //Busca a entidade pessoa
        var pessoa = buscarEntidade(idPessoa);
        //Arquiva pessoa
        pessoa.arquivar();
        //Salva alteração no banco
        banco.salvar(pessoa);
    }

    /**
     * Desarquiva pessoa
     * @param idPessoa identificador de pessoa
     */
    @Override
    public void desarquivar(UUID idPessoa) {
        //Busca a entidade pessoa
        var  pessoa = buscarEntidade(idPessoa);
        //Desarquiva pessoa
        pessoa.desarquivar();
        //Salva alteração no banco
        banco.salvar(pessoa);
    }

    //=================
    //Query
    //=================

    /**
     * Busca dados de pessoa pelo id
     * @param id identificador de pessoa
     * @return dados da pessoa buscada
     */
    @Transactional(readOnly = true)
    @Override
    public PessoaInfoOutputDto buscarPorId(UUID id) {
        //Busca pessoa e realiza o mapper de retorno
        return PessoaApplicationMapper.paraInfo(buscarEntidade(id));
    }


    /**
     * Lista paginada de pessoa com base no nome
     * @param nome nome procurado
     * @param pagina pagina
     * @param tamanho quantidade de retorno
     * @return lista paginada de pessoa
     */
    @Transactional(readOnly = true)
    @Override
    public ResultadoPaginado<PessoaInfoOutputDto> listarPorNome(String nome, int pagina, int tamanho) {
        // Retorna a lista de todos os pacientes que possuem alguma compatibilidade com
        // o nome passado
        var listaRetorno = banco.buscarPorNome(nome, pagina, tamanho);
        //Mapeia a lista para o retorno aceitável
        return listaRetorno.map(PessoaApplicationMapper::paraInfo);
    }



    //=================
    //Metodos privados
    //=================
    /**
     * Busca um paciente pelo seu identificador único "ID".
     *
     * @param idPessoa o identificador único da pessoa a ser buscado.
     * @return o paciente correspondente ao "ID" informado.
     */
    protected Pessoa buscarEntidade(UUID idPessoa) {
        // Busca o paciente através do "id"
        return banco.buscarPorId(idPessoa).orElseThrow(
                // Caso o "id" não seja encontrado retorne o erro
                () -> new EntidadeNaoEncontradoException(
                        "Pessoa não encontrada.%nID: %s".formatted(idPessoa)));
    }
}
