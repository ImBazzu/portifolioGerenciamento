package br.com.imbazzu.secretariaTransporte.cadastrais.infraestrutura;

import br.com.imbazzu.secretariaTransporte.cadastrais.application.*;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.*;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBeanApplication {

    @Bean
    public PessoaApplicationPorta pessoaApplicationPorta(PessoaRepositorioPorta pessoaRepositorioPorta) {
        return new PessoaApplication(pessoaRepositorioPorta);
    }

    @Bean
    public PessoaCondicaoApplicationPorta pessoaCondicaoApplicationPorta(
            PessoaCondicaoRepositorioPorta repositorioPessoaCondicao) {
        return new PessoaCondicaoApplication(repositorioPessoaCondicao);
    }

}
