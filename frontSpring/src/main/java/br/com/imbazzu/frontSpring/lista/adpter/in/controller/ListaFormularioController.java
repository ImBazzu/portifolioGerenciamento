package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/listas")
public class ListaFormularioController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private final ListaApplicationPorta application;
    private final LocalidadeApplicationPorta localidade;
    private final VeiculoApplicationPorta veiculos;

    public ListaFormularioController(
            ListaApplicationPorta application,
            LocalidadeApplicationPorta localidade,
            VeiculoApplicationPorta veiculos) {
        this.application = application;
        this.localidade = localidade;
        this.veiculos = veiculos;
    }

    @HxRequest
    @GetMapping("/abrirFormulario")
    public String formNovo(Model model) {
        model.addAttribute("id", null);
        model.addAttribute("lista", new ListaRequestDto("", ""));
        model.addAttribute("regrasCadastro", List.of());
        model.addAttribute("regrasSeparacao", List.of());
        adicionarOpcoes(model);
        return "lista/formLista :: formLista";
    }

    @HxRequest
    @GetMapping("/abrirFormulario/{id}")
    public String formEditar(@PathVariable UUID id, Model model) {
        var lista = application.buscarAtiva(id);
        model.addAttribute("id", id);
        model.addAttribute("lista", new ListaRequestDto(lista.nome(), lista.descricao()));
        model.addAttribute("regrasCadastro", lista.regrasCadastroDados());
        model.addAttribute("regrasSeparacao", lista.regrasSeparacaoDados());
        adicionarOpcoes(model);
        return "lista/formLista :: formLista";
    }

    @HxRequest
    @PostMapping("/salvar")
    @HxTriggerAfterSettle("listar-ativos")
    public String salvar(
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam String regrasJson) throws JsonProcessingException {
        ListaRegrasRequestDto regras = lerRegras(regrasJson);
        UUID id = application.salvar(new ListaRequestDto(nome, descricao));
        application.atualizarRegras(id, regras);
        return "layout/vazio :: vazio";
    }

    @HxRequest
    @PutMapping("/{id}")
    @HxTriggerAfterSettle("listar-ativos")
    public String editar(
            @PathVariable UUID id,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam String regrasJson) throws JsonProcessingException {
        ListaRegrasRequestDto regras = lerRegras(regrasJson);
        application.editar(id, new ListaRequestDto(nome, descricao));
        application.atualizarRegras(id, regras);
        return "layout/vazio :: vazio";
    }

    private ListaRegrasRequestDto lerRegras(String regrasJson) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(regrasJson, ListaRegrasRequestDto.class);
    }

    private void adicionarOpcoes(Model model) {
        model.addAttribute("opcoesVeiculo", listarVeiculos());
        model.addAttribute("opcoesCidade", listarCidades());
        model.addAttribute("prioridades", List.of("BAIXA", "MODERADA", "ALTA", "URGENTE"));
    }

    private List<TipoVeiculoResponseDto> listarVeiculos() {
        var resultado = veiculos.listar("", 0);
        var todos = new ArrayList<>(resultado.conteudo());
        for (int pagina = 1; pagina < resultado.totalPaginas(); pagina++) {
            todos.addAll(veiculos.listar("", pagina).conteudo());
        }
        return todos;
    }

    private List<CidadeResponseDto> listarCidades() {
        ResultadoPaginado<CidadeResponseDto> resultado = localidade.listarCidades("", 0);
        var todas = new ArrayList<>(resultado.conteudo());
        for (int pagina = 1; pagina < resultado.totalPaginas(); pagina++) {
            todas.addAll(localidade.listarCidades("", pagina).conteudo());
        }
        return todas;
    }
}
