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

// Fechamento automático de modais em requisições HTMX
document.body.addEventListener('htmx:afterSwap', function (evt) {
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

        return {
            idsVeiculos: valoresSelecionados(card, 'idsVeiculos'),
            idsCidades: valoresSelecionados(card, 'idsCidades'),
            prioridades: valoresSelecionados(card, 'prioridades'),
            inicioHorario: inicio.value || null,
            finalHorario: final.value || null,
            obrigacaoAnexo: card.querySelector('[data-campo="obrigacaoAnexo"]').checked,
            descricao: card.querySelector('[data-campo="descricao"]').value.trim() || null
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