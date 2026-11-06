package br.com.imbazzu.secretariaTransporte.tipoVeiculo.infraestrutura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.jpaAdapter.TipoVeiculoCommandAdapter;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.jpaAdapter.TipoVeiculoQueryAdapter;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoCommandRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.TipoVeiculoQueryRepository;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceEditarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceSalvarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceValidarExclusao;

@Configuration 
public class TipoVeiculoInfraestrutura {
    
    @Bean 
    public TipoVeiculoCommandPorta tipoVeiculoCommandPorta(TipoVeiculoCommandRepository repo) {
        return new TipoVeiculoCommandAdapter(repo);
    }

    @Bean 
    public TipoVeiculoQueryPorta tipoVeiculoQueryPorta(TipoVeiculoQueryRepository repo) {
        return new TipoVeiculoQueryAdapter(repo);
    }

    @Bean
    public TipoVeiculoServiceBuscarEntidade tipoVeiculoServiceBuscarEntidade(TipoVeiculoQueryPorta repo) {
        return new TipoVeiculoServiceBuscarEntidade(repo);
    }

    @Bean 
    public TipoVeiculoServiceEditarEntidade tipoVeiculoServiceEditarEntidade(TipoVeiculoQueryPorta repo) {
        return new TipoVeiculoServiceEditarEntidade(repo);
    
    }

    @Bean
    public TipoVeiculoServiceSalvarEntidade tipoVServiceSalvarEntidade(TipoVeiculoQueryPorta repo) {
        return new TipoVeiculoServiceSalvarEntidade(repo);
    }

    @Bean
    public TipoVeiculoServiceValidarExclusao tipoValidarExclusao(TipoVeiculoQueryPorta repo) {
        return new TipoVeiculoServiceValidarExclusao(repo);
    }
}

