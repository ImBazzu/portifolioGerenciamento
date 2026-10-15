function toggleSenha() {
    const input = document.getElementById('senha');
    const icone = document.getElementById('icone-senha');

    if (input.type === 'password') {
        input.type = 'text';
        icone.className = 'bi bi-eye-slash';
    } else {
        input.type = 'password';
        icone.className = 'bi bi-eye';
    }
}