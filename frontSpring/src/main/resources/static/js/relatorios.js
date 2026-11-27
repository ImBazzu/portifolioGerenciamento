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
        this.configurarEstudoListas();
        this.carregar();
    }

    configurarEstudoListas() {
        const select = document.getElementById('relatorioListas');
        const button = document.getElementById('carregarEstudoListas');
        const status = document.getElementById('statusEstudoListas');
        const opcoesUrl = document.getElementById('formPdfPassageiros').dataset.opcoesUrl;
        fetch(opcoesUrl, { headers: { Accept: 'application/json' } })
            .then(response => {
                if (!response.ok) throw new Error(`HTTP ${response.status}`);
                return response.json();
            })
            .then(dados => {
                dados.listas.forEach(lista => select.add(new Option(lista.nome, lista.id)));
                status.textContent = 'Opções de listas carregadas.';
            })
            .catch(error => {
                console.error('Erro ao carregar listas do estudo:', error);
                status.textContent = 'Não foi possível carregar as listas.';
            });
        button.addEventListener('click', () => this.carregarEstudoListas());
    }

    async carregarEstudoListas() {
        const inicio = document.getElementById('dataInicio').value;
        const fim = document.getElementById('dataFim').value;
        const status = document.getElementById('statusEstudoListas');
        if (!inicio || !fim || inicio > fim) {
            status.textContent = 'Informe um período válido nos filtros gerais.';
            return;
        }
        status.textContent = 'Carregando estudo...';
        const params = new URLSearchParams({ dataInicio: inicio, dataFim: fim });
        try {
            const response = await fetch(`${this.baseUrl}/passageiros-periodo?${params}`, {
                headers: { Accept: 'application/json' }
            });
            if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
                throw new Error(`HTTP ${response.status}`);
            }
            const viagens = await response.json();
            const selecionadas = new Set(Array.from(
                document.getElementById('relatorioListas').selectedOptions,
                option => option.value));
            const filtradas = viagens.filter(item => selecionadas.size === 0 || selecionadas.has(item.idLista));
            this.renderizarEstudoListas(filtradas);
            status.textContent = `${filtradas.length} agendamento(s) analisado(s) no período.`;
        } catch (error) {
            console.error('Erro ao carregar estudo das listas:', error);
            status.textContent = 'Não foi possível carregar o estudo das listas.';
        }
    }

    renderizarEstudoListas(viagens) {
        const porLista = new Map();
        const porData = new Map();
        const porDestino = new Map();
        viagens.forEach(item => {
            const lista = porLista.get(item.idLista) || { nome: item.lista, viagens: 0, cancelamentos: 0 };
            if (item.cancelado) lista.cancelamentos++;
            else lista.viagens++;
            porLista.set(item.idLista, lista);

            if (item.cancelado) return;
            const dia = porData.get(item.data) || { quantidade: 0, horarios: [] };
            dia.quantidade++;
            dia.horarios.push(`${item.horarioSaida || 'A definir'} → ${item.horarioChegada || 'A definir'} (${item.destino})`);
            porData.set(item.data, dia);

            const destinoKey = `${item.idCidade}:${item.idDestino}`;
            const destino = porDestino.get(destinoKey) || {
                cidade: item.cidade,
                destino: item.destino,
                quantidade: 0
            };
            destino.quantidade++;
            porDestino.set(destinoKey, destino);
        });

        const tabelaListas = document.getElementById('tabelaEstudoListas');
        tabelaListas.replaceChildren();
        const linhasLista = Array.from(porLista.values()).sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
        if (linhasLista.length === 0) {
            this.adicionarLinhaVazia(tabelaListas, 3);
        } else {
            linhasLista.forEach(item => {
                const row = tabelaListas.insertRow();
                [item.nome, item.viagens, item.cancelamentos].forEach(value => row.insertCell().textContent = value);
            });
        }

        const tabelaSemanal = document.getElementById('tabelaEstudoSemanal');
        tabelaSemanal.replaceChildren();
        const linhasData = Array.from(porData.entries()).sort(([a], [b]) => a.localeCompare(b));
        if (linhasData.length === 0) {
            this.adicionarLinhaVazia(tabelaSemanal, 4);
        } else {
            linhasData.forEach(([data, resumo]) => {
                const diaSemana = new Intl.DateTimeFormat('pt-BR', { weekday: 'long', timeZone: 'UTC' })
                    .format(new Date(`${data}T12:00:00Z`));
                const row = tabelaSemanal.insertRow();
                [data, diaSemana, resumo.quantidade, resumo.horarios.sort().join('; ')]
                    .forEach(value => row.insertCell().textContent = value);
            });
        }

        const tabelaDestinos = document.getElementById('tabelaEstudoCidadesDestinos');
        tabelaDestinos.replaceChildren();
        const linhasDestino = Array.from(porDestino.values())
            .sort((a, b) => a.cidade.localeCompare(b.cidade, 'pt-BR')
                || a.destino.localeCompare(b.destino, 'pt-BR'));
        if (linhasDestino.length === 0) {
            this.adicionarLinhaVazia(tabelaDestinos, 3);
            return;
        }
        linhasDestino.forEach(item => {
            const row = tabelaDestinos.insertRow();
            [item.cidade, item.destino, item.quantidade]
                .forEach(value => row.insertCell().textContent = value);
        });
    }

    adicionarLinhaVazia(tabela, colunas) {
        const row = tabela.insertRow();
        const cell = row.insertCell();
        cell.colSpan = colunas;
        cell.textContent = 'Sem dados para os filtros selecionados.';
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
            agendamentos: [['Total de agendamentos', dados.totalAgendamentos], ['Cancelados', dados.agendamentosCancelados]],
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
    configurarPdfPassageiros();
    window.relatorioManager = new RelatorioManager();
});

function configurarPdfPassageiros() {
    const form = document.getElementById('formPdfPassageiros');
    const erro = document.getElementById('erroPdfPassageiros');
    const status = document.getElementById('statusPdfPassageiros');
    const cidade = document.getElementById('pdfCidade');
    const destino = document.getElementById('pdfDestino');
    const botao = document.getElementById('carregarFiltrosPdf');
    let destinos = [];
    let controller;

    function preencher(select, valores, rotuloTodos) {
        const selecionados = new Set(Array.from(select.selectedOptions, option => option.value));
        select.replaceChildren();
        if (rotuloTodos) select.add(new Option(rotuloTodos, ''));
        valores.forEach(item => select.add(new Option(item.nome, item.id, false, selecionados.has(item.id))));
    }

    form.addEventListener('submit', event => {
        const inicio = document.getElementById('pdfInicio').value;
        const fim = document.getElementById('pdfFim').value;
        if (inicio && fim && inicio > fim) {
            event.preventDefault();
            erro.textContent = 'A data de início não pode ser posterior à data de fim.';
            erro.hidden = false;
        }
    });
    cidade.addEventListener('change', () => {
        preencher(destino, destinos.filter(item => !cidade.value || item.idCidade === cidade.value), 'Todos');
    });

    async function carregarOpcoes() {
        if (controller) controller.abort();
        controller = new AbortController();
        const requisicao = controller;
        botao.disabled = true;
        erro.hidden = true;
        status.textContent = 'Carregando opções dos filtros...';
        try {
            const response = await fetch(form.dataset.opcoesUrl, {
                headers: { Accept: 'application/json' }, signal: requisicao.signal
            });
            if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) {
                throw new Error(`HTTP ${response.status}`);
            }
            const dados = await response.json();
            if (requisicao.signal.aborted) return;
            preencher(document.getElementById('pdfListas'), dados.listas);
            preencher(cidade, dados.cidades, 'Todas');
            destinos = dados.destinos;
            preencher(destino, destinos.filter(item => !cidade.value || item.idCidade === cidade.value), 'Todos');
            preencher(document.getElementById('pdfTipoVeiculo'), dados.tiposVeiculo, 'Todos');
            status.textContent = 'Opções carregadas. Sem seleção, o relatório inclui todos.';
        } catch (error) {
            if (error.name === 'AbortError') return;
            console.error('Erro ao carregar filtros do PDF:', error);
            status.textContent = '';
            erro.textContent = 'Não foi possível carregar os filtros. Verifique se o frontend e o backend estão atualizados e em execução; depois tente atualizar as opções.';
            erro.hidden = false;
        } finally {
            if (controller === requisicao) botao.disabled = false;
        }
    }
    botao.addEventListener('click', carregarOpcoes);
    carregarOpcoes();
}
