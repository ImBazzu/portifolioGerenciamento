package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoApi agendamentoApi;
    private final PessoaApplicationPorta pessoas;
    private final ListaApplicationPorta listas;
    private final VeiculoApplicationPorta tiposVeiculo;
    private final LocalidadeApplicationPorta localidades;
    private final ObjectMapper objectMapper;

    public AgendamentoController(
            AgendamentoApi agendamentoApi,
            PessoaApplicationPorta pessoas,
            ListaApplicationPorta listas,
            VeiculoApplicationPorta tiposVeiculo,
            LocalidadeApplicationPorta localidades,
            ObjectMapper objectMapper) {
        this.agendamentoApi = agendamentoApi;
        this.pessoas = pessoas;
        this.listas = listas;
        this.tiposVeiculo = tiposVeiculo;
        this.localidades = localidades;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String inicio(@RequestParam(required = false) LocalDate data, Model model) {
        var dia = data == null ? LocalDate.now() : data;
        model.addAttribute("pagina", "agendamentos");
        model.addAttribute("data", dia);
        carregarOpcoes(model);
        var pessoaSelecionada = model.asMap().get("idPessoaSelecionada");
        if (pessoaSelecionada instanceof UUID idPessoa) {
            model.addAttribute("pessoaSelecionada", pessoas.buscar(idPessoa));
        }
        var destinoSelecionado = model.asMap().get("idDestinoSelecionado");
        var destinos = model.asMap().get("destinos");
        if (destinoSelecionado instanceof UUID idDestino && destinos instanceof List<?> opcoes) {
            opcoes.stream()
                    .filter(DestinoView.class::isInstance)
                    .map(DestinoView.class::cast)
                    .filter(destino -> destino.id().equals(idDestino))
                    .findFirst()
                    .ifPresent(destino -> model.addAttribute("idCidadeSelecionada", destino.idCidade()));
        }
        try {
            var agenda = agendamentoApi.buscarDia(dia);
            model.addAttribute("agenda", agenda);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                model.addAttribute("agenda", null);
            } else {
                model.addAttribute("erroConsultaAgenda", true);
                model.addAttribute("erro", mensagemErro(e));
            }
        }
        return "agendamento/inicio";
    }

    @GetMapping("/buscar-pessoas")
    @ResponseBody
    public PessoaBuscaResponse buscarPessoas(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina) {
        if (termo.isBlank()) {
            return new PessoaBuscaResponse(List.of(), false, 0);
        }
        var resultado = pessoas.listar(termo.trim(), false, pagina);
        var opcoes = resultado.conteudo().stream()
                .map(pessoa -> new PessoaBuscaOption(
                        pessoa.id(), pessoa.nome(), pessoa.prioridade().name(), pessoa.cpf(), pessoa.telefone()))
                .toList();
        return new PessoaBuscaResponse(
                opcoes, pagina + 1 < resultado.totalPaginas(), resultado.paginaAtual());
    }

    @PostMapping("/dias/abrir")
    public String abrirDia(@RequestParam LocalDate data, RedirectAttributes redirect) {
        try {
            agendamentoApi.abrirDia(data);
            redirect.addFlashAttribute("sucesso", "Agenda de %s aberta.".formatted(data));
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
        }
        return redirect(data);
    }

    @PostMapping("/veiculos")
    public String abrirVeiculo(
            @RequestParam LocalDate data,
            @RequestParam UUID idTipoVeiculo,
            @RequestParam(required = false) String identificacao,
            @RequestParam(required = false) LocalTime horarioSaida,
            RedirectAttributes redirect) {
        try {
            agendamentoApi.abrirVeiculo(data,
                    new AbrirVeiculoRequestDto(idTipoVeiculo, identificacao, horarioSaida));
            redirect.addFlashAttribute("sucesso", "Veículo aberto para a agenda do dia.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
        }
        return redirect(data);
    }

    @PostMapping("/verificar-veiculos")
    public String verificarVeiculos(
            @RequestParam LocalDate data,
            @RequestParam UUID idPessoa,
            @RequestParam UUID idLista,
            @RequestParam UUID idDestino,
            @RequestParam LocalTime horarioChegada,
            @RequestParam(defaultValue = "0") int acompanhantes,
            RedirectAttributes redirect) {
        redirect.addFlashAttribute("idPessoaSelecionada", idPessoa);
        redirect.addFlashAttribute("idListaSelecionada", idLista);
        redirect.addFlashAttribute("idDestinoSelecionado", idDestino);
        redirect.addFlashAttribute("horarioChegadaSelecionado", horarioChegada);
        redirect.addFlashAttribute("acompanhantesSelecionados", acompanhantes);
        try {
            var resposta = agendamentoApi.verificarVeiculos(data, new VerificarVeiculosRequestDto(
                    idPessoa, idLista, idDestino, horarioChegada, acompanhantes));
            redirect.addFlashAttribute("verificacao", resposta);
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
            manterDadosAgendamento(redirect, idPessoa, idLista, idDestino, horarioChegada, acompanhantes);
        }
        return redirect(data);
    }

    @PostMapping("/passageiros")
    public String criar(
            @RequestParam LocalDate data,
            @RequestParam UUID idPessoa,
            @RequestParam UUID idLista,
            @RequestParam UUID idRegraCadastro,
            @RequestParam UUID idTipoVeiculo,
            @RequestParam UUID idDestino,
            @RequestParam LocalTime horarioChegada,
            @RequestParam(defaultValue = "false") boolean buscarNaResidencia,
            @RequestParam(defaultValue = "0") int acompanhantes,
            RedirectAttributes redirect) {
        try {
            agendamentoApi.criar(data, new AgendamentoRequestDto(
                    idPessoa, idLista, idRegraCadastro, idTipoVeiculo, idDestino,
                    horarioChegada, buscarNaResidencia, acompanhantes));
            redirect.addFlashAttribute("sucesso", "Passageiro agendado.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
            manterDadosAgendamento(redirect, idPessoa, idLista, idDestino, horarioChegada, acompanhantes);
        }
        return redirect(data);
    }

    @PostMapping("/dias/{data}/separacao-automatica")
    public String separarAutomaticamente(@PathVariable LocalDate data, RedirectAttributes redirect) {
        try {
            var resultado = agendamentoApi.separarAutomaticamente(data);
            redirect.addFlashAttribute("sucesso",
                    "%d passageiro(s) alocado(s). Pendentes: %d."
                            .formatted(resultado.passageirosAlocados(), resultado.passageirosPendentes().size()));
            if (!resultado.passageirosPendentes().isEmpty()) {
                redirect.addFlashAttribute("pendencias", resultado.passageirosPendentes());
            }
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
        }
        return redirect(data);
    }

    @PostMapping("/{idAgendamento}/veiculo")
    public String atribuirVeiculo(
            @PathVariable UUID idAgendamento,
            @RequestParam UUID idVeiculo,
            @RequestParam LocalDate data,
            RedirectAttributes redirect) {
        try {
            agendamentoApi.atribuir(idAgendamento, idVeiculo);
            redirect.addFlashAttribute("sucesso", "Passageiro atribuído ao veículo.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
        }
        return redirect(data);
    }

    @PostMapping("/{idAgendamento}/remover-veiculo")
    public String removerVeiculo(
            @PathVariable UUID idAgendamento,
            @RequestParam LocalDate data,
            RedirectAttributes redirect) {
        try {
            agendamentoApi.removerDoVeiculo(idAgendamento);
            redirect.addFlashAttribute("sucesso", "Passageiro removido do veículo.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
        }
        return redirect(data);
    }

    @PostMapping("/{idAgendamento}/excluir")
    public String excluir(
            @PathVariable UUID idAgendamento,
            @RequestParam LocalDate data,
            RedirectAttributes redirect) {
        try {
            agendamentoApi.remover(idAgendamento);
            redirect.addFlashAttribute("sucesso", "Agendamento excluído.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro(e));
        }
        return redirect(data);
    }

    private void carregarOpcoes(Model model) {
        model.addAttribute("listas", listarListasAtivas());
        model.addAttribute("tiposVeiculo", listarTiposAtivos());
        model.addAttribute("cidades", listarCidadesAtivas());
        model.addAttribute("destinos", listarDestinosAtivos());
    }

    private void manterDadosAgendamento(
            RedirectAttributes redirect,
            UUID idPessoa,
            UUID idLista,
            UUID idDestino,
            LocalTime horarioChegada,
            int acompanhantes) {
        redirect.addFlashAttribute("idPessoaSelecionada", idPessoa);
        redirect.addFlashAttribute("idListaSelecionada", idLista);
        redirect.addFlashAttribute("idDestinoSelecionado", idDestino);
        redirect.addFlashAttribute("horarioChegadaSelecionado", horarioChegada);
        redirect.addFlashAttribute("acompanhantesSelecionados", acompanhantes);
    }

    private List<ListaResponseDto> listarListasAtivas() {
        var primeira = listas.listarAtivas("", 0);
        var todos = new ArrayList<ListaResponseDto>();
        adicionarDetalhesLista(todos, primeira.conteudo());
        for (int pagina = 1; pagina < primeira.totalPaginas(); pagina++) {
            adicionarDetalhesLista(todos, listas.listarAtivas("", pagina).conteudo());
        }
        return todos;
    }

    private void adicionarDetalhesLista(List<ListaResponseDto> destino, List<ListaResponseDto> resumos) {
        resumos.stream()
                .filter(lista -> !lista.arquivado())
                .map(lista -> listas.buscarAtiva(lista.id()))
                .forEach(destino::add);
    }

    private List<TipoVeiculoResponseDto> listarTiposAtivos() {
        var primeira = tiposVeiculo.listar("", 0);
        var todos = new ArrayList<>(primeira.conteudo());
        for (int pagina = 1; pagina < primeira.totalPaginas(); pagina++) {
            todos.addAll(tiposVeiculo.listar("", pagina).conteudo());
        }
        return todos;
    }

    private List<CidadeResponseDto> listarCidadesAtivas() {
        var primeira = localidades.listarCidades("", 0);
        var todas = new ArrayList<>(primeira.conteudo());
        for (int pagina = 1; pagina < primeira.totalPaginas(); pagina++) {
            todas.addAll(localidades.listarCidades("", pagina).conteudo());
        }
        return todas;
    }

    private List<DestinoView> listarDestinosAtivos() {
        var destinos = new ArrayList<DestinoView>();
        for (var cidade : listarCidadesAtivas()) {
            adicionarDestinos(destinos, cidade, localidades.listarDestinos(cidade.id(), "", 0));
        }
        return destinos;
    }

    private void adicionarDestinos(
            List<DestinoView> destino,
            CidadeResponseDto cidade,
            ResultadoPaginado<DestinoResponseDto> primeira) {
        primeira.conteudo().stream()
                .map(item -> new DestinoView(item.id(), cidade.id(), cidade.nome(), item.nome()))
                .forEach(destino::add);
        for (int pagina = 1; pagina < primeira.totalPaginas(); pagina++) {
            localidades.listarDestinos(cidade.id(), "", pagina).conteudo().stream()
                    .map(item -> new DestinoView(item.id(), cidade.id(), cidade.nome(), item.nome()))
                    .forEach(destino::add);
        }
    }

    private String redirect(LocalDate data) {
        return "redirect:/agendamentos?data=" + data;
    }

    private String mensagemErro(RestClientResponseException e) {
        try {
            var json = objectMapper.readTree(e.getResponseBodyAsString());
            for (var campo : List.of("mensagem", "message", "detail", "erro")) {
                if (json.hasNonNull(campo)) {
                    return json.get(campo).asText();
                }
            }
        } catch (JsonProcessingException ignored) {
            return "A API de agendamentos retornou HTTP %d.".formatted(e.getStatusCode().value());
        }
        return "A API de agendamentos retornou HTTP %d.".formatted(e.getStatusCode().value());
    }

    public record DestinoView(UUID id, UUID idCidade, String cidade, String nome) {
    }

    public record PessoaBuscaOption(UUID id, String nome, String prioridade, String cpf, String telefone) {
    }

    public record PessoaBuscaResponse(List<PessoaBuscaOption> pessoas, boolean temMais, int pagina) {
    }
}
