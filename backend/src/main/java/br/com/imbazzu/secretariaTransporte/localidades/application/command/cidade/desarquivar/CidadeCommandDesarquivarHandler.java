package br.com.imbazzu.secretariaTransporte.localidades.application.command.cidade.desarquivar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CidadeCommandDesarquivarHandler {

    private final CidadeCommandPorta porta;

    public void handler(CidadeCommandDesarquivar command) {

        var cidade = porta.buscarPorId(command.id()).orElseThrow(
                () -> new RuntimeException("Cidade não encontrada"));
        cidade.desarquivar();
        porta.salvar(cidade);
    }
}
