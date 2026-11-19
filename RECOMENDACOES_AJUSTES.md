# 🔧 RECOMENDAÇÕES DE AJUSTES E MELHORIAS

## 1️⃣ VALIDAÇÃO DE DATAS (CRÍTICA)

### Problema
Nenhuma validação garante que `dataInicio <= dataFim`.

### Solução A: Backend (Recomendado)

Adicione validação em `FiltroRelatorio`:

```java
public FiltroRelatorio(LocalDate dataInicio, LocalDate dataFim, List<Long> idsPessoa,
                      List<Long> idsVeiculo, List<Long> idsDestino, List<Long> idsEspecificacao,
                      Integer pagina, Integer tamanho) {
    
    // Validação de datas
    if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
        throw new IllegalArgumentException("dataInicio não pode ser posterior a dataFim");
    }
    
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.idsPessoa = idsPessoa;
    this.idsVeiculo = idsVeiculo;
    this.idsDestino = idsDestino;
    this.idsEspecificacao = idsEspecificacao;
    this.pagina = pagina != null ? pagina : 0;
    this.tamanho = tamanho != null ? tamanho : 20;
}
```

### Solução B: Frontend (Complementar)

Adicione em `relatorios.js`:

```javascript
validarFiltros() {
    const dataInicio = new Date(document.getElementById('dataInicio').value);
    const dataFim = new Date(document.getElementById('dataFim').value);
    
    if (dataInicio > dataFim) {
        this.mostrarErro('Data de início não pode ser posterior à data de fim');
        return false;
    }
    return true;
}

async filtrar() {
    if (!this.validarFiltros()) {
        return;
    }
    
    const filtro = this.extrairFiltros();
    this.filtroAtual = filtro;
    // ... resto do código
}
```

---

## 2️⃣ TRATAMENTO DE ERRO HTTP (CRÍTICA)

### Problema
Erros HTTP genéricos não informam a causa real.

### Solução: Custom Exception Handler

Crie `RelatorioExceptionHandler.java`:

```java
@RestControllerAdvice
public class RelatorioExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("INVALID_FILTER", ex.getMessage(), 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(500)
            .body(new ErrorResponse("INTERNAL_ERROR", "Erro ao processar relatório", 500));
    }
}

public class ErrorResponse {
    private String code;
    private String message;
    private int status;

    // Getters e constructors
}
```

Atualize o frontend para capturar erro:

```javascript
async carregarAgendamentos(filtro = {}) {
    try {
        const url = `${this.baseUrl}/agendamentos?${this.construirQueryString(filtro)}`;
        const response = await fetch(url);
        
        if (!response.ok) {
            const error = await response.json();
            this.mostrarErro(error.message || 'Erro ao carregar dados');
            return;
        }
        
        const data = await response.json();
        this.dataAtual.agendamentos = data;
        this.renderizarAgendamentos(data);
    } catch (error) {
        console.error('Erro ao carregar agendamentos:', error);
        this.mostrarErro('Erro de conexão. Tente novamente.');
    }
}
```

---

## 3️⃣ CACHE DE DADOS (ALTA)

### Problema
Cada filtro faz uma nova requisição, mesmo que seja idêntica.

### Solução A: Backend (Cache com @Cacheable)

```java
@Service
@CacheConfig(cacheNames = "relatorios")
public class RelatorioService {
    
    private final RelatorioQueryRepository queryRepository;

    public RelatorioService(RelatorioQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Cacheable(key = "#filtro.hashCode()")
    public EstatisticasAgendamentosDTO obterEstatisticasAgendamentos(FiltroRelatorio filtro) {
        return queryRepository.buscarEstatisticasAgendamentos(filtro);
    }
    
    // ... outros métodos
}
```

Configure cache em `application.yml`:

```yaml
spring:
  cache:
    type: simple
    cache-names: relatorios
```

### Solução B: Frontend (localStorage)

```javascript
class RelatorioManager {
    constructor() {
        // ... resto do código
        this.cacheExpiration = 5 * 60 * 1000; // 5 minutos
    }

    async carregarAgendamentos(filtro = {}) {
        const cacheKey = `agendamentos_${JSON.stringify(filtro)}`;
        const cached = this.getCache(cacheKey);
        
        if (cached) {
            console.log('Dados em cache', cacheKey);
            this.dataAtual.agendamentos = cached;
            this.renderizarAgendamentos(cached);
            return;
        }

        try {
            const url = `${this.baseUrl}/agendamentos?${this.construirQueryString(filtro)}`;
            const response = await fetch(url);
            const data = await response.json();
            
            this.setCache(cacheKey, data);
            this.dataAtual.agendamentos = data;
            this.renderizarAgendamentos(data);
        } catch (error) {
            console.error('Erro ao carregar agendamentos:', error);
        }
    }

    getCache(key) {
        const cached = localStorage.getItem(key);
        if (!cached) return null;

        const { data, timestamp } = JSON.parse(cached);
        if (Date.now() - timestamp > this.cacheExpiration) {
            localStorage.removeItem(key);
            return null;
        }

        return data;
    }

    setCache(key, data) {
        localStorage.setItem(key, JSON.stringify({
            data,
            timestamp: Date.now()
        }));
    }
}
```

---

## 4️⃣ EXPORTAR CSV/PDF (ALTA)

### Novo Endpoint

```java
@GetMapping("/agendamentos/export")
public ResponseEntity<InputStreamResource> exportarAgendamentos(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) List<Long> idsPessoa,
        @RequestParam(defaultValue = "csv") String formato) throws IOException {
    
    FiltroRelatorio filtro = new FiltroRelatorio(dataInicio, dataFim, idsPessoa, null, null, null, 0, 10000);
    
    if ("csv".equals(formato)) {
        ByteArrayInputStream bais = relatorioService.exportarAgendamentosCSV(filtro);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=agendamentos.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(new InputStreamResource(bais));
    }
    
    return ResponseEntity.badRequest().build();
}
```

### Método no Service

```java
public ByteArrayInputStream exportarAgendamentosCSV(FiltroRelatorio filtro) throws IOException {
    EstatisticasAgendamentosDTO dados = obterEstatisticasAgendamentos(filtro);
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (PrintWriter writer = new PrintWriter(out)) {
        writer.println("DESTINO,PESSOA,VEICULO,DATA,STATUS");
        
        for (AgendamentoTabela agendamento : dados.getAgendamentosTabela()) {
            writer.printf("%s,%s,%s,%s,%s%n",
                agendamento.getDestino(),
                agendamento.getPessoa(),
                agendamento.getVeiculo(),
                agendamento.getData(),
                agendamento.getStatus());
        }
    }
    
    return new ByteArrayInputStream(out.toByteArray());
}
```

### Frontend Button

```html
<button class="btn btn-outline-secondary btn-sm ms-2" onclick="relatorioManager.exportarCSV()">
    <i class="bi bi-download me-1"></i> Exportar CSV
</button>
```

```javascript
exportarCSV() {
    const filtro = this.extrairFiltros();
    const queryString = this.construirQueryString({...filtro, formato: 'csv'});
    window.location.href = `${this.baseUrl}/agendamentos/export?${queryString}`;
}
```

---

## 5️⃣ AUTO-REFRESH DE DADOS (ALTA)

### Adicione em RelatorioManager

```javascript
class RelatorioManager {
    constructor() {
        // ... resto do código
        this.autoRefreshInterval = null;
        this.autoRefreshEnabled = false;
    }

    toggleAutoRefresh() {
        if (this.autoRefreshEnabled) {
            clearInterval(this.autoRefreshInterval);
            this.autoRefreshEnabled = false;
            console.log('Auto-refresh desabilitado');
        } else {
            this.autoRefreshInterval = setInterval(() => {
                this.filtrar();
            }, 5 * 60 * 1000); // 5 minutos
            this.autoRefreshEnabled = true;
            console.log('Auto-refresh habilitado (5 min)');
        }
    }
}
```

HTML:

```html
<div class="filter-actions mb-3">
    <button id="btnFiltrar" class="btn btn-primary">
        <i class="bi bi-funnel me-1"></i> Filtrar
    </button>
    <button id="btnLimpar" class="btn btn-outline-secondary">
        <i class="bi bi-x-circle me-1"></i> Limpar
    </button>
    <button id="btnAutoRefresh" class="btn btn-outline-info ms-2">
        <i class="bi bi-arrow-clockwise me-1"></i> Auto-refresh (OFF)
    </button>
</div>
```

JavaScript:

```javascript
document.getElementById('btnAutoRefresh').addEventListener('click', () => {
    relatorioManager.toggleAutoRefresh();
    const btn = document.getElementById('btnAutoRefresh');
    btn.textContent = relatorioManager.autoRefreshEnabled ? 
        '✓ Auto-refresh (ON - 5 min)' : 
        '◯ Auto-refresh (OFF)';
});
```

---

## 6️⃣ COMPARAÇÃO DE PERÍODOS (MÉDIA)

### Novo DTO

```java
public class ComparativoPeriodsDTO {
    private EstatisticasAgendamentosDTO periodoAtual;
    private EstatisticasAgendamentosDTO periodoAnterior;
    private Map<String, Double> variacao; // ex: {"total": 15.5, "confirmados": -5.2}

    // Getters e constructors
}
```

### Novo Endpoint

```java
@GetMapping("/agendamentos/comparativo")
public ResponseEntity<ComparativoPeriodsDTO> compararPeriodos(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
    
    // Calcula intervalo de dias
    long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
    
    // Período anterior com mesmo intervalo
    LocalDate dataAnteriorFim = dataInicio.minusDays(1);
    LocalDate dataAnteriorInicio = dataAnteriorFim.minusDays(dias);
    
    FiltroRelatorio filtroAtual = new FiltroRelatorio(dataInicio, dataFim, null, null, null, null, 0, 20);
    FiltroRelatorio filtroAnterior = new FiltroRelatorio(dataAnteriorInicio, dataAnteriorFim, null, null, null, null, 0, 20);
    
    return ResponseEntity.ok(relatorioService.compararPeriodos(filtroAtual, filtroAnterior));
}
```

---

## 7️⃣ PERMISSÕES POR USUÁRIO (BAIXA)

### Adicionar @PreAuthorize

```java
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    
    @GetMapping("/agendamentos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<EstatisticasAgendamentosDTO> obterEstatisticasAgendamentos(...) {
        // ...
    }

    @GetMapping("/operacional")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstatisticasOperacionaisDTO> obterEstatisticasOperacionais(...) {
        // ...
    }
}
```

---

## 📋 CHECKLIST DE IMPLEMENTAÇÃO

### Implementar agora (Crítica)
- [ ] Validação de datas (backend + frontend)
- [ ] Exception handler (mensagens de erro específicas)
- [ ] Cache (backend ou frontend)

### Implementar próximo sprint (Alta)
- [ ] Exportar CSV/PDF
- [ ] Auto-refresh
- [ ] Responsividade mobile (otimizar gráficos)

### Implementar futuramente (Média/Baixa)
- [ ] Comparação de períodos
- [ ] Filtros salvos (localStorage)
- [ ] Customização de métricas
- [ ] Relatórios agendados
- [ ] Permissões por usuário

---

## 🧪 TESTES PARA ADICIONAR

### Unit Tests (Backend)

```java
@SpringBootTest
class RelatorioServiceTests {
    
    @Autowired
    RelatorioService relatorioService;
    
    @Test
    void testarValidacaoDatas() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FiltroRelatorio(
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 19),
                null, null, null, null, 0, 20
            );
        });
    }
    
    @Test
    void testarPaginacao() {
        FiltroRelatorio filtro = new FiltroRelatorio(null, null, null, null, null, null, 0, 10);
        EstatisticasAgendamentosDTO resultado = relatorioService.obterEstatisticasAgendamentos(filtro);
        
        assertTrue(resultado.getAgendamentosTabela().size() <= 10);
    }
    
    @Test
    void testarCacheHit() {
        FiltroRelatorio filtro = new FiltroRelatorio(null, null, null, null, null, null, 0, 20);
        
        EstatisticasAgendamentosDTO primeira = relatorioService.obterEstatisticasAgendamentos(filtro);
        EstatisticasAgendamentosDTO segunda = relatorioService.obterEstatisticasAgendamentos(filtro);
        
        assertEquals(primeira.getId(), segunda.getId());
    }
}
```

### Integration Tests

```java
@SpringBootTest
class RelatorioIntegrationTests {
    
    @Autowired
    MockMvc mockMvc;
    
    @Test
    void testarEndpointAgendamentos() throws Exception {
        mockMvc.perform(get("/api/relatorios/agendamentos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.metricas").isArray());
    }
    
    @Test
    void testarFiltroComDatas() throws Exception {
        mockMvc.perform(get("/api/relatorios/agendamentos")
            .param("dataInicio", "2026-11-01")
            .param("dataFim", "2026-11-30"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testarErroDataInvalida() throws Exception {
        mockMvc.perform(get("/api/relatorios/agendamentos")
            .param("dataInicio", "2026-11-30")
            .param("dataFim", "2026-11-01"))
            .andExpect(status().isBadRequest());
    }
}
```

---

## 🎯 ORDEM RECOMENDADA

1. **Semana 1**: Validação de datas + Exception handler
2. **Semana 2**: Cache + Testes
3. **Semana 3**: Exportar CSV + Auto-refresh
4. **Semana 4**: Comparação de períodos + Responsividade

---

**Versão**: 1.0.0
**Atualizado**: 19/11/2026
