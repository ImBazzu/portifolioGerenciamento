// Função para inicializar os eventos do menu mobile
function initMobileMenu() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.getElementById('sidebarOverlay');

    function fecharMenu() {
        if (sidebar) sidebar.classList.remove('show');
        if (overlay) overlay.classList.remove('show');
    }

    function abrirMenu() {
        if (sidebar) sidebar.classList.add('show');
        if (overlay) overlay.classList.add('show');
    }

    // Event listener global com delegação
    document.addEventListener('click', (e) => {
        // Abrir menu ao clicar no botão hambúrguer
        if (e.target.closest('#btnToggleSidebar')) {
            abrirMenu();
        }
        // Fechar menu ao clicar no overlay ou no botão "X"
        if (e.target.closest('#sidebarOverlay') || e.target.closest('#btnFecharSidebar')) {
            fecharMenu();
        }
    });
}

// Inicializa quando o DOM estiver pronto
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMobileMenu);
} else {
    initMobileMenu();
}

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/service-worker.js')
            .catch(error => console.error('Não foi possível registrar o service worker:', error));
    });
}

let instalacaoPwa;
const botaoInstalacaoPwa = document.getElementById('installPwaButton');
window.addEventListener('beforeinstallprompt', event => {
    event.preventDefault();
    instalacaoPwa = event;
    if (botaoInstalacaoPwa) botaoInstalacaoPwa.hidden = false;
});

botaoInstalacaoPwa?.addEventListener('click', async () => {
    if (!instalacaoPwa) return;
    instalacaoPwa.prompt();
    await instalacaoPwa.userChoice;
    instalacaoPwa = null;
    botaoInstalacaoPwa.hidden = true;
});

window.addEventListener('appinstalled', () => {
    instalacaoPwa = null;
    if (botaoInstalacaoPwa) botaoInstalacaoPwa.hidden = true;
});

// Fechamento automático de modais em requisições HTMX
document.body.addEventListener('htmx:afterSwap', function (evt) {
    if (evt.detail.target.id === 'especificacaoResultados') {
        const grupo = evt.detail.target.closest('.js-especificacao-grupo');
        const busca = grupo?.querySelector('.js-especificacao-busca');
        const mensagem = grupo?.querySelector('#especificacaoBuscaMensagem');
        const termoCurto = (busca?.value ?? '').trim().length < 2;
        if (mensagem) {
            mensagem.textContent = termoCurto
                ? 'Digite ao menos duas letras para pesquisar.'
                : evt.detail.target.children.length === 0
                    ? 'Nenhuma especificação encontrada.'
                    : 'Selecione uma especificação da lista.';
        }
    }

    if (evt.detail.target.id === 'modal') {
        document.querySelectorAll('[data-lista-regras]').forEach(container => {
            atualizarTitulosRegras(container, container.dataset.listaRegras);
        });
    }

    const tabelasQueFechamModal = ['tabela-motoristas', 'tabela-cidades-container'];
    if (tabelasQueFechamModal.includes(evt.detail.target.id)) {
        const modalEl = document.getElementById('modalBasico');
        if (modalEl) {
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) {
                modal.hide();
                document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
                document.body.classList.remove('modal-open');
                document.body.style.overflow = '';
                document.body.style.paddingRight = '';
            }
        }
    }
});

document.addEventListener('click', function (event) {
    if (!(event.target instanceof Element)) return;

    const addButton = event.target.closest('[data-adicionar-regra]');
    if (addButton) {
        const tipo = addButton.dataset.adicionarRegra;
        const template = document.getElementById(`regra-${tipo}-template`);
        const container = document.querySelector(`[data-lista-regras="${tipo}"]`);
        if (!template || !container) return;

        const emptyState = container.querySelector('[data-vazio]');
        if (emptyState) emptyState.remove();
        container.append(template.content.cloneNode(true));
        atualizarTitulosRegras(container, tipo);
        return;
    }

    const removeButton = event.target.closest('[data-remover-regra]');
    if (!removeButton) return;

    const card = removeButton.closest('[data-regra]');
    const tipo = card?.dataset.regra;
    const container = card?.parentElement;
    if (!card || !tipo || !container) return;
    card.remove();
    atualizarTitulosRegras(container, tipo);

    if (!container.querySelector('[data-regra]')) {
        const emptyState = document.createElement('div');
        emptyState.className = 'lista-rule-empty';
        emptyState.dataset.vazio = tipo;
        const icon = document.createElement('i');
        icon.className = 'bi bi-inbox';
        const message = document.createElement('span');
        message.textContent = `Nenhuma regra de ${tipo} adicionada.`;
        emptyState.append(icon, message);
        container.append(emptyState);
    }
});

document.addEventListener('change', function (event) {
    const input = event.target;
    if (input instanceof HTMLInputElement && input.matches('[data-filtrar-residencia]')) {
        const filtrar = input.checked;
        const itens = Array.from(document.querySelectorAll('[data-residencia-item]'));
        itens.forEach(item => {
            item.hidden = filtrar && item.dataset.residencia !== 'true';
        });
        const visiveis = itens.filter(item => !item.hidden).length;
        const status = input.closest('section')?.querySelector('[data-residencia-status]')
            || document.querySelector('[data-residencia-status]');
        if (status) {
            status.textContent = filtrar
                ? `${visiveis} passageiro(s) para buscar na residência.`
                : 'Filtro de residência desativado.';
        }
        return;
    }

    if (input instanceof HTMLInputElement && input.id === 'informarHorarioSaidaCheckbox') {
        const hidden = document.getElementById('informarHorarioSaida');
        const label = document.getElementById('horarioAgendamentoLabel');
        if (hidden) hidden.value = String(input.checked);
        if (label) label.textContent = input.checked
            ? 'Horário de saída do veículo'
            : 'Chegada desejada';
        return;
    }

    if (input instanceof HTMLSelectElement && input.matches('[data-finalidade-abertura]')) {
        atualizarCamposAberturaCarro(input);
        return;
    }

    if (input instanceof HTMLSelectElement && input.id === 'listaAgendamento') {
        atualizarOpcaoHorarioSaida();
        return;
    }

    if (!(input instanceof HTMLInputElement) || !input.matches('[data-campo]')) return;

    if (input.checked && input.matches('[data-selecao-unica]')) {
        const grupo = input.closest('[data-grupo-obrigatorio]');
        grupo?.querySelectorAll('[data-selecao-unica]').forEach(opcao => {
            if (opcao !== input) opcao.checked = false;
        });
    }

    const grupo = input.closest('[data-grupo-obrigatorio]');
    if (grupo) validarGrupoRegra(grupo);
});

function atualizarOpcaoHorarioSaida() {
    const lista = document.getElementById('listaAgendamento');
    const opcao = lista?.selectedOptions[0];
    const container = document.getElementById('opcaoHorarioSaidaEmergencia');
    const checkbox = document.getElementById('informarHorarioSaidaCheckbox');
    const hidden = document.getElementById('informarHorarioSaida');
    const label = document.getElementById('horarioAgendamentoLabel');
    if (!lista || !opcao || !container || !checkbox || !hidden || !label) return;

    const nomeLista = (opcao.dataset.listName || opcao.textContent || '')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase();
    const permiteSaida = container.dataset.origemEspecial === 'true'
        || nomeLista.includes('emergencia') || nomeLista.includes('alta');
    container.hidden = !permiteSaida;
    if (!permiteSaida) checkbox.checked = false;
    hidden.value = String(permiteSaida && checkbox.checked);
    label.textContent = checkbox.checked
        ? 'Horário de saída do veículo'
        : 'Chegada desejada';
}

function atualizarCamposAberturaCarro(finalidade) {
    const form = finalidade.form;
    const horarioContainer = form?.querySelector('[data-finalidade-horario]');
    const lista = form?.querySelector('[name="idListaAbertura"]');
    const horario = form?.querySelector('[name="horarioSaida"]');
    const status = form?.querySelector('[data-finalidade-lista-status]');
    const botao = form?.querySelector('[data-abrir-carro]');
    if (!form || !horarioContainer || !lista || !horario || !status || !botao) return;

    const especial = finalidade.value === 'EMERGENCIA' || finalidade.value === 'ALTA';
    horarioContainer.hidden = !especial;
    horario.required = especial;
    const correspondente = Array.from(form.querySelectorAll('[data-lista-finalidade]'))
        .find(opcao => opcao.dataset.listaFinalidade === finalidade.value);
    lista.value = especial && correspondente ? correspondente.dataset.listaId : '';
    if (!especial) horario.value = '';
    const indisponivel = especial && !correspondente;
    botao.disabled = form.dataset.carroDisponivel !== 'true' || indisponivel;
    status.hidden = !indisponivel;
    status.textContent = indisponivel
        ? 'Lista do atendimento indisponível. Solicite a liberação ou reativação da lista correspondente.'
        : '';
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        atualizarOpcaoHorarioSaida();
        document.querySelectorAll('[data-finalidade-abertura]').forEach(atualizarCamposAberturaCarro);
    });
} else {
    atualizarOpcaoHorarioSaida();
    document.querySelectorAll('[data-finalidade-abertura]').forEach(atualizarCamposAberturaCarro);
}

document.addEventListener('input', function (event) {
    const campoBusca = event.target;
    if (!(campoBusca instanceof HTMLInputElement) || !campoBusca.matches('[data-busca-cidades]')) return;

    const grupo = campoBusca.closest('.lista-checkbox-group');
    const opcoes = grupo?.querySelector('[data-opcoes-cidades]');
    if (!grupo || !opcoes) return;

    const termo = normalizarTexto(campoBusca.value.trim());
    let opcoesVisiveis = 0;

    opcoes.querySelectorAll('.lista-checkbox-option').forEach(opcao => {
        const corresponde = normalizarTexto(opcao.textContent).includes(termo);
        opcao.hidden = !corresponde;
        if (corresponde) opcoesVisiveis++;
    });

    const semResultado = grupo.querySelector('[data-cidades-sem-resultado]');
    if (semResultado) semResultado.hidden = opcoesVisiveis > 0;
});

document.addEventListener('input', function (event) {
    const busca = event.target;
    if (!(busca instanceof HTMLInputElement) || !busca.matches('.js-especificacao-busca')) return;

    const grupo = busca.closest('.js-especificacao-grupo');
    const idEspecificacao = grupo?.querySelector('#especificacaoId');
    if (idEspecificacao) idEspecificacao.value = '';
    busca.setCustomValidity('');

    const mensagem = grupo?.querySelector('#especificacaoBuscaMensagem');
    if (mensagem) mensagem.textContent = 'Digite e selecione uma especificação da lista.';
});

document.addEventListener('submit', function (event) {
    const formulario = event.target;
    if (!(formulario instanceof HTMLFormElement)) return;

    const busca = formulario.querySelector('.js-especificacao-busca');
    const idEspecificacao = formulario.querySelector('#especificacaoId');
    if (!busca || !idEspecificacao || idEspecificacao.value) return;

    busca.setCustomValidity('Selecione uma especificação da lista.');
    busca.reportValidity();
    event.preventDefault();
}, true);

document.addEventListener('click', function (event) {
    const alvo = event.target;
    if (!(alvo instanceof Element)) return;

    const opcao = alvo.closest('#especificacaoResultados button');
    if (opcao) {
        const grupo = opcao.closest('.js-especificacao-grupo');
        const busca = grupo?.querySelector('.js-especificacao-busca');
        const idEspecificacao = grupo?.querySelector('#especificacaoId');
        if (!busca || !idEspecificacao) return;

        busca.value = opcao.dataset.label || opcao.textContent.trim();
        busca.setCustomValidity('');
        idEspecificacao.value = opcao.dataset.id || '';
        grupo.querySelector('#especificacaoResultados')?.replaceChildren();

        const mensagem = grupo.querySelector('#especificacaoBuscaMensagem');
        if (mensagem) mensagem.textContent = 'Especificação selecionada.';
        return;
    }

    if (!alvo.closest('#especificacaoBusca, #especificacaoResultados')) {
        document.querySelectorAll('#especificacaoResultados').forEach(resultados => resultados.replaceChildren());
    }
});

document.addEventListener('submit', function (event) {
    const form = event.target;
    if (!(form instanceof HTMLFormElement) || !form.matches('[data-lista-form]')) return;

    const gruposInvalidos = Array.from(form.querySelectorAll('[data-grupo-obrigatorio]'))
        .filter(grupo => !validarGrupoRegra(grupo));
    if (gruposInvalidos.length > 0) {
        event.preventDefault();
        event.stopImmediatePropagation();
        gruposInvalidos[0].querySelector('input[type="checkbox"]')?.focus();
        return;
    }

    const regrasCadastro = Array.from(form.querySelectorAll('[data-regra="cadastro"]')).map(card => {
        const inicio = card.querySelector('[data-campo="inicioHorario"]');
        const final = card.querySelector('[data-campo="finalHorario"]');
        let mensagemHorario = '';
        if (Boolean(inicio.value) !== Boolean(final.value)) {
            mensagemHorario = 'Preencha o horário inicial e final ou deixe ambos vazios.';
        } else if (inicio.value && inicio.value === final.value) {
            mensagemHorario = 'Os horários inicial e final devem ser diferentes.';
        }
        inicio.setCustomValidity(mensagemHorario);
        if (mensagemHorario) {
            event.preventDefault();
            event.stopImmediatePropagation();
            inicio.reportValidity();
            return null;
        }

        const intervaloRepetacao = card.querySelector('[data-campo="intervaloRepetacao"]');
        const intervaloValue = intervaloRepetacao.value ? parseInt(intervaloRepetacao.value) : 1;
        
        if (intervaloValue < 1 || intervaloValue > 52) {
            intervaloRepetacao.setCustomValidity('O intervalo deve estar entre 1 e 52 semanas.');
            event.preventDefault();
            event.stopImmediatePropagation();
            intervaloRepetacao.reportValidity();
            return null;
        }
        intervaloRepetacao.setCustomValidity('');

        return {
            idsVeiculos: valoresSelecionados(card, 'idsVeiculos'),
            idsCidades: valoresSelecionados(card, 'idsCidades'),
            prioridades: valoresSelecionados(card, 'prioridades'),
            inicioHorario: inicio.value || null,
            finalHorario: final.value || null,
            obrigacaoAnexo: card.querySelector('[data-campo="obrigacaoAnexo"]').checked,
            descricao: card.querySelector('[data-campo="descricao"]').value.trim() || null,
            diasSemana: valoresSelecionados(card, 'diasSemana'),
            intervaloRepetacao: intervaloValue
        };
    });

    if (regrasCadastro.includes(null)) {
        return;
    }

    const regrasSeparacao = Array.from(form.querySelectorAll('[data-regra="separacao"]')).map(card => {
        const [horas, minutos] = card.querySelector('[data-campo="tempoTolerancia"]').value
            .split(':')
            .map(Number);
        return {
            idTipoVeiculo: valoresSelecionados(card, 'idTipoVeiculo')[0],
            tempoToleranciaSegundos: horas * 3600 + minutos * 60,
            tempoToleranciaNanos: 0
        };
    });

    form.querySelector('[data-regras-json]').value = JSON.stringify({ regrasCadastro, regrasSeparacao });
}, true);

function valoresSelecionados(card, campo) {
    return Array.from(
        card.querySelectorAll(`[data-campo="${campo}"]:checked`),
        input => input.value
    );
}

function validarGrupoRegra(grupo) {
    const valido = grupo.querySelectorAll('input[type="checkbox"]:checked').length > 0;
    grupo.classList.toggle('is-invalid', !valido);
    grupo.setAttribute('aria-invalid', String(!valido));

    const mensagem = grupo.querySelector('[data-erro-selecao]');
    if (mensagem) mensagem.hidden = valido;

    return valido;
}

function normalizarTexto(texto) {
    return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR');
}

function atualizarTitulosRegras(container, tipo) {
    const nome = tipo === 'cadastro' ? 'cadastro' : 'separação';
    container.querySelectorAll('[data-regra]').forEach((card, index) => {
        card.querySelector('[data-titulo-regra]').textContent = `Regra de ${nome} ${index + 1}`;
    });
}