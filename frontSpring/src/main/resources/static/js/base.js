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