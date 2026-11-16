package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;

/** Endpoints que devolvem fragmentos HTML para as buscas (autocomplete) do modal de agendamento. */
@Controller
@RequestMapping("/agendamentos")
public class AgendamentoBuscaController {

    private static final int TERMO_MINIMO = 2;

    private final PessoaApplicationPorta pessoas;
    private final LocalidadeApplicationPorta localidades;

    public AgendamentoBuscaController(PessoaApplicationPorta pessoas, LocalidadeApplicationPorta localidades) {
        this.pessoas = pessoas;
        this.localidades = localidades;
    }

    @GetMapping("/buscar-pessoas")
    public String buscarPessoas(
            @RequestParam(defaultValue = "") String termo,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        var resultado = pessoas.listar(termo.strip(), false, pagina);

        model.addAttribute("pessoas", resultado.conteudo());
        model.addAttribute("temMais", pagina + 1 < resultado.totalPaginas());
        return "agendamento/fragmentos/resultados-pessoas :: resultados";
    }

    @GetMapping("/buscar-cidades")
    public String buscarCidades(
            @RequestParam(name = "termoCidade", defaultValue = "") String termo,
            Model model) {
        var termoLimpo = termo.strip();

        // Assume que o 1º parâmetro de listarCidades é o termo de busca (como em pessoas.listar).
        List<CidadeResponseDto> cidades = termoLimpo.length() < TERMO_MINIMO
                ? List.of()
                : localidades.listarCidades(termoLimpo, 0).conteudo();

        model.addAttribute("cidades", cidades);
        return "agendamento/fragmentos/resultados-busca :: cidades";
    }

    @GetMapping("/buscar-destinos")
    public String buscarDestinos(
            @RequestParam(name = "idCidade", required = false) UUID idCidade,
            @RequestParam(name = "termoDestino", defaultValue = "") String termo,
            Model model) {
        // Validação de servidor: sem cidade, não há destinos (o "disabled" do input não protege o endpoint).
        List<DestinoResponseDto> destinos = idCidade == null
                ? List.of()
                : localidades.listarDestinos(idCidade, termo.strip(), 0).conteudo();

        model.addAttribute("destinos", destinos);
        return "agendamento/fragmentos/resultados-busca :: destinos";
    }
}
