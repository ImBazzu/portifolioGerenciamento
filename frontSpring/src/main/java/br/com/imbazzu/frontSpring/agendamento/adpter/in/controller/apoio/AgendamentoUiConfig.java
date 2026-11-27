package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import br.com.imbazzu.frontSpring.usuario.UsuarioApi;

@Configuration
public class AgendamentoUiConfig {

    @Bean
    MensagemErroApi mensagemErroApi(ObjectMapper objectMapper) {
        return new MensagemErroApi(objectMapper);
    }

    @Bean
    RespostaFlash respostaFlash(MensagemErroApi mensagemErro) {
        return new RespostaFlash(mensagemErro);
    }

    @Bean
    CarregadorOpcoesAgendamento carregadorOpcoesAgendamento(
            ListaApplicationPorta listas,
            VeiculoApplicationPorta tiposVeiculo,
            LocalidadeApplicationPorta localidades,
            UsuarioApi usuarios) {
        return new CarregadorOpcoesAgendamento(listas, tiposVeiculo, localidades, usuarios);
    }

    @Bean
    MontadorAgendaDia montadorAgendaDia() {
        return new MontadorAgendaDia();
    }

    @Bean
    MontadorDetalhesPassageiros montadorDetalhesPassageiros(PessoaApplicationPorta pessoas) {
        return new MontadorDetalhesPassageiros(pessoas);
    }
}
