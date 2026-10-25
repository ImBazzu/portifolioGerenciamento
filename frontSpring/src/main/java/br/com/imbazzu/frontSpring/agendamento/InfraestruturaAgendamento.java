package br.com.imbazzu.frontSpring.agendamento;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration 
public class InfraestruturaAgendamento {
    
    @Bean 
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    };
}
