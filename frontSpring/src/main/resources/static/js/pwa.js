if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/service-worker.js')
            .catch(error => console.error('Não foi possível registrar o service worker:', error));
    });
}

let instalacaoPwa;
const botaoInstalacaoPwa = document.getElementById('installPwaButton');
const ajudaInstalacaoPwa = document.getElementById('installPwaHelp');
const instalado = window.matchMedia('(display-mode: standalone)').matches || navigator.standalone === true;
const ios = /iPad|iPhone|iPod/.test(navigator.userAgent)
    || (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1);

if (ios && !instalado && botaoInstalacaoPwa) botaoInstalacaoPwa.hidden = false;

window.addEventListener('beforeinstallprompt', event => {
    event.preventDefault();
    instalacaoPwa = event;
    if (botaoInstalacaoPwa) botaoInstalacaoPwa.hidden = false;
});

botaoInstalacaoPwa?.addEventListener('click', async () => {
    if (!instalacaoPwa) {
        if (ios && ajudaInstalacaoPwa) ajudaInstalacaoPwa.hidden = !ajudaInstalacaoPwa.hidden;
        return;
    }
    try {
        await instalacaoPwa.prompt();
        await instalacaoPwa.userChoice;
        instalacaoPwa = null;
        botaoInstalacaoPwa.hidden = true;
    } catch (error) {
        console.error('Não foi possível iniciar a instalação do aplicativo:', error);
        if (ajudaInstalacaoPwa) {
            ajudaInstalacaoPwa.textContent = 'Não foi possível iniciar a instalação. Use o menu do navegador.';
            ajudaInstalacaoPwa.hidden = false;
        }
    }
});

window.addEventListener('appinstalled', () => {
    instalacaoPwa = null;
    if (botaoInstalacaoPwa) botaoInstalacaoPwa.hidden = true;
    if (ajudaInstalacaoPwa) ajudaInstalacaoPwa.hidden = true;
});
