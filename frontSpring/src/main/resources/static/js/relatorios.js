/**
 * Sistema de Relatórios e Análises
 * Gerencia filtros, requisições à API e visualização de gráficos com ApexCharts
 */

class RelatorioManager {
    constructor() {
        this.baseUrl = '/api/relatorios';
        this.dataAtual = {
            agendamentos: null,
            pessoas: null,
            veiculos: null,
            operacional: null
        };
        this.charts = {};
        this.filtroAtual = {};
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.carregarDadosIniciais();
    }

    setupEventListeners() {
        // Tab navigation
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => this.trocarAba(e.currentTarget));
        });

        // Filter buttons
        document.getElementById('btnFiltrar').addEventListener('click', () => this.filtrar());
        document.getElementById('btnLimpar').addEventListener('click', () => this.limparFiltros());

        // Allow Enter key to trigger filter
        document.getElementById('filtroForm').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.filtrar();
            }
        });
    }

    async carregarDadosIniciais() {
        try {
            console.log('Carregando dados iniciais...');
            await Promise.all([
                this.carregarAgendamentos(),
                this.carregarPessoas(),
                this.carregarVeiculos(),
                this.carregarOperacional()
            ]);
        } catch (error) {
            console.error('Erro ao carregar dados iniciais:', error);
            this.mostrarErro('Erro ao carregar dados. Tente novamente.');
        }
    }

    trocarAba(tabBtn) {
        // Remove active class from all tabs
        document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

        // Add active class to clicked tab
        tabBtn.classList.add('active');
        const tabName = tabBtn.getAttribute('data-tab');
        document.getElementById(tabName).classList.add('active');

        // Redraw charts if needed
        setTimeout(() => {
            Object.values(this.charts).forEach(chart => {
                if (chart && typeof chart.render === 'function') {
                    chart.render();
                }
            });
        }, 100);
    }

    async filtrar() {
        const filtro = this.extrairFiltros();
        this.filtroAtual = filtro;

        try {
            console.log('Filtro aplicado:', filtro);
            await Promise.all([
                this.carregarAgendamentos(filtro),
                this.carregarPessoas(filtro),
                this.carregarVeiculos(filtro),
                this.carregarOperacional(filtro)
            ]);
        } catch (error) {
            console.error('Erro ao filtrar dados:', error);
            this.mostrarErro('Erro ao aplicar filtros. Tente novamente.');
        }
    }

    limparFiltros() {
        document.getElementById('filtroForm').reset();
        this.filtroAtual = {};
        this.carregarDadosIniciais();
    }

    extrairFiltros() {
        const filtro = {};

        const dataInicio = document.getElementById('dataInicio').value;
        const dataFim = document.getElementById('dataFim').value;

        if (dataInicio) filtro.dataInicio = dataInicio;
        if (dataFim) filtro.dataFim = dataFim;

        return filtro;
    }

    construirQueryString(params) {
        const query = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== null && value !== '') {
                query.append(key, value);
            }
        });
        return query.toString();
    }

    // ===== AGENDAMENTOS =====
    async carregarAgendamentos(filtro = {}) {
        try {
            const url = `${this.baseUrl}/agendamentos?${this.construirQueryString(filtro)}`;
            const response = await fetch(url);
            const data = await response.json();
            this.dataAtual.agendamentos = data;
            this.renderizarAgendamentos(data);
        } catch (error) {
            console.error('Erro ao carregar agendamentos:', error);
        }
    }

    renderizarAgendamentos(data) {
        // Renderizar métricas
        this.renderMetricasAgendamentos(data);

        // Renderizar gráficos
        this.renderChartAgendamentosPeriodo(data);
        this.renderChartAgendamentosDestino(data);
        this.renderChartUtilizacaoVeiculo(data);

        // Renderizar tabela
        this.renderTabelaAgendamentos(data);
    }

    renderMetricasAgendamentos(data) {
        const metricsHtml = `
            <div class="metric-card">
                <div class="metric-label">📊 Total de Agendamentos</div>
                <p class="metric-value">${this.formatarNumero(data.totalAgendamentos)}</p>
            </div>
            <div class="metric-card success">
                <div class="metric-label">✅ Realizados</div>
                <p class="metric-value">${this.formatarNumero(data.agendamentosRealizados)}</p>
                <div class="metric-subtitle">${this.formatarPercentual(data.taxaRealizacao)}% de taxa de realização</div>
            </div>
            <div class="metric-card warning">
                <div class="metric-label">❌ Cancelados</div>
                <p class="metric-value">${this.formatarNumero(data.agendamentosCancelados)}</p>
            </div>
            <div class="metric-card info">
                <div class="metric-label">⏳ Pendentes</div>
                <p class="metric-value">${this.formatarNumero(data.agendamentosPendentes)}</p>
            </div>
        `;
        document.getElementById('metricsAgendamentos').innerHTML = metricsHtml;
    }

    renderChartAgendamentosPeriodo(data) {
        const chartId = 'chartAgendamentosPeriodo';
        
        if (!data.agendamentosPorData || data.agendamentosPorData.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const datas = data.agendamentosPorData.map(d => d.data);
        const totais = data.agendamentosPorData.map(d => d.quantidade);
        const realizados = data.agendamentosPorData.map(d => d.realizados);
        const cancelados = data.agendamentosPorData.map(d => d.cancelados);

        const options = {
            chart: { type: 'area', toolbar: { show: true }, sparkline: { enabled: false } },
            xaxis: { categories: datas, title: { text: 'Data' } },
            yaxis: { title: { text: 'Quantidade' } },
            series: [
                { name: 'Total', data: totais, color: '#2563eb' },
                { name: 'Realizados', data: realizados, color: '#16a34a' },
                { name: 'Cancelados', data: cancelados, color: '#dc2626' }
            ],
            tooltip: { theme: 'light' },
            stroke: { curve: 'smooth', width: 2 }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderChartAgendamentosDestino(data) {
        const chartId = 'chartAgendamentosDestino';
        
        if (!data.agendamentosPorDestino || data.agendamentosPorDestino.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const destinos = data.agendamentosPorDestino.slice(0, 10).map(d => d.destino);
        const quantidades = data.agendamentosPorDestino.slice(0, 10).map(d => d.quantidade);

        const options = {
            chart: { type: 'bar', toolbar: { show: true } },
            xaxis: { categories: destinos },
            yaxis: { title: { text: 'Quantidade' } },
            series: [{ name: 'Agendamentos', data: quantidades, color: '#2563eb' }],
            plotOptions: { bar: { horizontal: false, dataLabels: { position: 'top' } } },
            dataLabels: { enabled: true },
            tooltip: { theme: 'light' }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderChartUtilizacaoVeiculo(data) {
        const chartId = 'chartUtilizacaoVeiculo';
        
        if (!data.agendamentosPorVeiculo || data.agendamentosPorVeiculo.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const veiculos = data.agendamentosPorVeiculo.slice(0, 8).map(v => v.veiculo);
        const utilizacao = data.agendamentosPorVeiculo.slice(0, 8).map(v => v.taxaUtilizacao || 0);

        const options = {
            chart: { type: 'radar', toolbar: { show: true } },
            xaxis: { categories: veiculos },
            yaxis: { title: { text: 'Taxa Utilização (%)' } },
            series: [{ name: 'Utilização', data: utilizacao, color: '#ea580c' }],
            plotOptions: { radar: { polygons: { strokeColors: '#e2e8f0' } } },
            tooltip: { theme: 'light' }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderTabelaAgendamentos(data) {
        if (!data.agendamentosPorData || data.agendamentosPorData.length === 0) {
            document.getElementById('tabelaAgendamentosDetalhes').innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 2rem;">Sem dados disponíveis</td></tr>';
            return;
        }

        const html = data.agendamentosPorData.slice(0, 20).map((item, idx) => `
            <tr>
                <td>${this.formatarData(item.data)}</td>
                <td>-</td>
                <td><strong>${item.realizados}</strong></td>
                <td>${item.cancelados}</td>
                <td>${item.quantidade}</td>
            </tr>
        `).join('');

        document.getElementById('tabelaAgendamentosDetalhes').innerHTML = html;
    }

    // ===== PESSOAS =====
    async carregarPessoas(filtro = {}) {
        try {
            const url = `${this.baseUrl}/pessoas?${this.construirQueryString(filtro)}`;
            const response = await fetch(url);
            const data = await response.json();
            this.dataAtual.pessoas = data;
            this.renderizarPessoas(data);
        } catch (error) {
            console.error('Erro ao carregar pessoas:', error);
        }
    }

    renderizarPessoas(data) {
        this.renderMetricasPessoas(data);
        this.renderChartPessoasEspecificacao(data);
        this.renderChartPessoasMaisFrequentes(data);
        this.renderTabelaPessoasFrequentes(data);
    }

    renderMetricasPessoas(data) {
        const metricsHtml = `
            <div class="metric-card">
                <div class="metric-label">👥 Total de Pessoas</div>
                <p class="metric-value">${this.formatarNumero(data.totalPessoas)}</p>
            </div>
            <div class="metric-card success">
                <div class="metric-label">✅ Ativas</div>
                <p class="metric-value">${this.formatarNumero(data.pessoasAtivas)}</p>
            </div>
            <div class="metric-card danger">
                <div class="metric-label">📦 Arquivadas</div>
                <p class="metric-value">${this.formatarNumero(data.pessoasArquivadas)}</p>
            </div>
            <div class="metric-card info">
                <div class="metric-label">📊 Média Acompanhantes</div>
                <p class="metric-value">${this.formatarNumero(data.mediaAcompanhantes || 0)}</p>
            </div>
        `;
        document.getElementById('metricsPessoas').innerHTML = metricsHtml;
    }

    renderChartPessoasEspecificacao(data) {
        const chartId = 'chartPessoasEspecificacao';
        
        if (!data.pessoasPorEspecificacao || data.pessoasPorEspecificacao.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const especificacoes = data.pessoasPorEspecificacao.map(e => e.especificacao);
        const quantidades = data.pessoasPorEspecificacao.map(e => e.quantidade);

        const options = {
            chart: { type: 'pie', toolbar: { show: true } },
            labels: especificacoes,
            series: quantidades,
            tooltip: { theme: 'light' },
            plotOptions: { pie: { donut: { size: '65%' } } }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderChartPessoasMaisFrequentes(data) {
        const chartId = 'chartPessoasMaisFrequentes';
        
        if (!data.pessoasMaisFrequentes || data.pessoasMaisFrequentes.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const pessoas = data.pessoasMaisFrequentes.slice(0, 10).map(p => p.nome);
        const agendamentos = data.pessoasMaisFrequentes.slice(0, 10).map(p => p.totalAgendamentos);

        const options = {
            chart: { type: 'barH', toolbar: { show: true } },
            xaxis: { categories: pessoas },
            series: [{ name: 'Agendamentos', data: agendamentos, color: '#0891b2' }],
            plotOptions: { bar: { horizontal: true, dataLabels: { position: 'right' } } },
            dataLabels: { enabled: true },
            tooltip: { theme: 'light' }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderTabelaPessoasFrequentes(data) {
        if (!data.pessoasMaisFrequentes || data.pessoasMaisFrequentes.length === 0) {
            document.getElementById('tabelaPessoasFrequentes').innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 2rem;">Sem dados disponíveis</td></tr>';
            return;
        }

        const html = data.pessoasMaisFrequentes.slice(0, 10).map((item, idx) => `
            <tr>
                <td>${idx + 1}</td>
                <td><strong>${item.nome}</strong></td>
                <td>${item.especificacao}</td>
                <td>${this.formatarNumero(item.totalAgendamentos)}</td>
            </tr>
        `).join('');

        document.getElementById('tabelaPessoasFrequentes').innerHTML = html;
    }

    // ===== VEÍCULOS =====
    async carregarVeiculos(filtro = {}) {
        try {
            const url = `${this.baseUrl}/veiculos?${this.construirQueryString(filtro)}`;
            const response = await fetch(url);
            const data = await response.json();
            this.dataAtual.veiculos = data;
            this.renderizarVeiculos(data);
        } catch (error) {
            console.error('Erro ao carregar veículos:', error);
        }
    }

    renderizarVeiculos(data) {
        this.renderMetricasVeiculos(data);
        this.renderChartUtilizacaoVeiculos(data);
        this.renderTabelaVeiculosDetalhes(data);
    }

    renderMetricasVeiculos(data) {
        const metricsHtml = `
            <div class="metric-card">
                <div class="metric-label">🚐 Total de Veículos</div>
                <p class="metric-value">${this.formatarNumero(data.totalVeiculos)}</p>
            </div>
            <div class="metric-card success">
                <div class="metric-label">✅ Ativos</div>
                <p class="metric-value">${this.formatarNumero(data.veiculosAtivos)}</p>
            </div>
            <div class="metric-card danger">
                <div class="metric-label">📦 Arquivados</div>
                <p class="metric-value">${this.formatarNumero(data.veiculosArquivados)}</p>
            </div>
            <div class="metric-card info">
                <div class="metric-label">📊 Média Utilização</div>
                <p class="metric-value">${this.formatarPercentual(data.mediaUtilizacao || 0)}%</p>
            </div>
        `;
        document.getElementById('metricsVeiculos').innerHTML = metricsHtml;
    }

    renderChartUtilizacaoVeiculos(data) {
        const chartId = 'chartUtilizacaoVeiculos';
        
        if (!data.utilizacaoVeiculos || data.utilizacaoVeiculos.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const veiculos = data.utilizacaoVeiculos.map(v => v.identificacao);
        const utilizacao = data.utilizacaoVeiculos.map(v => v.taxaUtilizacao || 0);

        const options = {
            chart: { type: 'bar', toolbar: { show: true } },
            xaxis: { categories: veiculos },
            series: [{ name: 'Taxa de Utilização (%)', data: utilizacao, color: '#16a34a' }],
            plotOptions: { bar: { dataLabels: { position: 'top' } } },
            dataLabels: { enabled: true, formatter: (val) => `${val.toFixed(1)}%` },
            tooltip: { theme: 'light' }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderTabelaVeiculosDetalhes(data) {
        if (!data.utilizacaoVeiculos || data.utilizacaoVeiculos.length === 0) {
            document.getElementById('tabelaVeiculosDetalhes').innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 2rem;">Sem dados disponíveis</td></tr>';
            return;
        }

        const html = data.utilizacaoVeiculos.map((item) => `
            <tr>
                <td><strong>${item.identificacao}</strong></td>
                <td>${item.placa}</td>
                <td>${this.formatarNumero(item.totalAgendamentos)}</td>
                <td>${this.formatarPercentual(item.taxaUtilizacao || 0)}%</td>
                <td>${item.capacidade} pessoas</td>
            </tr>
        `).join('');

        document.getElementById('tabelaVeiculosDetalhes').innerHTML = html;
    }

    // ===== OPERACIONAL =====
    async carregarOperacional(filtro = {}) {
        try {
            const url = `${this.baseUrl}/operacional?${this.construirQueryString(filtro)}`;
            const response = await fetch(url);
            const data = await response.json();
            this.dataAtual.operacional = data;
            this.renderizarOperacional(data);
        } catch (error) {
            console.error('Erro ao carregar operacional:', error);
        }
    }

    renderizarOperacional(data) {
        this.renderMetricasOperacional(data);
        this.renderChartEficienciaPorData(data);
        this.renderChartEficienciaPorDestino(data);
    }

    renderMetricasOperacional(data) {
        const metricsHtml = `
            <div class="metric-card info">
                <div class="metric-label">⚙️ Eficiência Operacional</div>
                <p class="metric-value">${this.formatarPercentual(data.eficienciaOperacional || 0)}%</p>
            </div>
            <div class="metric-card success">
                <div class="metric-label">✅ Taxa Realização</div>
                <p class="metric-value">${this.formatarPercentual(data.taxaRealizacaoGeral || 0)}%</p>
            </div>
            <div class="metric-card warning">
                <div class="metric-label">❌ Taxa Cancelamento</div>
                <p class="metric-value">${this.formatarPercentual(data.taxaCancelamento || 0)}%</p>
            </div>
            <div class="metric-card danger">
                <div class="metric-label">⏳ Pendentes</div>
                <p class="metric-value">${this.formatarNumero(data.agendamentosPendentes)}</p>
                <div class="metric-subtitle">Aguardando realização</div>
            </div>
        `;
        document.getElementById('metricsOperacional').innerHTML = metricsHtml;
    }

    renderChartEficienciaPorData(data) {
        const chartId = 'chartEficienciaPorData';
        
        if (!data.eficienciaPorData || data.eficienciaPorData.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const datas = data.eficienciaPorData.map(d => d.data);
        const eficiencia = data.eficienciaPorData.map(d => d.eficiencia);

        const options = {
            chart: { type: 'line', toolbar: { show: true }, sparkline: { enabled: false } },
            xaxis: { categories: datas },
            yaxis: { title: { text: 'Eficiência (%)' }, min: 0, max: 100 },
            series: [{ name: 'Eficiência', data: eficiencia, color: '#16a34a' }],
            stroke: { curve: 'smooth', width: 3 },
            markers: { size: 5 },
            tooltip: { theme: 'light' }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    renderChartEficienciaPorDestino(data) {
        const chartId = 'chartEficienciaPorDestino';
        
        if (!data.eficienciaPorDestino || data.eficienciaPorDestino.length === 0) {
            document.getElementById(chartId).innerHTML = '<p style="text-align: center; padding: 2rem;">Sem dados disponíveis</p>';
            return;
        }

        const destinos = data.eficienciaPorDestino.slice(0, 10).map(d => d.destino);
        const eficiencia = data.eficienciaPorDestino.slice(0, 10).map(d => d.eficiencia);

        const options = {
            chart: { type: 'bar', toolbar: { show: true } },
            xaxis: { categories: destinos },
            yaxis: { title: { text: 'Eficiência (%)' }, min: 0, max: 100 },
            series: [{ name: 'Eficiência', data: eficiencia, color: '#ea580c' }],
            plotOptions: { bar: { dataLabels: { position: 'top' } } },
            dataLabels: { enabled: true, formatter: (val) => `${val.toFixed(1)}%` },
            tooltip: { theme: 'light' }
        };

        if (this.charts[chartId]) {
            this.charts[chartId].updateOptions(options);
            this.charts[chartId].updateSeries(options.series);
        } else {
            this.charts[chartId] = new ApexCharts(document.getElementById(chartId), options);
            this.charts[chartId].render();
        }
    }

    // ===== UTILIDADES =====
    formatarNumero(num) {
        if (!num) return '0';
        return new Intl.NumberFormat('pt-BR').format(num);
    }

    formatarPercentual(num) {
        if (!num) return '0.0';
        return new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 }).format(num);
    }

    formatarData(data) {
        return new Date(data).toLocaleDateString('pt-BR');
    }

    mostrarErro(mensagem) {
        console.error(mensagem);
        // Pode adicionar notificação visual aqui
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.relatorioManager = new RelatorioManager();
});
