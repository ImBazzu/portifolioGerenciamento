# 🗺️ Mapa de Dependências e Fluxo de Implementação

## 🔴 ALTA PRIORIDADE - FLUXO CRÍTICO

### Cadeia de Dependências - Emergência

```
SPRINT 1 (Semana 1)
┌──────────────────────────────────────┐
│ emerg-001: Model TipoSaida (2h)      │ ◄─── INÍCIO
│ ├─ Enum: NORMAL, EMERGENCIA, ALTA    │
│ └─ ADD field em Agendamento          │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ emerg-002: JPA Mapping (2h)          │
│ ├─ Migration V18                     │
│ ├─ ALTER TABLE agendamento           │
│ └─ @Enumerated annotation            │
└────────────┬─────────────────────────┘
             │
        ┌────┴────┐
        │          │
        ▼          ▼
    SPRINT 2   SPRINT 2
    ┌────┐     ┌────┐
    │EM3 │     │EM4 │
    │ API│ ◄─► │HND │
    └────┘     └────┘
        │          │
        └────┬─────┘
             │
        ┌────┴────────┐
        │             │
        ▼ SPRINT 3    ▼ SPRINT 3
    ┌────────────┐   ┌────────────┐
    │ emerg-005  │   │ emerg-006  │
    │ Botão UI   │   │ Modal UI   │
    └─────┬──────┘   └────┬───────┘
          │               │
          └───────┬───────┘
                  │
                  ▼
            ┌──────────────┐
            │ emerg-007    │
            │ Testes (3h)  │
            └──────────────┘
            
RESULTADO: Emergência Completa em 3 Sprints (20h)
```

### Cadeia de Dependências - Alta Hospitalar

```
DEPENDE DE: emerg-001, emerg-002 ◄─── IMPORTANTE!

SPRINT 2 (Semana 2-3, pós Emergência)
┌──────────────────────────────────────┐
│ alta-001: Model (1h)                 │
│ ├─ Reutiliza TipoSaida.ALTA         │
│ └─ Mínimas mudanças                  │
└────────────┬─────────────────────────┘
             │
        ┌────┴────┐
        │          │
        ▼          ▼
    ┌────┐     ┌────┐
    │AL2 │     │AL3 │
    │API │ ◄─► │HND │
    └────┘     └────┘
        │          │
        └────┬─────┘
             │
        ┌────┴────────┐
        │             │
        ▼ SPRINT 3    ▼ SPRINT 3
    ┌────────────┐   ┌────────────┐
    │ alta-004   │   │ alta-005   │
    │ Botão UI   │   │ Modal UI   │
    └─────┬──────┘   └────┬───────┘
          │               │
          └───────┬───────┘
                  │
                  ▼
            ┌──────────────┐
            │ alta-006     │
            │ Testes (3h)  │
            └──────────────┘
            
RESULTADO: Alta Hospitalar em 2 Sprints (17h, pós Emerg)
TOTAL Emerg + Alta: 37h em 3 Sprints
```

### Cadeia de Dependências - Cancelamento

```
SPRINT 1 (Semana 1 - PARALELO com Emergência)
┌──────────────────────────────────────┐
│ canc-001: Model StatusViagem (2h)    │ ◄─── INÍCIO
│ ├─ Enum: ATIVO, CANCELADO            │
│ ├─ ADD fields: status, motivo, data  │
│ └─ Soft-delete pattern               │
└────────────┬─────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ canc-002: JPA Mapping (2h)           │
│ ├─ Migration V19                     │
│ └─ 3 novas colunas                   │
└────────────┬─────────────────────────┘
             │
        ┌────┴──────────┐
        │               │
        ▼ SPRINT 2      ▼ SPRINT 1
    ┌────────────┐  ┌──────────────┐
    │ canc-003   │  │ canc-005     │
    │ API (3h)   │  │ Query (3h)   │
    │ Endpoint   │  │ Atualizar    │
    └─────┬──────┘  │ findAll()    │
          │         └──────┬───────┘
          │                │
          └────────┬───────┘
                   │
            ┌──────┴──────┐
            │             │
        ┌───▼──┐      ┌───▼──┐
        │ CN4  │      │ CN3  │
        │ HND  │      │ API  │
        └───┬──┘      └───┬──┘
            │             │
        ┌───┴────────┬────┴────┐
        │            │         │
        ▼ SPRINT 3   ▼ SPRINT 3 ▼ SPRINT 1
    ┌────────┐  ┌────────┐  ┌────────┐
    │ canc-006 │canc-007│canc-008│
    │ Botão   │Modal  │Testes  │
    └────────┘ └────────┘ └────────┘
            
RESULTADO: Cancelamento Completo em 3 Sprints (19h)
NOTA: Pode rodar em paralelo com Emergência (Sem 1-2)
```

---

## 🟡 NORMAL PRIORIDADE - MICRO-FLUXOS

### Fluxo: Visualização Residência (RÁPIDO)
```
SPRINT 4 (Semana 4+, independente)
┌────────────────────────────────┐
│ resid-001: Model (0.5h)         │ ◄─── START
│ Usar Pessoa.buscarNaResidencia │
└────────────┬────────────────────┘
             │
             ▼
        ┌────────────────┐
        │ resid-002:     │
        │ API (1h)       │
        │ Retornar flag  │
        └────────┬───────┘
                 │
            ┌────┴─────────┐
            │              │
            ▼              ▼
        ┌────────┐    ┌────────┐
        │resid-03│   │resid-04│
        │ UI (2h)│   │ Filtro │
        │ Ícone  │   │  (2h)  │
        └────┬───┘   └────┬───┘
             │            │
             └──────┬─────┘
                    │
                    ▼
            ┌───────────────┐
            │ resid-005     │
            │ Teste (1h)    │
            └───────────────┘
            
TOTAL: 6.5h, pode fazer em 2-3 dias
```

### Fluxo: Ordenação Visual (SIMPLES)
```
SPRINT 4 (Semana 4+, independente)
┌────────────────────────┐
│ ordem-001: API (1h)    │ ◄─── START
│ Query ordenada         │
└────────────┬───────────┘
             │
        ┌────┴────┬──────┐
        │          │      │
        ▼          ▼      ▼
    ┌────────┐ ┌────┐ ┌────┐
    │ordem-02│ │ord3│ │ord4│
    │ UI (2h)│ │UI  │ │TST │
    │Ordernar│ │Bad │ │(1h)│
    └────┬───┘ │ges │ └────┘
         │     │(2h)│
         └─────┴────┘
         
TOTAL: 6h, pode fazer em 1-2 dias
```

### Fluxo: Confirmação de Saída (MÉDIO)
```
SPRINT 4 (Semana 4+, independente)
┌────────────────────────────────┐
│ conf-001: Model (1h)           │ ◄─── START
│ ADD: confirmadoSaida, timestamp│
└────────────┬────────────────────┘
             │
             ▼
        ┌────────────────┐
        │ conf-002:      │
        │ JPA (1h)       │
        │ Coluna BD      │
        └────────┬───────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼ SPRINT 2        ▼ SPRINT 1
    ┌────────────┐   ┌──────────────┐
    │ conf-003   │   │ conf-004     │
    │ API (2h)   │   │ Handler (2h) │
    │ Endpoint   │   │ Lógica       │
    └────┬───────┘   └──────┬───────┘
         │                  │
         └──────┬───────────┘
                │
                ▼
            ┌──────────────┐
            │ conf-005     │
            │ UI (2h)      │
            │ Botão Ok     │
            └──────┬───────┘
                   │
                   ▼
            ┌──────────────┐
            │ conf-006     │
            │ Teste (2h)   │
            └──────────────┘
            
TOTAL: 10h, pode fazer em 3-4 dias
```

---

## 🌳 ÁRVORE DE DEPENDÊNCIAS COMPLETA

```
                        SPRINT 1 (SEM 1-2)
                        
        emerg-001 (2h)                  canc-001 (2h)
           │                               │
           ├─→ emerg-002 (2h) ┐           ├─→ canc-002 (2h)
           │                  │           │       │
           ├─→ emerg-004 (3h) ├─→ SPRINT2 │       ├─→ canc-005 (3h)
           │                  │   (25h)   │       │
           │       ┌──────────┘           ├─→ canc-004 (2h)
           │       │                      │       
           └──┐    └─────┬──────────┐     └───┐
              │          │          │         │
         SPRINT2    emerg-003    emerg-004   SPRINT2
         emerg-003    (4h)        canc-003
         canc-003    API          (3h)
         canc-005    Handler      API
         (25h)       │            │
              ┌──────┴────────────┴───┐
              │                       │
         SPRINT3                 SPRINT3
         emerg-005, 006          canc-006, 007
         (6h UI)                 (4h UI)
         emerg-007               canc-008
         (3h TST)                (3h TST)
         alta-004, 005           
         (6h UI)                 
         alta-006                
         (3h TST)                

                SPRINT 2 (PÓS SPRINT 1)
                
        alta-001 (1h) ◄─── DEPENDE: emerg-001
           │
           ├─→ alta-002 (4h) ◄─── DEPENDE: emerg-002
           │       │
           ├─→ alta-003 (3h)
           │       │
           └───────┴──→ SPRINT 3
                       alta-004, 005 (6h UI)
                       alta-006 (3h TST)

                SPRINT 4+ (INDEPENDENTES)
                
        resid-001 ─────→ resid-002 ─────┬──→ resid-003, 004 ─→ resid-005
        (0.5h)          (1h)             │   (4h UI)             (1h)
                                         └───────────────────────┘
        
        ordem-001 ─────→ ordem-002 ─────→ ordem-003
        (1h)            (2h)              (2h)
           └────────────→ ordem-004 (1h)
        
        conf-001 ─────→ conf-002 ───┬──→ conf-003 (2h API)
        (1h)           (1h JPA)      │       │
                                     └──→ conf-004 (2h HND)
                                             │
                                             ▼
                                        conf-005 (2h UI)
                                             │
                                             ▼
                                        conf-006 (2h TST)

        [OUTRAS REQUISIÇÕES - Ver TASK_REFERENCE.md]
```

---

## 📊 Matriz de Dependências Críticas

| Task Destino | Depende De | Aguarda Semanas | Criticidade |
|---|---|---|---|
| emerg-002 | emerg-001 | 0 (mesma semana) | 🔴 Crítica |
| emerg-003 | emerg-002 | 1 | 🔴 Crítica |
| emerg-004 | emerg-001 | 0 (pode paralelo) | 🟡 Alta |
| alta-001 | emerg-001 | 1 | 🔴 Crítica |
| alta-002 | emerg-002 | 1 | 🔴 Crítica |
| canc-002 | canc-001 | 0 (mesma semana) | 🔴 Crítica |
| canc-003 | canc-002 | 1 | 🟡 Alta |
| canc-005 | canc-002 | 1 | 🟡 Alta |
| conf-002 | conf-001 | 0 (mesma semana) | 🟡 Alta |
| perm-002 | perm-001 | 0 (mesma semana) | 🟡 Alta |

---

## ⚠️ Riscos e Pontos de Atenção

### SPRINT 1
- **Risco**: Mudanças no banco de dados (emerg-002, canc-002) podem impactar outras features
- **Mitigação**: Fazer rollback scripts prontos, testar em staging
- **Checkpoint**: Fim da Semana 1 - validar migrations funcionando

### SPRINT 2
- **Risco**: Handlers com lógica complexa (emerg-004, canc-004) precisam testes sólidos
- **Mitigação**: Code review obrigatório, testes de integração
- **Checkpoint**: APIs prontas e testadas

### SPRINT 3
- **Risco**: UI com muitos eventos e validações (emerg-005, 006, canc-006, 007)
- **Mitigação**: Componentes reutilizáveis, design system consistente
- **Checkpoint**: UI aceita pelo Product Owner

### BLOQUEADORES POTENCIAIS
1. ❌ BD em produção sem backup (Sem 1-2)
2. ❌ Falta de ambiente de staging (Sem 2-3)
3. ❌ Comunicação inadequada entre Backend/Frontend (Sem 3+)
4. ❌ Mudanças de requisitos no meio do Sprint

---

## ✅ Checklist de Handoff entre Sprints

### Fim Sprint 1 → Início Sprint 2
- [ ] Migrations rodaram sem erro em staging
- [ ] Models compilam e possuem testes
- [ ] Code review aprovado por Tech Lead
- [ ] Nenhuma breaking change em APIs existentes
- [ ] Documentação de banco de dados atualizada

### Fim Sprint 2 → Início Sprint 3
- [ ] Todos os endpoints retornam HTTP status correto
- [ ] Testes de integração passando (>80% cobertura)
- [ ] Performance validada (queries < 200ms)
- [ ] Documentação de API atualizada (Swagger/OpenAPI)
- [ ] Preparação de mock/fixtures para UI team

### Fim Sprint 3 → Início Sprint 4
- [ ] UI testada em desktop e tablet
- [ ] Sem console errors/warnings
- [ ] Acessibilidade validada (WCAG 2.1 AA)
- [ ] Performance frontend OK (Lighthouse > 80)
- [ ] User stories validadas pelo PO

---

## 🎯 Próximos Passos

1. **Esta semana**: Leia [SPRINT_1_QUICKSTART.md](./SPRINT_1_QUICKSTART.md)
2. **Próxima semana**: Comece emerg-001 (Model)
3. **Fin Sem 1**: Comece emerg-002 (DB) + canc-001/002 (paralelo)
4. **Sem 2**: APIs prontas e testadas
5. **Sem 3**: UI completa e validada

---

**Documento Gerado**: 2026-11-26  
**Atualizar a cada Sprint finalizado**
