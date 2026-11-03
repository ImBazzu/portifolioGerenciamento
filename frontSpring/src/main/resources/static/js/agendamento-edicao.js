document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".js-editar-agendamento-form").forEach((form) => {
        const tipo = form.querySelector(".js-editar-tipo");
        const veiculo = form.querySelector(".js-editar-veiculo");

        const atualizarVeiculos = () => {
            const tipoSelecionado = tipo.selectedOptions[0];
            const ehCarro = tipoSelecionado?.dataset.carro === "true";
            let selecaoValida = veiculo.value === "";

            Array.from(veiculo.options).forEach((opcao) => {
                if (!opcao.value) {
                    opcao.hidden = false;
                    return;
                }
                const compativel = opcao.dataset.tipo === tipo.value;
                opcao.hidden = !compativel;
                if (opcao.selected && !compativel) {
                    veiculo.value = "";
                }
                if (opcao.selected && compativel) {
                    selecaoValida = true;
                }
            });

            veiculo.required = !ehCarro;
            if (!ehCarro && !selecaoValida) {
                veiculo.value = "";
            }
        };

        tipo.addEventListener("change", atualizarVeiculos);
        atualizarVeiculos();
    });
});
