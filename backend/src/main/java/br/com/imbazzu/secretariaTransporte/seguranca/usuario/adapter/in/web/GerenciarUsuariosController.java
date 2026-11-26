package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.in.web;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.application.GerenciarUsuariosService;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;

@RestController
@RequestMapping("/usuarios")
public class GerenciarUsuariosController {
    public record CriarRequest(String nome, String senha, String perfil) {}
    public record PerfilRequest(String perfil) {}
    public record ProprioRequest(String nome, String senhaAtual, String novaSenha) {}
    public record Pagina(List<GerenciarUsuariosService.UsuarioResumo> content, int totalPages, int number) {}
    private final GerenciarUsuariosService service;

    public GerenciarUsuariosController(GerenciarUsuariosService service) {
        this.service = service;
    }

    @GetMapping
    public Pagina listar(@RequestParam(defaultValue = "") String nome, @RequestParam(defaultValue = "0") int pagina) {
        var resultado = service.listar(nome, pagina);
        return new Pagina(resultado.getContent(), resultado.getTotalPages(), resultado.getNumber());
    }

    @PostMapping
    public GerenciarUsuariosService.UsuarioResumo criar(@RequestBody CriarRequest dados) {
        return service.criar(dados.nome(), dados.senha(), dados.perfil());
    }

    @PutMapping("/{id}/perfil")
    public void alterarPerfil(@PathVariable UUID id, @RequestBody PerfilRequest dados) {
        service.alterarPerfil(id, dados.perfil());
    }

    @GetMapping("/eu")
    public GerenciarUsuariosService.UsuarioResumo proprio(Authentication auth) {
        return service.buscar((UUID) auth.getPrincipal());
    }

    @PutMapping("/eu")
    public TokenResponseDto atualizarProprio(Authentication auth, @RequestBody ProprioRequest dados) {
        return service.atualizarProprio((UUID) auth.getPrincipal(), dados.nome(), dados.senhaAtual(), dados.novaSenha());
    }
}
