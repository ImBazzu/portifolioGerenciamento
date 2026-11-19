class RelatorioManager {
    constructor() {
        this.dados = {};
        this.graficos = {};
        this.baseUrl = document.querySelector('.relatorios').dataset.apiRelatorios;
        this.abortController = null;
        this.aba = 'agendamentos';
        document.querySelectorAll('.tab-btn').forEach(button => {
            button.addEventListener('click', () => this.trocarAba(button));
        });
        document.getElementById('btnFiltrar').addEventListener('click', () => this.carregar());
        document.getElementById('btnLimpar').addEventListener('click', () => {
            document.getElementById('filtroForm').reset();
            this.carregar();
        });
        document.getElementById('filtroForm').addEventListener('submit', event => {
            event.preventDefault();
            this.carregar();
        });
        this.carregar();
    }

    trocarAba(button) {
        document.querySelectorAll('.tab-btn, .relatorios .tab-content').forEach(element => element.classList.remove('active'));
        button.classList.add('active');
        this.aba = button.dataset.tab;
        document.getElementById(this.aba).classList.add('active');
        if (this.dados[this.aba]) this.renderizar(this.aba, this.dados[this.aba]);
    }

    async carregar() {
        const inicio = document.getElementById('dataInicio').value;
        const fim = document.getElementById('dataFim').value;
        const alerta = document.getElementById('erroRelatorio');
        if (inicio && fim && inicio > fim) {
            alerta.textContent = 'A data de início não pode ser posterior à data de fim.';
            alerta.hidden = false;
            return;
        }
        alerta.hidden = true;
        if (this.abortController) this.abortController.abort();
        const controller = new AbortController();
        this.abortController = controller;
        this.dados = {};
        Object.values(this.graficos).forEach(chart => chart.destroy());
        this.graficos = {};
        document.querySelectorAll('.grafico-relatorio, #tabelaAgendamentosDetalhes, #tabelaPessoasFrequentes')
            .forEach(element => element.replaceChildren());
        const params = new URLSearchParams();
        if (inicio) params.set('dataInicio', inicio);
        if (fim) params.set('dataFim', fim);

        await Promise.all(['agendamentos', 'pessoas', 'veiculos', 'operacional'].map(async tipo => {
            const metricas = document.getElementById(`metrics${tipo[0].toUpperCase()}${tipo.slice(1)}`);
            metricas.textContent = 'Carregando...';
            try {
                const response = await fetch(`${this.baseUrl}/${tipo}?${params}`, {
                    headers: { Accept: 'application/json' }, signal: controller.signal
                });
                if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
                    throw new Error(`HTTP ${response.status}`);
                }
                const dados = await response.json();
                if (controller.signal.aborted) return;
                this.dados[tipo] = dados;
                this.renderizarMetricas(tipo, dados);
                if (this.aba === tipo) this.renderizar(tipo, dados);
            } catch (error) {
                if (error.name === 'AbortError') return;
                console.error(`Erro ao consultar ${tipo}:`, error);
                metricas.textContent = `Não foi possível carregar ${tipo}.`;
                alerta.textContent = 'Não foi possível carregar todos os relatórios. Verifique a conexão e tente novamente.';
                alerta.hidden = false;
            }
        }));
    }

    renderizarMetricas(tipo, dados) {
        const itens = {
            agendamentos: [['Total de agendamentos', dados.totalAgendamentos], ['Realizados', null], ['Cancelados', null]],
            pessoas: [['Pessoas cadastradas', dados.totalPessoas], ['Ativas', dados.pessoasAtivas], ['Arquivadas', dados.pessoasArquivadas]],
            veiculos: [['Tipos cadastrados', dados.totalVeiculos], ['Tipos ativos', dados.veiculosAtivos], ['Tipos arquivados', dados.veiculosArquivados]],
            operacional: [['Agendamentos', dados.totalAgendamentos], ['Com veículo atribuído', dados.agendamentosAlocados], ['Sem veículo atribuído', dados.agendamentosSemVeiculo]]
        };
        const container = document.getElementById(`metrics${tipo[0].toUpperCase()}${tipo.slice(1)}`);
        container.replaceChildren();
        itens[tipo].forEach(([rotulo, valor]) => {
            const card = document.createElement('div');
            card.className = 'metric-card';
            const titulo = document.createElement('div');
            titulo.className = 'metric-label';
            titulo.textContent = rotulo;
            const numero = document.createElement('p');
            numero.className = 'metric-value';
            numero.textContent = valor == null ? 'Indisponível' : new Intl.NumberFormat('pt-BR').format(valor);
            card.append(titulo, numero);
            container.append(card);
        });
    }

    renderizar(tipo, dados) {
        if (tipo === 'agendamentos') {
            this.grafico('chartAgendamentosPeriodo', 'area', dados.agendamentosPorData, item => item.data, item => item.quantidade);
            this.grafico('chartAgendamentosDestino', 'bar', dados.agendamentosPorDestino, item => item.destino, item => item.quantidade);
            this.tabela('tabelaAgendamentosDetalhes', dados.agendamentosPorData, item => [item.data, item.quantidade], 2);
        } else if (tipo === 'pessoas') {
            this.grafico('chartPessoasEspecificacao', 'pie', dados.pessoasPorEspecificacao, item => item.especificacao, item => item.quantidade);
            this.grafico('chartPessoasMaisFrequentes', 'bar', dados.pessoasMaisFrequentes, item => item.nome, item => item.totalAgendamentos, true);
            this.tabela('tabelaPessoasFrequentes', dados.pessoasMaisFrequentes,
                (item, index) => [index + 1, item.nome, item.especificacao, item.totalAgendamentos], 4);
        }
    }

    grafico(id, tipo, dados, rotulo, valor, horizontal = false) {
        const container = document.getElementById(id);
        if (this.graficos[id]) {
            this.graficos[id].destroy();
            delete this.graficos[id];
        }
        container.replaceChildren();
        if (!dados || dados.length === 0) {
            container.textContent = 'Sem dados para o período selecionado.';
            return;
        }
        if (typeof ApexCharts === 'undefined') {
            container.textContent = 'Não foi possível carregar a biblioteca de gráficos.';
            return;
        }
        const categorias = dados.map(rotulo);
        const valores = dados.map(valor);
        const options = {
            chart: { type: tipo, height: 400 },
            series: tipo === 'pie' ? valores : [{ name: 'Agendamentos', data: valores }],
            labels: tipo === 'pie' ? categorias : undefined,
            xaxis: tipo === 'pie' ? undefined : { categories: categorias },
            plotOptions: { bar: { horizontal } }
        };
        this.graficos[id] = new ApexCharts(container, options);
        this.graficos[id].render();
    }

    tabela(id, dados, valores, colunas) {
        const corpo = document.getElementById(id);
        corpo.replaceChildren();
        if (!dados || dados.length === 0) {
            const linha = corpo.insertRow();
            const celula = linha.insertCell();
            celula.colSpan = colunas;
            celula.textContent = 'Sem dados para o período selecionado.';
            return;
        }
        dados.forEach((item, index) => {
            const linha = corpo.insertRow();
            valores(item, index).forEach(valor => {
                linha.insertCell().textContent = valor ?? '—';
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.relatorioManager = new RelatorioManager();
});
