package br.com.imbazzu.secretariaTransporte.motorista;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.repositorio.VeiculoAgendaRepository;

@Service
public class MotoristaService {
    public record Categoria(UUID id, String nome) {}
    public record Motorista(UUID id, String nome, String telefone, UUID categoriaId, String categoriaNome) {}
    public record Atribuicao(UUID veiculoId, UUID motoristaId, String nome) {}
    private final MotoristaRepository motoristas;
    private final CategoriaMotoristaRepository categorias;
    private final VeiculoAgendaRepository veiculos;

    public MotoristaService(MotoristaRepository motoristas, CategoriaMotoristaRepository categorias,
                            VeiculoAgendaRepository veiculos) {
        this.motoristas = motoristas;
        this.categorias = categorias;
        this.veiculos = veiculos;
    }

    @Transactional(readOnly = true)
    public List<Categoria> categorias() {
        return categorias.findAllByOrderByNomeAsc().stream().map(c -> new Categoria(c.getId(), c.getNome())).toList();
    }

    @Transactional
    public Categoria salvarCategoria(UUID id, String nome) {
        nome = validarNome(nome, 100);
        UUID atual = id == null ? UUID.randomUUID() : id;
        if (categorias.existsByNomeIgnoreCaseAndIdNot(nome, atual)) {
            throw new EntidadeDuplicadaException("Já existe uma categoria com este nome.");
        }
        var categoria = id == null ? new CategoriaMotoristaJpa() : categorias.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Categoria não encontrada."));
        categoria.setId(atual);
        categoria.setNome(nome);
        try {
            categorias.saveAndFlush(categoria);
        } catch (DataIntegrityViolationException e) {
            if (causadaPor(e, "uk_categoria_motorista_nome")) {
                throw new EntidadeDuplicadaException("Já existe uma categoria com este nome.");
            }
            throw e;
        }
        return new Categoria(atual, nome);
    }

    @Transactional(readOnly = true)
    public Page<Motorista> buscar(String nome, UUID categoria, int pagina) {
        if (pagina < 0) throw new RegraDeNegocioException("Página inválida.");
        return motoristas.buscar(nome == null ? "" : nome.trim(), categoria, PageRequest.of(pagina, 20))
                .map(MotoristaService::resposta);
    }

    @Transactional
    public Motorista salvar(UUID id, String nome, String telefone, UUID categoriaId) {
        nome = validarNome(nome, 150);
        if (telefone == null) throw new RegraDeNegocioException("Telefone é obrigatório.");
        String telefoneNormalizado;
        try {
            telefoneNormalizado = new Telefone(telefone).valor();
        } catch (DadosInvalidosException e) {
            throw new RegraDeNegocioException(e.getMessage());
        }
        if (categoriaId == null) throw new RegraDeNegocioException("Categoria é obrigatória.");
        var categoria = categorias.findById(categoriaId)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Categoria não encontrada."));
        UUID atual = id == null ? UUID.randomUUID() : id;
        if (motoristas.existsByTelefoneAndIdNot(telefoneNormalizado, atual)) {
            throw new EntidadeDuplicadaException("Já existe um motorista com este telefone.");
        }
        var motorista = id == null ? new MotoristaJpa() : motoristas.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Motorista não encontrado."));
        motorista.setId(atual);
        motorista.setNome(nome);
        motorista.setTelefone(telefoneNormalizado);
        motorista.setCategoria(categoria);
        try {
            motoristas.saveAndFlush(motorista);
        } catch (DataIntegrityViolationException e) {
            if (causadaPor(e, "uk_motorista_telefone")) {
                throw new EntidadeDuplicadaException("Já existe um motorista com este telefone.");
            }
            throw e;
        }
        return resposta(motorista);
    }

    @Transactional(readOnly = true)
    public List<Atribuicao> atribuicoes(LocalDate data) {
        var atribuidos = veiculos.findByDataOrderById(data).stream()
                .filter(v -> v.getIdMotorista() != null).toList();
        var nomes = motoristas.findAllById(atribuidos.stream().map(v -> v.getIdMotorista()).toList())
                .stream().collect(Collectors.toMap(MotoristaJpa::getId, Function.identity()));
        return atribuidos.stream()
                .map(v -> {
                    var m = nomes.get(v.getIdMotorista());
                    if (m == null) throw new EntidadeNaoEncontradoException("Motorista atribuído não encontrado.");
                    return new Atribuicao(v.getId(), m.getId(), m.getNome());
                }).toList();
    }

    @Transactional
    public void atribuir(UUID veiculoId, UUID motoristaId) {
        if (motoristaId != null && !motoristas.existsById(motoristaId)) {
            throw new EntidadeNaoEncontradoException("Motorista não encontrado.");
        }
        if (veiculos.atribuirMotorista(veiculoId, motoristaId) == 0) {
            throw new EntidadeNaoEncontradoException("Veículo não encontrado.");
        }
    }

    private static Motorista resposta(MotoristaJpa m) {
        return new Motorista(m.getId(), m.getNome(), m.getTelefone(), m.getCategoria().getId(), m.getCategoria().getNome());
    }

    private static String validarNome(String nome, int maximo) {
        if (nome == null || nome.isBlank() || nome.trim().length() > maximo) {
            throw new RegraDeNegocioException("Informe um nome com até " + maximo + " caracteres.");
        }
        return nome.trim();
    }

    private static boolean causadaPor(Throwable e, String constraint) {
        for (Throwable causa = e; causa != null; causa = causa.getCause()) {
            if (causa.getMessage() != null && causa.getMessage().contains(constraint)) return true;
        }
        return false;
    }
}
