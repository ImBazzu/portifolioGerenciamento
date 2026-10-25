package br.com.imbazzu.secretariaTransporte.pessoa.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaApplication {

    private final PessoaPorta pessoaPorta;

    @Transactional
    public UUID criar(String nome, String observacao, EnumPrioridade prioridade,
            String endereco, String cpf, String telefone) {
        var pessoa = Pessoa.criar(new Nome(nome), observacaoOpcional(observacao),
                prioridade, new Endereco(endereco), new Cpf(cpf), new Telefone(telefone));
        validarCpfDisponivel(pessoa.getCpf().valor(), null);
        return pessoaPorta.salvar(pessoa);
    }

    @Transactional
    public void editar(UUID id, String nome, String observacao, EnumPrioridade prioridade,
            String endereco, String cpf, String telefone) {
        var pessoa = buscarObrigatoria(id);
        var novoCpf = new Cpf(cpf);
        validarCpfDisponivel(novoCpf.valor(), id);
        pessoa.editar(new Nome(nome), observacaoOpcional(observacao), prioridade,
                new Endereco(endereco), novoCpf, new Telefone(telefone));
        pessoaPorta.salvar(pessoa);
    }

    @Transactional(readOnly = true)
    public Pessoa buscar(UUID id) {
        return buscarObrigatoria(id);
    }

    @Transactional(readOnly = true)
    public ResultadoPaginado<Pessoa> listar(String nome, boolean arquivado, int pagina, int tamanho) {
        if (pagina < 0) {
            throw new DadosInvalidosException("A página não pode ser negativa");
        }
        if (tamanho < 1 || tamanho > 100) {
            throw new DadosInvalidosException("O tamanho da página deve estar entre 1 e 100");
        }
        return pessoaPorta.listar(nome == null ? "" : nome.trim(), arquivado, pagina, tamanho);
    }

    @Transactional
    public void arquivar(UUID id) {
        var pessoa = buscarObrigatoria(id);
        pessoa.arquivar();
        pessoaPorta.salvar(pessoa);
    }

    @Transactional
    public void desarquivar(UUID id) {
        var pessoa = buscarObrigatoria(id);
        pessoa.desarquivar();
        pessoaPorta.salvar(pessoa);
    }

    @Transactional
    public void deletar(UUID id) {
        buscarObrigatoria(id);
        pessoaPorta.deletar(id);
    }

    private Pessoa buscarObrigatoria(UUID id) {
        return pessoaPorta.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Pessoa não encontrada: " + id));
    }

    private void validarCpfDisponivel(String cpf, UUID ignorarId) {
        if (pessoaPorta.existeCpf(cpf, ignorarId)) {
            throw new EntidadeDuplicadaException("Já existe uma pessoa cadastrada com este CPF");
        }
    }

    private static Observacao observacaoOpcional(String observacao) {
        return observacao == null || observacao.isBlank() ? null : new Observacao(observacao);
    }
}
