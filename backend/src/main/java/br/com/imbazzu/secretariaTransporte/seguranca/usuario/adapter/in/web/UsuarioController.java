package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.in.web;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AtualizarTokenUseCase;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AutenticarUsuarioUseCase;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.UsuarioApplicationPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenRequestDto;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.UsuarioRequestDto;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class UsuarioController {

    private final AtualizarTokenUseCase atualizacaoService;

    private final AutenticarUsuarioUseCase autenticacaoService;

    private final UsuarioApplicationPorta usuarioCadastroService;

    public UsuarioController(AtualizarTokenUseCase atualizacaoService,
                             AutenticarUsuarioUseCase autenticarUsuarioUseCase,
                             UsuarioApplicationPorta usuarioCadastroService){
        this.atualizacaoService = atualizacaoService;
        this.usuarioCadastroService = usuarioCadastroService;
        this.autenticacaoService= autenticarUsuarioUseCase;
    }



    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid UsuarioRequestDto dto,
                                                  HttpServletRequest request) {
        var tokenRetorno=  autenticacaoService.autenticar(dto.login(), dto.senha(), request.getRemoteAddr());
        // adicionarTokenAtualizarNoCookie(response,tokenAtualizar);
        return ResponseEntity.ok().body(tokenRetorno);
    }

    @PostMapping("/atualizarToken")
    @PermitAll
    public ResponseEntity<TokenResponseDto> atualizar(@RequestBody @Valid TokenRequestDto tokenRequestDto) {
        var tokenRetorno = atualizacaoService.atualizarToken(tokenRequestDto.refreshToken());
        return ResponseEntity.ok().body(tokenRetorno);
    }

    @PostMapping("/registrar")
    @PermitAll
    public ResponseEntity<String> registrar(@RequestBody @Valid UsuarioRequestDto dto) {
        usuarioCadastroService.cadastrar(dto.login(),dto.senha(),"USUARIO");
        return ResponseEntity.ok().body("Usuario cadastrado com sucesso");
    }

    @PostMapping("/registrarAdm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registrarAdm(@RequestBody @Valid UsuarioRequestDto dto) {

        usuarioCadastroService.cadastrar(dto.login(),dto.senha(),"ADMIN");
        return ResponseEntity.ok().body("Administrador cadastrado com sucesso");
    }
}
