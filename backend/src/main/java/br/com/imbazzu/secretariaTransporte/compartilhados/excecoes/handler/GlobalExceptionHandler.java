package br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.handler;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeEmUsoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.ArquivoNaoCarregadoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.LoginBloqueadoException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.UsuarioNaoAutorizadoException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.UsuarioNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.UsuarioSemPermissaoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Entidade ----------

    @ExceptionHandler(EntidadeNaoEncontradoException.class)
    public ResponseEntity<ErroDto> entidadeNaoEncontrado(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.NOT_FOUND, e, request); // 404
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(EntidadeDuplicadaException.class)
    public ResponseEntity<ErroDto> entidadeDuplicada(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.CONFLICT, e, request); // 409
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(EntidadeEmUsoException.class)
    public ResponseEntity<ErroDto> entidadeEmUso(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.CONFLICT, e, request); // 409
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    // ---------- Regras de negócio ----------

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroDto> dadosInvalidos(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.BAD_REQUEST, e, request); // 400
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroDto> regraDeNegocioViolada(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.UNPROCESSABLE_CONTENT, e, request); // 422
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    public ResponseEntity<ErroDto> operacaoNaoPermitida(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.CONFLICT, e, request); // 409
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    // ---------- Autenticação / Autorização ----------
    //
    // @ExceptionHandler(TokenInvalidoException.class)
    // public ResponseEntity<ErroDto> tokenInvalido(Exception e, HttpServletRequest
    // request) {
    // var retorno = ErroMapper.toErroDto(HttpStatus.UNAUTHORIZED, e, request);
    // //401
    // return ResponseEntity.status(retorno.status()).body(retorno);
    // }

    @ExceptionHandler(UsuarioNaoAutorizadoException.class)
    public ResponseEntity<ErroDto> usuarioNaoAutorizado(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.UNAUTHORIZED, e, request); // 403
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(UsuarioSemPermissaoException.class)
    public ResponseEntity<ErroDto> usuarioSemPermissaoException(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.FORBIDDEN, e, request);
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroDto> usuarioNaoEncontrado(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.NOT_FOUND, e, request);
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(LoginBloqueadoException.class)
    public ResponseEntity<ErroDto> loginBloqueado(LoginBloqueadoException e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.TOO_MANY_REQUESTS, e, request);
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    // ---------- Infraestrutura ----------

    @ExceptionHandler(ArquivoNaoCarregadoException.class)
    public ResponseEntity<ErroDto> arquivoNaoCarregado(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.BAD_REQUEST, e, request); // 400
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    @ExceptionHandler(BancoDeDadosException.class)
    public ResponseEntity<ErroDto> bancoDeDados(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.INTERNAL_SERVER_ERROR, e, request); // 500
        return ResponseEntity.status(retorno.status()).body(retorno);
    }

    // ---------- Fallback ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDto> erroNaoMapeado(Exception e, HttpServletRequest request) {
        var retorno = ErroMapper.toErroDto(HttpStatus.INTERNAL_SERVER_ERROR, e, request); // 500
        return ResponseEntity.status(retorno.status()).body(retorno);
    }
}