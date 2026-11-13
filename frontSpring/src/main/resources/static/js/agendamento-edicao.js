/**
 * Comportamento do modal "Agendar passageiro" (página de agendamentos):
 *  - busca de pessoa (servidor: GET /agendamentos/buscar-pessoas)
 *  - busca de cidade e de destino (filtro no navegador sobre as listas que o
 *    Thymeleaf já renderizou em #cidadeResultados e #destinoResultados)
 *  - botão "Cadastrar destino"
 *  - alternância entre o formulário e o resultado da verificação
 *  - campos "tipo aprovado" e "veículo físico"
 *  - reabertura automática do modal quando o servidor devolve a página com
 *    pessoa/destino pré-selecionados (flash attributes) ou com uma verificação
 *
 * Tudo usa delegação de eventos no document: funciona quando a página chega via
 * htmx (DOMContentLoaded não dispara de novo) e não depende de os elementos já
 * existirem quando o script executa.
 */
(() => {
    if (window.__agendamentoModalInit) return; // evita registrar listeners em dobro
    window.__agendamentoModalInit = true;

    const MIN_CARACTERES_PESSOA = 2;
    const ATRASO_MS = 300;

    let temporizador = null;
    let requisicao = null;

    const $ = (id) => document.getElementById(id);
    const ehElemento = (e, seletor) => e.target instanceof Element && e.target.matches(seletor);
    const normalizar = (texto) =>
        (texto ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
    const mensagem = (id, texto) => {
        const el = $(id);
        if (el) el.textContent = texto;
    };
    const ocultar = (id) => {
        const el = $(id);
        if (el) el.hidden = true;
    };

    /* ------------------------------------------------------------------ */
    /* Pessoa                                                             */
    /* ------------------------------------------------------------------ */

    const limparResultadosPessoa = () => $("pessoaResultados")?.replaceChildren();

    function limparSelecaoPessoa() {
        const id = $("idPessoaAgendamento");
        if (id) id.value = "";
        const selecionada = $("pessoaSelecionada");
        if (selecionada) {
            selecionada.textContent = "";
            selecionada.classList.add("d-none");
        }
    }

    function selecionarPessoa({ id, nome, prioridade }) {
        $("idPessoaAgendamento").value = id;
        $("pessoaBusca").value = nome;

        const selecionada = $("pessoaSelecionada");
        selecionada.textContent = `${nome} — ${prioridade}`;
        selecionada.classList.remove("d-none");

        mensagem("pessoaBuscaMensagem", "Pessoa selecionada.");
        limparResultadosPessoa();
    }

    function renderizarPessoas(pessoas, temMais) {
        const lista = $("pessoaResultados");
        lista.replaceChildren();

        if (pessoas.length === 0) {
            mensagem("pessoaBuscaMensagem", "Nenhuma pessoa encontrada.");
            return;
        }

        pessoas.forEach((p) => {
            const botao = document.createElement("button");
            botao.type = "button";
            botao.className = "list-group-item list-group-item-action";
            botao.setAttribute("role", "option");
            botao.dataset.id = p.id;
            botao.dataset.nome = p.nome;
            botao.dataset.prioridade = p.prioridade;

            // textContent (e não innerHTML): nomes vêm do banco e não podem virar HTML
            const nome = document.createElement("strong");
            nome.textContent = p.nome;

            const detalhes = document.createElement("span");
            detalhes.className = "text-secondary small ms-2";
            detalhes.textContent = [p.cpf, p.telefone].filter(Boolean).join(" · ");

            botao.append(nome, detalhes);
            lista.append(botao);
        });

        mensagem(
            "pessoaBuscaMensagem",
            temMais ? "Há mais resultados. Refine a busca." : "Selecione uma pessoa da lista."
        );
    }

    async function buscarPessoas(termo) {
        const input = $("pessoaBusca");
        if (!input) return;

        requisicao?.abort(); // descarta a resposta de uma digitação anterior
        requisicao = new AbortController();

        const url = new URL(input.dataset.url, window.location.origin);
        url.searchParams.set("termo", termo);

        try {
            const resposta = await fetch(url, {
                signal: requisicao.signal,
                headers: { Accept: "application/json" },
            });
            if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`);
            const dados = await resposta.json();
            renderizarPessoas(dados.pessoas, dados.temMais);
        } catch (erro) {
            if (erro.name === "AbortError") return;
            limparResultadosPessoa();
            mensagem("pessoaBuscaMensagem", "Não foi possível buscar pessoas. Tente novamente.");
            console.error("Busca de pessoas falhou:", erro);
        }
    }

    function aoDigitarPessoa(input) {
        clearTimeout(temporizador);
        limparSelecaoPessoa(); // digitar invalida a pessoa escolhida antes

        const termo = input.value.trim();
        if (termo.length < MIN_CARACTERES_PESSOA) {
            requisicao?.abort();
            limparResultadosPessoa();
            mensagem("pessoaBuscaMensagem", "Pesquise e selecione uma pessoa para continuar.");
            return;
        }
        temporizador = setTimeout(() => buscarPessoas(termo), ATRASO_MS);
    }

    /* ------------------------------------------------------------------ */
    /* Cidade                                                             */
    /* ------------------------------------------------------------------ */

    function filtrarCidades(termo) {
        const lista = $("cidadeResultados");
        if (!lista) return;

        const alvo = normalizar(termo).trim();
        let visiveis = 0;

        lista.querySelectorAll("button").forEach((botao) => {
            const combina = !alvo || normalizar(botao.textContent).includes(alvo);
            botao.classList.toggle("d-none", !combina);
            if (combina) visiveis++;
        });

        lista.hidden = visiveis === 0;
        mensagem(
            "cidadeBuscaMensagem",
            visiveis === 0 ? "Nenhuma cidade encontrada." : "Selecione uma cidade da lista."
        );
    }

    function limparDestino() {
        const input = $("destinoAgendamento");
        if (input) input.value = "";
        const id = $("idDestinoAgendamento");
        if (id) id.value = "";
        ocultar("destinoResultados");
    }

    function aoDigitarCidade(input) {
        // digitar invalida a cidade escolhida e, com ela, o destino
        $("idCidadeAgendamento").value = "";
        const destino = $("destinoAgendamento");
        destino.disabled = true;
        destino.placeholder = "Selecione uma cidade primeiro";
        limparDestino();
        mensagem("destinoBuscaMensagem", "Selecione uma cidade antes de pesquisar o destino.");
        filtrarCidades(input.value);
    }

    function selecionarCidade({ id, nome, estado }) {
        const anterior = $("idCidadeAgendamento").value;

        $("cidadeAgendamento").value = `${nome} - ${estado}`;
        $("idCidadeAgendamento").value = id;
        ocultar("cidadeResultados");
        mensagem("cidadeBuscaMensagem", "Cidade selecionada.");

        const destino = $("destinoAgendamento");
        destino.disabled = false;
        destino.placeholder = "Digite o nome do destino";
        if (anterior !== id) limparDestino(); // trocou de cidade: o destino antigo não vale mais
        mensagem("destinoBuscaMensagem", "Pesquise e selecione um destino.");
        destino.focus(); // o focusin abaixo já lista os destinos da cidade
    }

    /* ------------------------------------------------------------------ */
    /* Destino                                                            */
    /* ------------------------------------------------------------------ */

    function filtrarDestinos(termo) {
        const lista = $("destinoResultados");
        const idCidade = $("idCidadeAgendamento")?.value;
        if (!lista) return;
        if (!idCidade) {
            lista.hidden = true;
            return;
        }

        const alvo = normalizar(termo).trim();
        let daCidade = 0;
        let visiveis = 0;

        lista.querySelectorAll("button").forEach((botao) => {
            const pertence = botao.dataset.cidade === idCidade;
            const combina = pertence && (!alvo || normalizar(botao.textContent).includes(alvo));
            botao.classList.toggle("d-none", !combina);
            if (pertence) daCidade++;
            if (combina) visiveis++;
        });

        lista.hidden = visiveis === 0;
        if (daCidade === 0) {
            mensagem("destinoBuscaMensagem", "Nenhum destino cadastrado nesta cidade. Use “Cadastrar destino”.");
        } else if (visiveis === 0) {
            mensagem("destinoBuscaMensagem", "Nenhum destino encontrado.");
        } else {
            mensagem("destinoBuscaMensagem", "Selecione um destino da lista.");
        }
    }

    function aoDigitarDestino(input) {
        $("idDestinoAgendamento").value = ""; // digitar invalida o destino escolhido antes
        filtrarDestinos(input.value);
    }

    function selecionarDestino(botao) {
        $("destinoAgendamento").value = botao.textContent.trim();
        $("idDestinoAgendamento").value = botao.dataset.id;
        ocultar("destinoResultados");
        mensagem("destinoBuscaMensagem", "Destino selecionado.");
    }

    function abrirCadastroDestino(botao) {
        const idCidade = $("idCidadeAgendamento").value;
        if (!idCidade) {
            mensagem("cidadeBuscaMensagem", "Selecione uma cidade antes de cadastrar o destino.");
            $("cidadeAgendamento").focus();
            return;
        }
        const { urlPrefix, urlSuffix } = botao.dataset;
        window.htmx.ajax("GET", `${urlPrefix}${idCidade}${urlSuffix}`, {
            target: "#modal",
            swap: "innerHTML",
        });
    }

    /* ------------------------------------------------------------------ */
    /* Verificação: formulário x resultado, tipo aprovado, veículo físico */
    /* ------------------------------------------------------------------ */

    function atualizarCamposVeiculo() {
        const tipo = $("tipoPermitido");
        const container = $("veiculoFisicoContainer");
        const veiculo = $("veiculoFisico");
        if (!tipo || !container || !veiculo) return;

        const ehCarro = tipo.selectedOptions[0]?.dataset.carro === "true";
        const exigeVeiculo = tipo.value !== "" && !ehCarro;

        container.classList.toggle("d-none", !exigeVeiculo);
        veiculo.disabled = !exigeVeiculo; // select desabilitado não é enviado (carro: idVeiculo nulo)
        veiculo.required = exigeVeiculo;

        Array.from(veiculo.options).forEach((opcao) => {
            if (!opcao.value) return;
            const compativel = opcao.dataset.tipo === tipo.value;
            opcao.hidden = !compativel;
            opcao.disabled = !compativel; // Safari/iOS ignoram option[hidden]
        });

        if (!exigeVeiculo || veiculo.selectedOptions[0]?.disabled) veiculo.value = "";
    }

    // Resultado visível => formulário de dados oculto (e vice-versa). Idempotente.
    function sincronizarVerificacao() {
        const resultado = $("resultadoVerificacao");
        const formulario = $("verificarAgendamentoForm");
        if (!resultado || !formulario) return;
        formulario.hidden = !resultado.hidden;
        atualizarCamposVeiculo();
    }

    function editarDadosAgendamento() {
        const formulario = $("verificarAgendamentoForm");
        const resultado = $("resultadoVerificacao");
        if (formulario) formulario.hidden = false;
        if (resultado) resultado.hidden = true;
    }

    /* ------------------------------------------------------------------ */
    /* Eventos                                                            */
    /* ------------------------------------------------------------------ */

    document.addEventListener("input", (e) => {
        if (ehElemento(e, "#pessoaBusca")) aoDigitarPessoa(e.target);
        else if (ehElemento(e, "#cidadeAgendamento")) aoDigitarCidade(e.target);
        else if (ehElemento(e, "#destinoAgendamento")) aoDigitarDestino(e.target);
    });

    document.addEventListener("focusin", (e) => {
        if (ehElemento(e, "#cidadeAgendamento")) filtrarCidades(e.target.value);
        else if (ehElemento(e, "#destinoAgendamento") && !e.target.disabled) filtrarDestinos(e.target.value);
    });

    document.addEventListener("change", (e) => {
        if (ehElemento(e, "#tipoPermitido")) atualizarCamposVeiculo();
    });

    document.addEventListener("click", (e) => {
        const alvo = e.target instanceof Element ? e.target : null;
        if (!alvo) return;

        const pessoa = alvo.closest("#pessoaResultados button");
        if (pessoa) return selecionarPessoa(pessoa.dataset);

        const cidade = alvo.closest("#cidadeResultados button");
        if (cidade) return selecionarCidade(cidade.dataset);

        const destino = alvo.closest("#destinoResultados button");
        if (destino) return selecionarDestino(destino);

        const cadastroDestino = alvo.closest("#abrirCadastroDestino");
        if (cadastroDestino) return abrirCadastroDestino(cadastroDestino);

        if (alvo.closest("#editarDadosAgendamento")) return editarDadosAgendamento();

        // clique fora: fecha as listas de sugestões
        if (!alvo.closest("#pessoaBusca, #pessoaResultados")) limparResultadosPessoa();
        if (!alvo.closest("#cidadeAgendamento, #cidadeResultados")) ocultar("cidadeResultados");
        if (!alvo.closest("#destinoAgendamento, #destinoResultados")) ocultar("destinoResultados");
    });

    // Enter nesses campos submeteria o formulário de verificação sem seleção feita
    document.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && ehElemento(e, "#pessoaBusca, #cidadeAgendamento, #destinoAgendamento")) {
            e.preventDefault();
        }
    });

    // O resultado da verificação chega por htmx (outerHTML em #resultadoVerificacao)
    document.addEventListener("htmx:afterSwap", sincronizarVerificacao);

    // Disparado pelo servidor (header HX-Trigger) após cadastrar a pessoa, se esse for o fluxo
    document.addEventListener("pessoa-criada", (e) => {
        selecionarPessoa(e.detail);
        $("modal")?.replaceChildren(); // fecha o overlay do cadastro
    });

    /* ------------------------------------------------------------------ */
    /* Inicialização da página                                            */
    /* ------------------------------------------------------------------ */

    function iniciarPagina(raiz) {
        const modal = $("agendamentoModal");
        if (!modal || !raiz?.contains?.(modal)) return;

        sincronizarVerificacao();

        // O servidor devolve a página com o agendamento em andamento (pessoa/destino recém
        // cadastrados, verificação reprovada...): reabre o modal para o usuário continuar.
        const resultado = $("resultadoVerificacao");
        const temEstado =
            $("idPessoaAgendamento")?.value ||
            $("idDestinoAgendamento")?.value ||
            (resultado && !resultado.hidden);

        if (temEstado && window.bootstrap) {
            window.bootstrap.Modal.getOrCreateInstance(modal).show();
        }
    }

    if (window.htmx) {
        window.htmx.onLoad(iniciarPagina); // carga inicial e a cada conteúdo trocado por htmx
    } else if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => iniciarPagina(document.body));
    } else {
        iniciarPagina(document.body);
    }
})();