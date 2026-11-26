package br.com.imbazzu.frontSpring.motorista;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.AgendamentoRedirect;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;

@Controller
@RequestMapping("/motoristas")
public class MotoristaPaginaController {
    private final MotoristaApi api;
    private final MensagemErroApi erro;

    public MotoristaPaginaController(MotoristaApi api, MensagemErroApi erro) {
        this.api = api;
        this.erro = erro;
    }

    @GetMapping
    public String inicio(@RequestParam(defaultValue = "") String nome,
            @RequestParam(required = false) UUID categoria,
            @RequestParam(defaultValue = "0") int pagina, Model model) {
        preencher(model, nome, categoria, pagina);
        return "motorista/inicio";
    }

    @GetMapping("/selecionar/{veiculo}")
    public String selecionar(@PathVariable UUID veiculo, @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean carros,
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(required = false) UUID categoria,
            @RequestParam(defaultValue = "0") int pagina, Model model) {
        preencher(model, nome, categoria, pagina);
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("data", data);
        model.addAttribute("carros", carros);
        return "motorista/selecionar";
    }

    private void preencher(Model model, String nome, UUID categoria, int pagina) {
        model.addAttribute("pagina", "motoristas");
        model.addAttribute("nome", nome);
        model.addAttribute("categoriaSelecionada", categoria);
        model.addAttribute("categorias", api.categorias());
        model.addAttribute("resultado", api.buscar(nome, categoria, pagina));
    }

    @PostMapping("/categorias")
    public String categoria(@RequestParam(required = false) UUID id, @RequestParam String nome,
            RedirectAttributes redirect) {
        try {
            api.salvarCategoria(id, nome);
            redirect.addFlashAttribute("sucesso", "Categoria salva.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
            if (id == null) redirect.addFlashAttribute("categoriaNova", nome);
        }
        return "redirect:/motoristas";
    }

    @PostMapping
    public String salvar(@RequestParam(required = false) UUID id, @RequestParam String nome,
            @RequestParam String telefone, @RequestParam UUID categoriaId, RedirectAttributes redirect) {
        try {
            api.salvar(id, nome, telefone, categoriaId);
            redirect.addFlashAttribute("sucesso", "Motorista salvo.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
            if (id == null) {
                redirect.addFlashAttribute("novoNome", nome);
                redirect.addFlashAttribute("novoTelefone", telefone);
                redirect.addFlashAttribute("novaCategoria", categoriaId);
            }
        }
        return "redirect:/motoristas";
    }

    @PostMapping("/selecionar/{veiculo}")
    public String atribuir(@PathVariable UUID veiculo, @RequestParam LocalDate data,
            @RequestParam(defaultValue = "false") boolean carros,
            @RequestParam(required = false) UUID motoristaId, RedirectAttributes redirect) {
        try {
            api.atribuir(veiculo, motoristaId);
            redirect.addFlashAttribute("sucesso", motoristaId == null ? "Motorista retirado." : "Motorista selecionado.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", erro.de(e));
        }
        return AgendamentoRedirect.retorno(carros, data);
    }
}
