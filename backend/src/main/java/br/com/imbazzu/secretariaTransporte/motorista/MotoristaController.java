package br.com.imbazzu.secretariaTransporte.motorista;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/motoristas")
public class MotoristaController {
    public record CategoriaRequest(String nome) {}
    public record MotoristaRequest(String nome, String telefone, UUID categoriaId) {}
    public record AtribuicaoRequest(UUID motoristaId) {}
    public record Pagina(List<MotoristaService.Motorista> content, int totalPages, int number) {}
    private final MotoristaService service;

    public MotoristaController(MotoristaService service) { this.service = service; }

    @GetMapping("/categorias")
    public List<MotoristaService.Categoria> categorias(@RequestParam(defaultValue = "false") boolean arquivadas) { return service.categorias(arquivadas); }

    @PostMapping("/categorias")
    public MotoristaService.Categoria criarCategoria(@RequestBody CategoriaRequest request) {
        return service.salvarCategoria(null, request.nome());
    }

    @PutMapping("/categorias/{id}")
    public MotoristaService.Categoria editarCategoria(@PathVariable UUID id, @RequestBody CategoriaRequest request) {
        return service.salvarCategoria(id, request.nome());
    }

    @PatchMapping("/categorias/{id}/arquivar")
    public void arquivarCategoria(@PathVariable UUID id) { service.arquivarCategoria(id, true); }
    @PatchMapping("/categorias/{id}/reativar")
    public void reativarCategoria(@PathVariable UUID id) { service.arquivarCategoria(id, false); }
    @DeleteMapping("/categorias/{id}")
    public void excluirCategoria(@PathVariable UUID id) { service.excluirCategoria(id); }

    @GetMapping
    public Pagina buscar(@RequestParam(defaultValue = "") String nome,
            @RequestParam(required = false) UUID categoria, @RequestParam(defaultValue = "false") boolean arquivados,
            @RequestParam(defaultValue = "0") int pagina) {
        var resultado = service.buscar(nome, categoria, arquivados, pagina);
        return new Pagina(resultado.getContent(), resultado.getTotalPages(), resultado.getNumber());
    }

    @PostMapping
    public MotoristaService.Motorista criar(@RequestBody MotoristaRequest request) {
        return service.salvar(null, request.nome(), request.telefone(), request.categoriaId());
    }

    @PutMapping("/{id}")
    public MotoristaService.Motorista editar(@PathVariable UUID id, @RequestBody MotoristaRequest request) {
        return service.salvar(id, request.nome(), request.telefone(), request.categoriaId());
    }

    @PatchMapping("/{id}/arquivar")
    public void arquivar(@PathVariable UUID id) { service.arquivarMotorista(id, true); }
    @PatchMapping("/{id}/reativar")
    public void reativar(@PathVariable UUID id) { service.arquivarMotorista(id, false); }
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable UUID id) { service.excluirMotorista(id); }

    @GetMapping("/atribuicoes")
    public List<MotoristaService.Atribuicao> atribuicoes(@RequestParam LocalDate data) {
        return service.atribuicoes(data);
    }

    @PutMapping("/veiculos/{id}")
    public void atribuir(@PathVariable UUID id, @RequestBody AtribuicaoRequest request) {
        service.atribuir(id, request.motoristaId());
    }
}
