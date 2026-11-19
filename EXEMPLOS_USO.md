# 📖 EXEMPLOS DE USO - SISTEMA DE RELATÓRIOS

## 🚀 Iniciando Rápido

### 1. Compilar e Executar

```bash
# Terminal 1: Compilar e iniciar servidor
cd backend
mvn clean install
mvn spring-boot:run

# Aguardar até ver: "Started... in X.XXX seconds"
```

### 2. Acessar a Página

```
Navegador: http://localhost:8080/relatorios
```

### 3. Testar Endpoints (Terminal 2)

```bash
# Agendamentos
curl "http://localhost:8080/api/relatorios/agendamentos"

# Pessoas
curl "http://localhost:8080/api/relatorios/pessoas"

# Veículos
curl "http://localhost:8080/api/relatorios/veiculos"

# Operacional
curl "http://localhost:8080/api/relatorios/operacional"
```

---

## 🔍 Exemplos de Filtros

### Filtro 1: Apenas últimos 30 dias

```bash
curl "http://localhost:8080/api/relatorios/agendamentos?dataInicio=2024-10-20&dataFim=2024-11-19"
```

**Resposta esperada:**
```json
{
  "totalAgendamentos": 450,
  "agendamentosRealizados": 380,
  "agendamentosCancelados": 35,
  "agendamentosPendentes": 35,
  "taxaRealizacao": 84.4,
  ...
}
```

### Filtro 2: Pessoa específica

```bash
curl "http://localhost:8080/api/relatorios/agendamentos?idsPessoa=1&idsPessoa=2"
```

### Filtro 3: Veículo específico

```bash
curl "http://localhost:8080/api/relatorios/agendamentos?idsVeiculo=5"
```

### Filtro 4: Paginação

```bash
curl "http://localhost:8080/api/relatorios/agendamentos?pagina=0&tamanho=50"
```

### Filtro 5: Combinado

```bash
curl "http://localhost:8080/api/relatorios/agendamentos?dataInicio=2024-11-01&dataFim=2024-11-30&idsVeiculo=1&pagina=0&tamanho=20"
```

---

## 🎨 Exemplos de Uso na Interface

### Cenário 1: Consultar Agendamentos do Mês

1. Clique na aba **📅 Agendamentos**
2. Preencha:
   - Data Início: `01/11/2024`
   - Data Fim: `30/11/2024`
3. Clique em **🔍 Filtrar**
4. Veja:
   - 4 cards com métricas
   - Gráfico de série temporal
   - Gráfico de destinos
   - Gráfico de utilização de veículos
   - Tabela com detalhes

### Cenário 2: Analisar Pessoas Mais Frequentes

1. Clique na aba **👥 Pessoas**
2. Especifique período (opcional):
   - Data Início: `01/11/2024`
   - Data Fim: `30/11/2024`
3. Clique em **🔍 Filtrar**
4. Analise:
   - Gráfico de pizza (distribuição por especificação)
   - Gráfico de barras (top 10 pessoas)
   - Tabela com nomes e agendamentos

### Cenário 3: Monitorar Utilização de Veículos

1. Clique na aba **🚐 Veículos**
2. Clique em **🔍 Filtrar** (sem filtros = todos os veículos)
3. Veja:
   - Taxa média de utilização
   - Gráfico de barras com utilização por veículo
   - Tabela com capacidade e detalhes

### Cenário 4: Verificar Eficiência Operacional

1. Clique na aba **⚙️ Operacional**
2. Preencha período:
   - Data Início: `01/11/2024`
   - Data Fim: `30/11/2024`
3. Clique em **🔍 Filtrar**
4. Monitore:
   - Eficiência operacional geral (%)
   - Taxa de realização
   - Taxa de cancelamento
   - Gráficos de tendência

### Cenário 5: Criar Consulta Customizada

1. Preencha múltiplos filtros:
   - Data Início: `15/11/2024`
   - Data Fim: `20/11/2024`
   - Pessoa: Selecione 2-3 pessoas
   - Veículo: Selecione 1-2 veículos
   - Destino: Selecione destinos
2. Clique **🔍 Filtrar**
3. Dados se atualizam automaticamente em todas as abas

---

## 💻 Exemplos de JavaScript (Console)

### Ver dados carregados

```javascript
// Abrir DevTools (F12) e colar no console:

// Ver todos os dados de agendamentos
console.log(window.relatorioManager.dataAtual.agendamentos);

// Ver dados de pessoas
console.log(window.relatorioManager.dataAtual.pessoas);

// Ver filtro atual
console.log(window.relatorioManager.filtroAtual);

// Ver charts registrados
console.log(Object.keys(window.relatorioManager.charts));
```

### Forçar reload

```javascript
// Recarregar todas as abas
window.relatorioManager.carregarDadosIniciais();

// Recarregar apenas agendamentos
window.relatorioManager.carregarAgendamentos();

// Limpar filtros e recarregar
window.relatorioManager.limparFiltros();
```

### Debugar requisições

```javascript
// Ver requisição que está sendo feita
console.log(window.relatorioManager.baseUrl);

// Ver URL construída
const filtro = { dataInicio: '2024-11-01', dataFim: '2024-11-30' };
console.log(`${window.relatorioManager.baseUrl}/agendamentos?${window.relatorioManager.construirQueryString(filtro)}`);
```

---

## 🧪 Teste de Stress

### Teste 1: Muitos filtros

```
1. Preencher todos os 4 campos de seleção múltipla
2. Adicionar intervalo de datas longo (1 ano)
3. Clicar Filtrar 5x rapidamente
4. Sistema deve responder sem travar
```

### Teste 2: Zoom nos Gráficos

```
1. Duplo clique em qualquer gráfico
2. Arrastar para fazer zoom
3. Clique direito para resetar
4. Legendas devem atualizar
```

### Teste 3: Trocar Abas Rapidamente

```
1. Clicar em abas diferentes 10x
2. Sem lag ou erros
3. Dados devem permanecer consistentes
```

### Teste 4: Responsividade

```
1. F12 → Toggle Device Toolbar
2. Testar em 375px (Mobile)
3. Testar em 768px (Tablet)
4. Testar em 1920px (Desktop)
5. Verificar se tudo fica legível
```

---

## 📊 Exemplo de Resposta Completa

### GET `/api/relatorios/agendamentos`

```json
{
  "totalAgendamentos": 1500,
  "agendamentosRealizados": 1200,
  "agendamentosCancelados": 150,
  "agendamentosPendentes": 150,
  "taxaRealizacao": 80.0,
  "agendamentosPorData": [
    {
      "data": "2024-11-01",
      "quantidade": 45,
      "realizados": 40,
      "cancelados": 2
    },
    {
      "data": "2024-11-02",
      "quantidade": 48,
      "realizados": 42,
      "cancelados": 3
    }
  ],
  "agendamentosPorDestino": [
    {
      "destino": "Centro",
      "quantidade": 500
    },
    {
      "destino": "Bairro A",
      "quantidade": 350
    },
    {
      "destino": "Bairro B",
      "quantidade": 300
    }
  ],
  "agendamentosPorVeiculo": [
    {
      "veiculo": "Microônibus 01",
      "quantidade": 120,
      "taxaUtilizacao": 85.5
    },
    {
      "veiculo": "Van 02",
      "quantidade": 95,
      "taxaUtilizacao": 72.0
    }
  ],
  "agendamentosPorEspecificacao": {
    "Idoso": 500,
    "Deficiente Físico": 400,
    "Padrão": 600
  }
}
```

### GET `/api/relatorios/pessoas`

```json
{
  "totalPessoas": 850,
  "pessoasAtivas": 750,
  "pessoasArquivadas": 100,
  "mediaAcompanhantes": 1.3,
  "pessoasMaisFrequentes": [
    {
      "nome": "Maria Silva",
      "totalAgendamentos": 45,
      "especificacao": "Idoso"
    },
    {
      "nome": "João Santos",
      "totalAgendamentos": 38,
      "especificacao": "Deficiente Físico"
    }
  ],
  "pessoasPorEspecificacao": [
    {
      "especificacao": "Idoso",
      "quantidade": 350
    },
    {
      "especificacao": "Deficiente Físico",
      "quantidade": 250
    },
    {
      "especificacao": "Padrão",
      "quantidade": 250
    }
  ],
  "pessoasPorLocalidade": {
    "Centro": 200,
    "Bairro A": 150,
    "Bairro B": 100
  }
}
```

---

## 🎓 Tutoriais Rápidos

### Como adicionar um novo gráfico?

1. No backend (`RelatorioJpaRepository.java`):
   ```java
   // Adicione método para buscar dados
   private List<DadosParaGrafico> buscarDadosParagrafico() {
       // Query e construção de dados
   }
   ```

2. No frontend (`relatorios.js`):
   ```javascript
   // Adicione método na classe
   renderChartMeuGrafico(data) {
       const options = { /* config ApexCharts */ };
       this.charts['chartMeuGrafico'] = new ApexCharts(...);
       this.charts['chartMeuGrafico'].render();
   }
   ```

3. No HTML:
   ```html
   <div id="chartMeuGrafico" style="height: 400px;"></div>
   ```

### Como adicionar um novo filtro?

1. No HTML (`relatorios.html`):
   ```html
   <input type="text" id="novoFiltro" placeholder="Filtro">
   ```

2. No JavaScript:
   ```javascript
   const novoFiltro = document.getElementById('novoFiltro').value;
   // Adicione ao objeto filtro
   ```

3. No backend:
   ```java
   // Adicione parâmetro ao método
   public ResponseEntity<DTO> endpoint(..., String novoFiltro)
   ```

---

## 🐛 Problemas Comuns e Soluções

### Problema: "Cannot read property 'render' of undefined"
**Solução**: ApexCharts não carregou. Aguarde 2s e tente novamente.

### Problema: Gráficos brancos
**Solução**: F12 → Console, procure por erros. Verifique dados da API.

### Problema: Filtro não funciona
**Solução**: 
1. F12 → Network tab
2. Verificar se requisição foi feita
3. Verificar se retornou status 200
4. Verificar se dados estão no response

### Problema: Lentidão
**Solução**:
1. Verificar DevTools → Performance
2. Verificar queries do banco
3. Adicionar índices se necessário

---

## 📱 Teste em Dispositivos Reais

### iOS Safari
```
1. Abrir em iPhone
2. Verificar se gráficos renderizam
3. Testar touch zoom
4. Verificar overflow
```

### Android Chrome
```
1. Abrir em Android
2. Verificar responsividade
3. Testar filtros
4. Verificar performance
```

---

## ✅ Validação Final

Antes de considerar completo, execute:

```bash
# Terminal 1
cd backend && mvn spring-boot:run

# Terminal 2
# Testar endpoints
curl http://localhost:8080/api/relatorios/agendamentos
curl http://localhost:8080/api/relatorios/pessoas
curl http://localhost:8080/api/relatorios/veiculos
curl http://localhost:8080/api/relatorios/operacional

# Navegador
http://localhost:8080/relatorios

# Verificar
- [ ] Página carrega
- [ ] Gráficos aparecem
- [ ] Filtros funcionam
- [ ] Sem console errors
- [ ] Sem console warnings
- [ ] Responsivo em mobile
```

---

**Pronto para usar! 🚀**
