package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.salvar;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Cidade;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.CidadeEstadoEnum;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.valueobject.TempoViagem;
import br.com.imbazzu.secretariaTransporte.localidades.core.excecoes.CidadeDuplicadoException;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CidadeCommandSalvarHandler {
    
    private final CidadeCommandPorta porta;
    private final CidadeQueryPorta queryPorta;

    @Transactional
    public UUID handler(CidadeCommandSalvar command) {
         var cidade = Cidade.criar(new Nome(command.nome()),
         CidadeEstadoEnum.valueOf(command.estado()),
          new TempoViagem(command.horas(), command.minutos()));
        var verificarDuplicidade = queryPorta.verificarCidadeCadastrada(cidade.getNome().valor(), cidade.getEstado());
        if(verificarDuplicidade) {
            throw new CidadeDuplicadoException(cidade.getEstado(), cidade.getNome());
        }

        return porta.salvar(cidade);
    }
}
