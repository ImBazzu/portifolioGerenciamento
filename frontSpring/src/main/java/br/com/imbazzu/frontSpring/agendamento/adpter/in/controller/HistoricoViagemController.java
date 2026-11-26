package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.imbazzu.frontSpring.agendamento.core.porta.in.HistoricoViagemApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

@Controller
@RequestMapping("/pessoas")
public class HistoricoViagemController {

    private final HistoricoViagemApplicationPorta historicoViagem;
    public HistoricoViagemController(HistoricoViagemApplicationPorta historicoViagem) {
        this.historicoViagem = historicoViagem;
    }

    @HxRequest
    @GetMapping("/{id}/historico-viagens")
    public String historico(@PathVariable UUID id, 
                           @RequestParam(defaultValue = "0") int pagina,
                           @RequestParam(defaultValue = "false") boolean contextoAgendamento,
                           Model model) {
        var historicoResult = historicoViagem.buscarHistoricoViagens(id, pagina);
        model.addAttribute("historicoViagens", historicoResult.conteudo());
        model.addAttribute("paginaAtual", historicoResult.paginaAtual());
        model.addAttribute("totalPaginas", historicoResult.totalPaginas());
        model.addAttribute("pessoaId", id);
        model.addAttribute("destinoBusca", "");
        model.addAttribute("contextoAgendamento", contextoAgendamento);
        return "paginas/pessoa/tabelaHistoricoViagens :: tabelaHistoricoViagens";
    }

    @HxRequest
    @GetMapping("/{id}/historico-viagens/buscar")
    public String historicoComBusca(@PathVariable UUID id,
                                   @RequestParam(defaultValue = "") String destinoBusca,
                                   @RequestParam(defaultValue = "0") int pagina,
                                   @RequestParam(defaultValue = "false") boolean contextoAgendamento,
                                   Model model) {
        var historicoResult = historicoViagem.buscarHistoricoViagensComFiltro(id, destinoBusca, pagina);
        model.addAttribute("historicoViagens", historicoResult.conteudo());
        model.addAttribute("paginaAtual", historicoResult.paginaAtual());
        model.addAttribute("totalPaginas", historicoResult.totalPaginas());
        model.addAttribute("pessoaId", id);
        model.addAttribute("destinoBusca", destinoBusca);
        model.addAttribute("contextoAgendamento", contextoAgendamento);
        return "paginas/pessoa/tabelaHistoricoViagens :: tabelaHistoricoViagens";
    }
}
