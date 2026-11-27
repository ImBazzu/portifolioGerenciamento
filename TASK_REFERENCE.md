# 📋 Referência Rápida - Tasks Breakdown

**Gerado em**: 2026-11-26  
**Total de Requisições**: 16  
**Total de Tasks**: 90  
**Total de Horas Estimadas**: ~212h

---

## 🔴 ALTA PRIORIDADE - RESUMO EXECUTIVO

| Requisição | Tasks | Horas | Status | Início Recomendado |
|---|---|---|---|---|
| **Motivo 02 - Emergência** | 7 | 20h | Planejado | Semana 1 |
| **Motivo 03 - Alta Hospitalar** | 6 | 17h | Planejado | Semana 2 (pós Emerg) |
| **Cancelamento** | 8 | 19h | Planejado | Semana 1 |
| **TOTAL ALTA** | **21** | **56h** | - | - |

### Requisição: Motivo 02 - Emergência (20h)
```
emerg-001: Model: Adicionar tipo de saída (Emergência) [2h]
emerg-002: JPA: Mapear tipo de saída no Agendamento [2h]
emerg-003: API: Endpoint para abrir veículo de emergência [4h]
emerg-004: Handler: Criar AbrirVeiculoEmergenciaHandler [3h]
emerg-005: UI: Botão Emergência na tela de agendamento [3h]
emerg-006: UI: Modal de agendamento rápido (Emergência) [3h]
emerg-007: Teste: Validar agendamento de emergência [3h]
```

### Requisição: Motivo 03 - Alta Hospitalar (17h)
```
alta-001: Model: Alta Hospitalar no domain [1h] *depende: emerg-001
alta-002: API: Endpoint para abrir veículo de alta [4h] *depende: emerg-002
alta-003: Handler: Criar AbrirVeiculoAltaHandler [3h]
alta-004: UI: Botão Alta na tela de agendamento [3h]
alta-005: UI: Modal de agendamento rápido (Alta) [3h]
alta-006: Teste: Validar agendamento de alta [3h]
```

### Requisição: Cancelamento (19h)
```
canc-001: Model: Adicionar status de cancelamento [2h]
canc-002: JPA: Adicionar coluna status_viagem [2h]
canc-003: API: Endpoint cancelar viagem [3h]
canc-004: Handler: CancelarViagemHandler [2h]
canc-005: Query: Listar viagens (excluindo canceladas) [3h]
canc-006: UI: Botão cancelar na linha do agendamento [2h]
canc-007: UI: Modal de cancelamento com motivo [2h]
canc-008: Teste: Validar cancelamento soft-delete [3h]
```

---

## 🟡 NORMAL PRIORIDADE - RESUMO EXECUTIVO

| Requisição | Tasks | Horas | Complexidade | ROI |
|---|---|---|---|---|
| Permissões Listas | 9 | 21h | Alta | Médio |
| Relatório Listas | 7 | 18h | Média | Baixo |
| Análise Cidades | 6 | 16h | Média | Baixo |
| Relatório Semana | 6 | 15h | Média | Baixo |
| Página Usuário | 6 | 14h | Baixa | Alto |
| PWA | 6 | 13h | Média | Alto |
| Confirmação Saída | 6 | 10h | Baixa | Alto |
| Horário Noturno | 5 | 10h | Média | Alto |
| Motivo 01 - Ajuste | 4 | 10h | Média | Médio |
| Tablet Responsivo | 4 | 9h | Baixa | Médio |
| Visualização Residência | 5 | 6.5h | Baixa | Alto |
| Ordenação Visual | 4 | 6h | Baixa | Alto |
| **TOTAL NORMAL** | **69** | **156h** | - | - |

---

## 🎯 Por Camada de Implementação

### Backend - Domain (10 tasks, ~16h)
```
emerg-001: Model: Adicionar tipo de saída [2h]
canc-001: Model: Adicionar status de cancelamento [2h]
conf-001: Model: Campo de confirmação de saída [1h]
alta-001: Model: Alta Hospitalar [1h]
noturno-001: Model: Regra de agrupamento [2h]
perm-001: Model: Mapping User-Lista [2h]
resid-001: Model: Usar campo existente [0.5h]
ordem-001: API: Query ordenação [1h] [NOTA: pode ser API]
cidade-001: Service: Análise destinos [3h] [NOTA: pode ser Application]
rel-lista-001: Agregações [3h] [NOTA: pode ser Application]
rel-semana-001: Agregações semana [3h] [NOTA: pode ser Application]
```

### Backend - Database (5 tasks, ~11h)
```
emerg-002: JPA: Mapear tipo_saida [2h]
canc-002: JPA: Adicionar status_viagem [2h]
conf-002: JPA: Adicionar confirmado_saida [1h]
perm-002: JPA: Tabela usuario_listas [2h]
[Implícito em outras tasks]
```

### Backend - Application/Handler (11 tasks, ~32h)
```
emerg-004: Handler: AbrirVeiculoEmergencia [3h]
canc-004: Handler: CancelarViagem [2h]
conf-004: Handler: ConfirmarSaida [2h]
alta-003: Handler: AbrirVeiculoAlta [3h]
perm-004: Handler: GerenciarListasUsuario [2h]
user-003: Handler: AtualizarPerfilUsuario [2h]
rel-lista-001: Service: Agregações lista [3h]
rel-semana-001: Service: Agregações semana [3h]
cidade-001: Service: Análise destinos [3h]
ajust-003: Handler: AjustarPassageiros [2h]
perm-005: Query: Listar listas usuário [2h]
```

### Backend - API/Adapter (11 tasks, ~31h)
```
emerg-003: API: Endpoint emergência [4h]
canc-003: API: Endpoint cancelar [3h]
conf-003: API: Endpoint confirmar saída [2h]
alta-002: API: Endpoint alta [4h]
perm-003: API: Endpoint gerenciar listas [3h]
perm-006: API: Filtrar listas no agendamento [2h]
user-001: API: GET /api/usuarios/{id} [2h]
user-002: API: PUT /api/usuarios/{id} [3h]
rel-lista-002: API: GET /api/relatorios/listas/{id} [3h]
rel-lista-003: API: GET /api/relatorios/listas [3h]
rel-semana-002: API: GET /api/relatorios/semana [3h]
cidade-002: API: GET /api/relatorios/destinos [2h]
ajust-002: API: PUT /api/agendamentos/ajustar [3h]
ordem-001: API: Query ordenação [1h]
resid-002: API: Retornar flag residência [1h]
noturno-002: API: Query com grupo noturno [2h]
```

### Backend - Test (10 tasks, ~21h)
```
emerg-007: Teste: Agendamento emergência [3h]
canc-008: Teste: Cancelamento soft-delete [3h]
conf-006: Teste: Confirmação saída [2h]
alta-006: Teste: Agendamento alta [3h]
perm-009: Teste: Permissões listas [2h]
user-006: Teste: Atualização perfil [2h]
rel-lista-007: Teste: Cálculo métricas [2h]
rel-semana-006: Teste: Agregação por dia [2h]
cidade-006: Teste: Análise destinos [2h]
ajust-004: Teste: Ajuste passageiros [2h]
resid-005: Teste: Filtro residência [1h]
noturno-005: Teste: Agrupamento noturno [1h]
ordem-004: Teste: Ordenação correta [1h]
```

### Frontend (25 tasks, ~65h)
```
emerg-005: Botão Emergência [3h]
emerg-006: Modal emergência [3h]
canc-006: Botão cancelar [2h]
canc-007: Modal cancelamento [2h]
conf-005: Botão Ok confirmação [2h]
alta-004: Botão Alta [3h]
alta-005: Modal alta [3h]
ajust-001: Modal ajuste separação [3h]
ajust-002: API integração [3h] [NOTE: poderia ser Backend]
noturno-003: Dividir em 2 seções [3h]
noturno-004: Marcação visual [2h]
ordem-002: Ordenar veículos [2h]
ordem-003: Badges de horário [2h]
resid-003: Ícone residência [2h]
resid-004: Filtro residência [2h]
perm-007: Tela gerenciamento listas [4h]
perm-008: Filtrar listas [2h]
user-004: Tela perfil [3h]
user-005: Modal editar perfil [2h]
rel-lista-004: Seleção listas [2h]
rel-lista-005: Exibir metrics [3h]
rel-lista-006: Filtros período [2h]
rel-semana-003: Gráfico semana [3h]
rel-semana-004: Tabela horários [2h]
rel-semana-005: Filtros relatório [2h]
cidade-003: Seção destinos [3h]
cidade-004: Gráfico destinos [2h]
cidade-005: Integração mapa [4h]
tablet-001: Audit responsivo [2h]
tablet-002: Grid layout [3h]
tablet-003: Testar [2h]
tablet-004: Touch targets [2h]
pwa-001: manifest.json [2h]
pwa-002: Service Worker [4h]
pwa-003: Ícones [2h]
pwa-004: Tema cores [1h]
pwa-005: Testar Android [2h]
pwa-006: Testar iOS [2h]
pwa-007: Teste offline [1h]
```

---

## 📅 Timeline Recomendado

```
MÊS 1 (4 semanas)
├─ Sprint 1 (Sem 1-2): Alta Prioridade Foundation [56h]
│  ├─ Semana 1: Models + DB Migrations [9h]
│  └─ Semana 2: Handlers + APIs [25h]
│
├─ Sprint 2 (Sem 2-3): Alta Prioridade Continuação [32h]
│  ├─ Semana 2: APIs restantes [15h]
│  └─ Semana 3: Testes [17h]
│
└─ Sprint 3 (Sem 3-4): Alta Prioridade UI [25h]
   ├─ Semana 3: UI Emergência + Alta [12h]
   └─ Semana 4: UI Cancelamento + testes [13h]

MÊS 2-3 (8 semanas)
├─ Sprint 4-5: Quick Wins (20h)
│  ├─ Visualização Residência [6.5h]
│  ├─ Ordenação Visual [6h]
│  └─ Confirmação Saída [10h]
│
├─ Sprint 6-7: Permissões (21h)
│  └─ Full stack: Backend + Frontend [21h]
│
├─ Sprint 8: Responsividade (9h)
│  └─ Frontend layout optimization
│
└─ Sprint 9-10: Relatórios & Análise (49h)
   ├─ Relatório Listas [18h]
   ├─ Relatório Semana [15h]
   └─ Análise Cidades [16h]

MÊS 4 (4 semanas)
├─ Sprint 11: PWA [13h]
├─ Sprint 12: Página Usuário [14h]
└─ Sprint 13: Contingência + Refinamentos
```

**Total Estimado**: ~16 semanas (4 meses) com 1 desenvolvedor full-time
**Com 2 devs (1 Backend + 1 Frontend)**: ~8 semanas (2 meses)
**Com 3 devs (1 Backend + 1 Frontend + 1 Infra)**: ~6 semanas (1.5 meses)

---

## 🚀 Como Usar Este Documento

### Para o Product Owner
1. Use para priorizar com stakeholders
2. Revise "ROI" para cada requisição
3. Ajuste timeline conforme recursos disponíveis

### Para o Tech Lead
1. Use "Por Camada" para distribuir entre devs
2. Valide estimativas localmente
3. Planeie code reviews e testes

### Para o Desenvolvedor
1. Vá para [SPRINT_1_QUICKSTART.md](./SPRINT_1_QUICKSTART.md)
2. Pegue a primeira task sem dependências
3. Faça commit com `Closes #<task_id>`

### Para o QA
1. Use checklist de tasks para validação
2. Revise "Teste" tasks para cobertura
3. Prepare casos de teste em paralelo

---

## 🔗 Documentos Relacionados

1. **[REQUISICOES_MELHORIAS.md](./REQUISICOES_MELHORIAS.md)** - Descrição original das requisições
2. **[PLANO_TASKS.md](./PLANO_TASKS.md)** - Detalhamento completo com dependências
3. **[SPRINT_1_QUICKSTART.md](./SPRINT_1_QUICKSTART.md)** - Guia rápido para começar Sprint 1

---

## 📊 Dashboard Resumido

```
┌─────────────────────────────────────────┐
│   TAREFAS POR PRIORIDADE                │
├─────────────────────────────────────────┤
│ 🔴 ALTA:   21 tasks  56h  (26% do total)│
│ 🟡 NORMAL: 69 tasks 156h  (74% do total)│
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│   TAREFAS POR CAMADA                    │
├─────────────────────────────────────────┤
│ 🔵 Frontend:      25 tasks  65h  (31%) │
│ 🟢 Backend (App):  11 tasks  32h  (15%) │
│ 🟣 Backend (API):  11 tasks  31h  (15%) │
│ 🟤 Backend (Test): 10 tasks  21h  (10%) │
│ 🔵 Backend (Dom):  10 tasks  16h  (8%)  │
│ ⚫ Backend (DB):    5 tasks  11h  (5%)  │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│   PRÓXIMOS PASSOS                       │
├─────────────────────────────────────────┤
│ 1. ✅ Leia [SPRINT_1_QUICKSTART.md]     │
│ 2. ⏳ Comece com emerg-001 (2h)         │
│ 3. 🔄 Faça daily standup                │
│ 4. 📝 Atualize status no banco de dados │
└─────────────────────────────────────────┘
```

---

**Last Updated**: 2026-11-26  
**Version**: 1.0  
**Status**: Ready for Sprint Planning
