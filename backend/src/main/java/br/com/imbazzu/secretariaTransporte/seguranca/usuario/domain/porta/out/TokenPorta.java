package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model.Usuario;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.DadosTokenDto;


public interface TokenPorta {

    String gerarTokenAcesso(Usuario usuario);

    String gerarTokenAtualizacao(Usuario usuario);

    String extrairDadosDoTokenAtualizacao(String token);

    DadosTokenDto extrairDadosDoTokenAcesso(String token);

}
