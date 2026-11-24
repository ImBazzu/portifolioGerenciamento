/**
 * Comportamento da página de Agendamentos.
 *
 * Responsabilidades:
 *  - Seleção inicial de data e atalhos rápidos (Hoje, Amanhã, +2 dias).
 *  - Troca rápida do tipo de veículo diretamente na tabela.
 *  - Filtros da tabela de passageiros cadastrados.
 *  - Filtro interno nos modais de atribuição direta.
 *  - Preenchimento do modal de edição de passageiro.
 *  - Seleção de pessoa nos resultados renderizados pelo Thymeleaf/HTMX.
 *  - Busca e atualização dinâmica de cidade e destino.
 *  - Verificação e escolha direta de veículos disponíveis.
 *  - Reabertura automática do modal de agendamento.
 *
 * IMPORTANTE:
 * A busca de pessoas, cidades e destinos é responsabilidade do HTMX:
 * o servidor devolve os <button> dos resultados a cada requisição.
 * Este arquivo não usa fetch() para buscar nem filtra listas no cliente;
 * apenas trata o clique (delegação no document) e a limpeza dos campos.
 */

(() => {
    if (window.__agendamentoModalInit) return;
    window.__agendamentoModalInit = true;

    const $ = (id) => document.getElementById(id);

    const ehElemento = (e, seletor) =>
        e.target instanceof Element && e.target.matches(seletor);

    const normalizar = (texto) =>
        (texto ?? "")
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase();

    const mensagem = (id, texto) => {
        const el = $(id);
        if (el) el.textContent = texto;
    };

    /** Esvazia uma lista de resultados renderizada pelo servidor (htmx). */
    const limpar = (id) => $(id)?.replaceChildren();

    /* ------------------------------------------------------------------ */
    /* 0. Seleção Inicial de Data (UX Data-First)                         */
    /* ------------------------------------------------------------------ */

    function formatarDataLocalISO(diasOffset = 0) {
        const data = new Date();
        data.setDate(data.getDate() + diasOffset);
        const ano = data.getFullYear();
        const mes = String(data.getMonth() + 1).padStart(2, "0");
        const dia = String(data.getDate()).padStart(2, "0");
        return `${ano}-${mes}-${dia}`;
    }

    function aplicarAtalhoData(botao) {
        const offset = parseInt(botao.dataset.offset ?? "0", 10);
        const input = $("inputDataInicial");
        const form = $("formSelecaoDataInicial");
        if (!input) return;

        input.value = formatarDataLocalISO(offset);
        if (form) {
            form.submit();
        }
    }

    /* ------------------------------------------------------------------ */
    /* 1. Filtros da Tabela Principal de Passageiros                      */
    /* ------------------------------------------------------------------ */

    function aplicarFiltrosTabela() {
        const filtroTexto = $("filtroTexto");
        const filtroCidade = $("filtroCidade");
        const filtroStatus = $("filtroStatus");

        const linhas = document.querySelectorAll(".linha-passageiro");
        const semResultados = $("semResultadosFiltro");

        if (!linhas.length) return;

        const txt = normalizar(filtroTexto?.value);
        const cid = normalizar(filtroCidade?.value);
        const st = filtroStatus?.value;

        let visiveis = 0;

        linhas.forEach((linha) => {
            const nome = normalizar(linha.dataset.nome);
            const tel = normalizar(linha.dataset.telefone);
            const cidade = normalizar(linha.dataset.cidade);
            const destino = normalizar(linha.dataset.destino);
            const status = linha.dataset.status;

            const bateTexto =
                !txt ||
                nome.includes(txt) ||
                tel.includes(txt) ||
                destino.includes(txt);

            const bateCidade = !cid || cidade.includes(cid);
            const bateStatus = !st || status === st;

            const mostrar = bateTexto && bateCidade && bateStatus;

            linha.classList.toggle("d-none", !mostrar);

            if (mostrar) visiveis++;
        });

        semResultados?.classList.toggle("d-none", visiveis > 0);
    }

    function limparFiltrosTabela() {
        if ($("filtroTexto")) $("filtroTexto").value = "";
        if ($("filtroCidade")) $("filtroCidade").value = "";
        if ($("filtroStatus")) $("filtroStatus").value = "";

        aplicarFiltrosTabela();
    }

    /* ------------------------------------------------------------------ */
    /* 2. Filtro nos Modais de Atribuição Direta ao Veículo               */
    /* ------------------------------------------------------------------ */

    function filtrarModalAtribuicao(modal, termoBruto) {
        if (!modal) return;

        const termo = normalizar(termoBruto).trim();

        let visiveis = 0;

        modal.querySelectorAll(".item-passageiro").forEach((item) => {
            const combina =
                !termo || normalizar(item.dataset.busca).includes(termo);

            item.classList.toggle("d-none", !combina);

            if (combina) visiveis++;
        });

        modal
            .querySelector(".sem-resultados")
            ?.classList.toggle("d-none", visiveis > 0);
    }

    /* ------------------------------------------------------------------ */
    /* 3. Preenchimento do Modal de Edição do Passageiro                  */
    /* ------------------------------------------------------------------ */

    function preencherModalEdicao(botao) {
        const targetSelector = botao.getAttribute("data-bs-target");

        if (!targetSelector) return;

        const modal = document.querySelector(targetSelector);

        if (!modal) return;

        const {
            horario,
            acompanhantes,
            residencia,
            veiculoId,
            tipoVeiculoId
        } = botao.dataset;

        const selectTipoVeiculo =
            modal.querySelector("select[name='idTipoVeiculo']");

        if (selectTipoVeiculo && tipoVeiculoId) {
            selectTipoVeiculo.value = tipoVeiculoId;
        }

        reiniciarEdicaoEndereco(modal);

        const inputHorario =
            modal.querySelector("input[name='horarioChegada']");

        if (inputHorario && horario) {
            inputHorario.value = horario.substring(0, 5);
        }

        const inputAcomp =
            modal.querySelector("input[name='acompanhantes']");

        if (inputAcomp && acompanhantes !== undefined) {
            inputAcomp.value = acompanhantes;
        }

        const checkResidencia =
            modal.querySelector(
                "input[id^='editarBusca-'][type='checkbox']"
            );

        if (checkResidencia) {
            checkResidencia.checked = residencia === "true";
        }

        const selectVeiculo =
            modal.querySelector("select[name='idVeiculo']");

        if (selectVeiculo) {
            selectVeiculo.value = veiculoId || "";
        }
    }

    /* ------------------------------------------------------------------ */
    /* 4. Seleção de Pessoa (HTMX + Thymeleaf)                            */
    /* ------------------------------------------------------------------ */

    function limparSelecaoPessoa() {
        const id = $("idPessoaAgendamento");

        if (id) {
            id.value = "";
        }

        const selecionada = $("pessoaSelecionada");

        if (selecionada) {
            selecionada.textContent = "";
            selecionada.classList.add("d-none");
        }
    }

    function selecionarPessoa({ id, nome, prioridade }) {
        if (!id || !nome) {
            console.error(
                "Não foi possível selecionar a pessoa: ID ou nome ausente."
            );
            return;
        }

        const idInput = $("idPessoaAgendamento");
        const buscaInput = $("pessoaBusca");

        if (idInput) {
            idInput.value = id;
        }

        if (buscaInput) {
            buscaInput.value = nome;
        }

        const selecionada = $("pessoaSelecionada");

        if (selecionada) {
            selecionada.textContent = prioridade
                ? `${nome} — ${prioridade}`
                : nome;

            selecionada.classList.remove("d-none");
        }

        mensagem("pessoaBuscaMensagem", "Pessoa selecionada.");

        $("pessoaResultados")?.replaceChildren();
    }

    function aoDigitarPessoa() {
        limparSelecaoPessoa();

        mensagem(
            "pessoaBuscaMensagem",
            "Pesquise e selecione uma pessoa para continuar."
        );
    }

    /* ------------------------------------------------------------------ */
    /* 5. Cidade e Destino                                                */
    /* ------------------------------------------------------------------ */

    function limparDestino() {
        const input = $("destinoAgendamento");

        if (input) {
            input.value = "";
        }

        const id = $("idDestinoAgendamento");

        if (id) {
            id.value = "";
        }

        limpar("destinoResultados");
    }

    function aoDigitarCidade(input) {
        const idCidadeInput = $("idCidadeAgendamento");

        if (idCidadeInput) {
            idCidadeInput.value = "";
        }

        const destino = $("destinoAgendamento");

        if (destino) {
            destino.disabled = true;
            destino.placeholder = "Selecione uma cidade primeiro";
        }

        limparDestino();

        mensagem(
            "destinoBuscaMensagem",
            "Selecione uma cidade antes de pesquisar o destino."
        );

        mensagem("cidadeBuscaMensagem", "Pesquise e selecione uma cidade.");
    }

    function selecionarCidade({ id, nome, estado }) {
        const idCidadeInput = $("idCidadeAgendamento");
        const anterior = idCidadeInput?.value;

        if ($("cidadeAgendamento")) {
            $("cidadeAgendamento").value = `${nome} - ${estado}`;
        }

        if (idCidadeInput) {
            idCidadeInput.value = id;
        }

        limpar("cidadeResultados");

        mensagem("cidadeBuscaMensagem", "Cidade selecionada.");

        const destino = $("destinoAgendamento");

        if (destino) {
            destino.disabled = false;
            destino.placeholder = "Digite o nome do destino";

            if (anterior !== id) {
                limparDestino();
            }

            mensagem(
                "destinoBuscaMensagem",
                "Pesquise e selecione um destino."
            );

            destino.focus();
        }
    }

    function aoDigitarDestino(input) {
        const idDestino = $("idDestinoAgendamento");

        if (idDestino) {
            idDestino.value = "";
        }

        mensagem("destinoBuscaMensagem", "Pesquise e selecione um destino.");
    }

    function selecionarDestino(botao) {
        if ($("destinoAgendamento")) {
            $("destinoAgendamento").value = botao.textContent.trim();
        }

        if ($("idDestinoAgendamento")) {
            $("idDestinoAgendamento").value = botao.dataset.id;
        }

        limpar("destinoResultados");

        mensagem("destinoBuscaMensagem", "Destino selecionado.");
    }

    function adicionarDestinoDinamico({ id, nome, idCidade }) {
        if (!id || !nome) return;

        if (idCidade && $("idCidadeAgendamento")) {
            $("idCidadeAgendamento").value = idCidade;
            const destinoInput = $("destinoAgendamento");
            if (destinoInput) destinoInput.disabled = false;
        }

        if ($("destinoAgendamento")) $("destinoAgendamento").value = nome;
        if ($("idDestinoAgendamento")) $("idDestinoAgendamento").value = id;

        limpar("destinoResultados");
        mensagem("destinoBuscaMensagem", "Destino selecionado.");
    }

    function abrirCadastroDestino(botao) {
        const idCidade = $("idCidadeAgendamento")?.value;

        if (!idCidade) {
            mensagem(
                "cidadeBuscaMensagem",
                "Selecione uma cidade antes de cadastrar o destino."
            );

            $("cidadeAgendamento")?.focus();

            return;
        }

        const { urlPrefix, urlSuffix } = botao.dataset;

        window.htmx.ajax(
            "GET",
            `${urlPrefix}${idCidade}${urlSuffix}`,
            {
                target: "#modal",
                swap: "innerHTML"
            }
        );
    }

    /* ------------------------------------------------------------------ */
    /* 5b. Cidade e Destino nos modais de edição (htmx)                   */
    /* ------------------------------------------------------------------ */

    const grupoEdicao = (el) => el.closest(".js-endereco-edicao");

    function limparResultados(grupo) {
        grupo
            ?.querySelectorAll(".js-cidade-resultados, .js-destino-resultados")
            .forEach((el) => el.replaceChildren());
    }

    function restaurarDestinoOriginal(grupo) {
        const hidden = grupo?.querySelector("[name='idDestino']");

        if (hidden) hidden.value = hidden.dataset.original ?? "";
    }

    function bloquearDestino(grupo) {
        const destino = grupo.querySelector(".js-destino-input");

        destino.value = "";
        destino.disabled = true;
        destino.placeholder = "Selecione uma cidade primeiro";
    }

    function aoDigitarCidadeEdicao(input) {
        const grupo = grupoEdicao(input);

        if (!grupo) return;

        grupo.querySelector("[name='idCidade']").value = "";
        bloquearDestino(grupo);
        restaurarDestinoOriginal(grupo);
    }

    function selecionarCidadeEdicao(botao) {
        const grupo = grupoEdicao(botao);
        const { id, nome, estado } = botao.dataset;

        grupo.querySelector(".js-cidade-input").value = `${nome} - ${estado}`;
        grupo.querySelector("[name='idCidade']").value = id;
        limparResultados(grupo);

        const destino = grupo.querySelector(".js-destino-input");

        destino.disabled = false;
        destino.value = "";
        destino.placeholder = "Digite o nome do destino";
        restaurarDestinoOriginal(grupo);
        destino.focus();
    }

    function aoDigitarDestinoEdicao(input) {
        restaurarDestinoOriginal(grupoEdicao(input));
    }

    function selecionarDestinoEdicao(botao) {
        const grupo = grupoEdicao(botao);

        grupo.querySelector(".js-destino-input").value =
            botao.textContent.trim();
        grupo.querySelector("[name='idDestino']").value = botao.dataset.id;
        limparResultados(grupo);
    }

    function reiniciarEdicaoEndereco(modal) {
        modal.querySelectorAll(".js-endereco-edicao").forEach((grupo) => {
            grupo.querySelector(".js-cidade-input").value = "";
            grupo.querySelector("[name='idCidade']").value = "";
            bloquearDestino(grupo);
            restaurarDestinoOriginal(grupo);
            limparResultados(grupo);
        });
    }

    /* ------------------------------------------------------------------ */
    /* Mensagens de apoio depois que o htmx troca os resultados           */
    /* ------------------------------------------------------------------ */

    const TERMO_MINIMO_CIDADE = 2;

    function atualizarMensagemBusca(e) {
        const alvo = e.detail?.target;

        if (!alvo) return;

        const vazio = alvo.children.length === 0;

        if (alvo.id === "cidadeResultados") {
            const curto =
                ($("cidadeAgendamento")?.value ?? "").trim().length < TERMO_MINIMO_CIDADE;

            mensagem(
                "cidadeBuscaMensagem",
                curto
                    ? `Digite ao menos ${TERMO_MINIMO_CIDADE} letras para pesquisar.`
                    : vazio
                        ? "Nenhuma cidade encontrada."
                        : "Selecione uma cidade da lista."
            );
        } else if (alvo.id === "destinoResultados") {
            mensagem(
                "destinoBuscaMensagem",
                vazio
                    ? "Nenhum destino encontrado. Use “Cadastrar destino”."
                    : "Selecione um destino da lista."
            );
        }
    }

    /* ------------------------------------------------------------------ */
    /* 6. Verificação do agendamento                                      */
    /* ------------------------------------------------------------------ */

    function sincronizarVerificacao() {
        const resultado = $("resultadoVerificacao");
        const formulario = $("verificarAgendamentoForm");

        if (!resultado || !formulario) return;

        formulario.hidden = !resultado.hidden;
    }

    function editarDadosAgendamento() {
        const formulario = $("verificarAgendamentoForm");
        const resultado = $("resultadoVerificacao");

        if (formulario) {
            formulario.hidden = false;
        }

        if (resultado) {
            resultado.hidden = true;
        }
    }

    /* ------------------------------------------------------------------ */
    /* 7. Delegação Global de Eventos                                     */
    /* ------------------------------------------------------------------ */

    document.addEventListener("input", (e) => {
        if (ehElemento(e, "#pessoaBusca")) {
            aoDigitarPessoa();
        } else if (ehElemento(e, ".js-cidade-input")) {
            aoDigitarCidadeEdicao(e.target);
        } else if (ehElemento(e, ".js-destino-input")) {
            aoDigitarDestinoEdicao(e.target);
        } else if (ehElemento(e, "#cidadeAgendamento")) {
            aoDigitarCidade(e.target);
        } else if (ehElemento(e, "#destinoAgendamento")) {
            aoDigitarDestino(e.target);
        } else if (
            ehElemento(e, "#filtroTexto, #filtroCidade")
        ) {
            aplicarFiltrosTabela();
        } else if (ehElemento(e, ".busca-passageiro-input")) {
            filtrarModalAtribuicao(
                e.target.closest(".modal"),
                e.target.value
            );
        }
    });

    document.addEventListener("change", (e) => {
        if (ehElemento(e, "#resultadoVerificacao input[name='acompanhantes']")) {
            document.querySelectorAll("#resultadoVerificacao .js-confirmar-agendamento")
                .forEach((botao) => { botao.disabled = true; });
            const status = e.target.form?.querySelector("[data-verificacao-status]");
            if (status) status.textContent = "Atualizando veículos permitidos...";
        }
    }, true);

    document.addEventListener("change", (e) => {
        if (ehElemento(e, "#filtroStatus")) {
            aplicarFiltrosTabela();
        } else if (ehElemento(e, ".js-troca-rapida-tipo")) {
            e.target.closest("form")?.submit();
        } else if (
            ehElemento(e, "#dataTopo") &&
            e.target.value
        ) {
            const urlAtual = window.location.pathname;

            const destino = urlAtual.includes("/carros")
                ? `${urlAtual}?data=${e.target.value}`
                : `/agendamentos?data=${e.target.value}`;

            window.location.href = destino;
        }
    });

    document.addEventListener("click", (e) => {
        const alvo =
            e.target instanceof Element ? e.target : null;

        if (!alvo) return;

        const atalhoData = alvo.closest(".js-atalho-data");
        if (atalhoData) {
            aplicarAtalhoData(atalhoData);
            return;
        }

        document
            .querySelectorAll(".modal.show .js-endereco-edicao")
            .forEach((grupo) => {
                if (!grupo.contains(alvo)) limparResultados(grupo);
            });

        const btnEditarPassageiro =
            alvo.closest(".btn-abrir-edicao");

        if (btnEditarPassageiro) {
            preencherModalEdicao(btnEditarPassageiro);
            return;
        }

        if (alvo.closest("#btnLimparFiltros")) {
            limparFiltrosTabela();
            return;
        }

        const cidadeEdicao = alvo.closest(".js-cidade-resultados button");

        if (cidadeEdicao) {
            selecionarCidadeEdicao(cidadeEdicao);
            return;
        }

        const destinoEdicao = alvo.closest(".js-destino-resultados button");

        if (destinoEdicao) {
            selecionarDestinoEdicao(destinoEdicao);
            return;
        }

        const pessoa =
            alvo.closest("#pessoaResultados button");

        if (pessoa) {
            selecionarPessoa(pessoa.dataset);
            return;
        }

        const cidade =
            alvo.closest("#cidadeResultados button");

        if (cidade) {
            selecionarCidade(cidade.dataset);
            return;
        }

        const destino =
            alvo.closest("#destinoResultados button");

        if (destino) {
            selecionarDestino(destino);
            return;
        }

        const cadastroDestino =
            alvo.closest("#abrirCadastroDestino");

        if (cadastroDestino) {
            abrirCadastroDestino(cadastroDestino);
            return;
        }

        if (alvo.closest("#editarDadosAgendamento")) {
            editarDadosAgendamento();
        }

        if (
            !alvo.closest(
                "#cidadeAgendamento, #cidadeResultados"
            )
        ) {
            limpar("cidadeResultados");
        }

        if (
            !alvo.closest(
                "#destinoAgendamento, #destinoResultados"
            )
        ) {
            limpar("destinoResultados");
        }
    });

    document.addEventListener("keydown", (e) => {
        if (
            e.key === "Enter" &&
            ehElemento(
                e,
                "#pessoaBusca, #cidadeAgendamento, #destinoAgendamento, .js-cidade-input, .js-destino-input"
            )
        ) {
            e.preventDefault();
        }
    });

    /* ------------------------------------------------------------------ */
    /* 8. Eventos dos Modais Bootstrap                                    */
    /* ------------------------------------------------------------------ */

    document.addEventListener("show.bs.modal", (e) => {
        const input =
            e.target.querySelector(".busca-passageiro-input");

        if (!input) return;

        input.value = "";

        filtrarModalAtribuicao(e.target, "");
    });

    document.addEventListener("shown.bs.modal", (e) => {
        e.target
            .querySelector(".busca-passageiro-input")
            ?.focus();
    });

    document.addEventListener("hidden.bs.modal", (e) => {
        if (e.target.matches?.("[id^='editarAgendamentoModal-']")) {
            reiniciarEdicaoEndereco(e.target);
        }
    });

    /* ------------------------------------------------------------------ */
    /* 9. Eventos HTMX                                                    */
    /* ------------------------------------------------------------------ */

    document.addEventListener("htmx:afterSwap", (e) => {
        if (e.detail?.target?.id === "resultadoVerificacao") {
            sincronizarVerificacao();
        }
    });

    document.addEventListener("htmx:afterSwap", atualizarMensagemBusca);

    function informarFalhaVerificacao(e) {
        const formulario = e.detail?.elt;
        if (!formulario?.matches?.(".js-reverificar-acompanhantes")) return;
        const status = formulario.querySelector("[data-verificacao-status]");
        if (status) status.textContent = "Não foi possível atualizar os veículos. Tente novamente.";
    }

    document.addEventListener("htmx:responseError", informarFalhaVerificacao);
    document.addEventListener("htmx:sendError", informarFalhaVerificacao);

    document.addEventListener("pessoa-criada", (e) => {
        if (e.detail) {
            selecionarPessoa(e.detail);
        }

        $("modal")?.replaceChildren();
    });

    document.addEventListener("destino-criado", (e) => {
        if (e.detail) {
            adicionarDestinoDinamico(e.detail);
        }

        $("modal")?.replaceChildren();
    });

    /* ------------------------------------------------------------------ */
    /* 10. Inicialização e Restauração de Estado                          */
    /* ------------------------------------------------------------------ */

    function iniciarPagina(raiz) {
        const inputDataInicial = $("inputDataInicial");
        if (inputDataInicial && !inputDataInicial.value) {
            inputDataInicial.value = formatarDataLocalISO(0);
            inputDataInicial.focus();
        }

        const modal = $("agendamentoModal");

        if (!modal || !raiz?.contains?.(modal)) return;

        const idCidade = $("idCidadeAgendamento")?.value;
        const destinoInput = $("destinoAgendamento");

        if (idCidade && destinoInput) {
            destinoInput.disabled = false;
            destinoInput.placeholder = "Digite o nome do destino";
        }

        sincronizarVerificacao();

        const resultado = $("resultadoVerificacao");

        const temEstado =
            $("idPessoaAgendamento")?.value ||
            $("idDestinoAgendamento")?.value ||
            (resultado && !resultado.hidden);

        if (temEstado && window.bootstrap) {
            window.bootstrap.Modal
                .getOrCreateInstance(modal)
                .show();
        }
    }

    if (window.htmx) {
        window.htmx.onLoad(iniciarPagina);
    } else if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => {
            iniciarPagina(document.body);
        });
    } else {
        iniciarPagina(document.body);
    }
})();
