# Plano de Tasks - Sistema de Agendamento de Transportes

**Última atualização**: 2026-11-26  
**Total de Tasks**: 90  
**Total de Horas Estimadas**: ~212 horas

---

## 📊 Resumo por Prioridade

### 🔴 ALTA PRIORIDADE (21 tasks - ~56 horas)

| Requisição | Tasks | Horas | Status |
|---|---|---|---|
| Motivo 02 - Emergência | 7 | 20 | Pendente |
| Motivo 03 - Alta Hospitalar | 6 | 17 | Pendente |
| Cancelamento | 8 | 19 | Pendente |
| **TOTAL** | **21** | **56** | - |

### 🟡 NORMAL PRIORIDADE (69 tasks - ~156 horas)

| Requisição | Tasks | Horas | Status |
|---|---|---|---|
| Permissões Listas | 9 | 21 | Pendente |
| Relatório Listas | 7 | 18 | Pendente |
| Análise Cidades/Destinos | 6 | 16 | Pendente |
| Relatório Semana | 6 | 15 | Pendente |
| Página de Usuário | 6 | 14 | Pendente |
| PWA | 6 | 13 | Pendente |
| Confirmação de Saída | 6 | 10 | Pendente |
| Horário Noturno | 5 | 10 | Pendente |
| Motivo 01 - Ajuste Separação | 4 | 10 | Pendente |
| Tablet Responsivo | 4 | 9 | Pendente |
| Visualização Residência | 5 | 6.5 | Pendente |
| Ordenação Visual | 4 | 6 | Pendente |
| **TOTAL** | **69** | **156** | - |

---

## 🔴 ALTA PRIORIDADE - DETALHAMENTO

### 1️⃣ MOTIVO 02 - EMERGÊNCIA (7 tasks)

**Descrição**: Facilitar abertura de veículos para chamadas de emergência com horário de saída.

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| emerg-001 | Model: Adicionar tipo de saída (Emergência) | Backend - Domain | 2 | - |
| emerg-002 | JPA: Mapear tipo de saída no Agendamento | Backend - Database | 2 | emerg-001 |
| emerg-003 | API: Endpoint para abrir veículo de emergência | Backend - API | 4 | emerg-002 |
| emerg-004 | Handler: Criar AbrirVeiculoEmergenciaHandler | Backend - Application | 3 | emerg-001 |
| emerg-005 | UI: Botão Emergência na tela de agendamento | Frontend | 3 | emerg-003 |
| emerg-006 | UI: Modal de agendamento rápido (Emergência) | Frontend | 3 | emerg-003 |
| emerg-007 | Teste: Validar agendamento de emergência | Backend - Test | 3 | emerg-004 |

**Total**: 20 horas

**Ordem de Execução Recomendada**:
1. emerg-001 (2h) - Criar enum de tipo de saída
2. emerg-002 (2h) - Migração DB
3. emerg-004 (3h) - Handler de negócio
4. emerg-003 (4h) - API endpoint
5. emerg-005 + emerg-006 (6h) - UI (paralelo)
6. emerg-007 (3h) - Testes

---

### 2️⃣ MOTIVO 03 - ALTA HOSPITALAR (6 tasks)

**Descrição**: Facilitar abertura de veículos para saídas de pacientes do hospital.

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| alta-001 | Model: Alta Hospitalar no domain | Backend - Domain | 1 | emerg-001 |
| alta-002 | API: Endpoint para abrir veículo de alta | Backend - API | 4 | emerg-002 |
| alta-003 | Handler: Criar AbrirVeiculoAltaHandler | Backend - Application | 3 | alta-001 |
| alta-004 | UI: Botão Alta na tela de agendamento | Frontend | 3 | alta-002 |
| alta-005 | UI: Modal de agendamento rápido (Alta) | Frontend | 3 | alta-002 |
| alta-006 | Teste: Validar agendamento de alta | Backend - Test | 3 | alta-003 |

**Total**: 17 horas

**Prerequisitos**: Emergência deve estar em progresso (reutiliza tipo_saida)

**Ordem de Execução Recomendada**:
1. alta-001 (1h) - Usar enum existente
2. alta-003 (3h) - Handler
3. alta-002 (4h) - API endpoint
4. alta-004 + alta-005 (6h) - UI (paralelo)
5. alta-006 (3h) - Testes

---

### 3️⃣ CANCELAMENTO (8 tasks)

**Descrição**: Implementar status de cancelamento para viagens (soft-delete).

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| canc-001 | Model: Adicionar status de cancelamento | Backend - Domain | 2 | - |
| canc-002 | JPA: Adicionar coluna status_viagem e motivo_cancelamento | Backend - Database | 2 | canc-001 |
| canc-003 | API: Endpoint cancelar viagem | Backend - API | 3 | canc-002 |
| canc-004 | Handler: CancelarViagemHandler | Backend - Application | 2 | canc-001 |
| canc-005 | Query: Listar viagens (excluindo canceladas) | Backend - Adapter | 3 | canc-002 |
| canc-006 | UI: Botão cancelar na linha do agendamento | Frontend | 2 | canc-003 |
| canc-007 | UI: Modal de cancelamento com motivo | Frontend | 2 | canc-003 |
| canc-008 | Teste: Validar cancelamento soft-delete | Backend - Test | 3 | canc-004 |

**Total**: 19 horas

**Ordem de Execução Recomendada**:
1. canc-001 (2h) - Model com enum Status
2. canc-002 (2h) - Migração DB
3. canc-004 (2h) - Handler
4. canc-005 (3h) - Atualizar queries
5. canc-003 (3h) - API endpoint
6. canc-006 + canc-007 (4h) - UI (paralelo)
7. canc-008 (3h) - Testes

---

## 🟡 NORMAL PRIORIDADE - DETALHAMENTO

### 4️⃣ MOTIVO 01 - AJUSTE DE SEPARAÇÃO (4 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| ajust-001 | UI: Modal de ajuste de separação | Frontend | 3 | - |
| ajust-002 | API: Endpoint para ajustar passageiros de veículo | Backend - API | 3 | ajust-001 |
| ajust-003 | Handler: AjustarPassageirosHandler | Backend - Application | 2 | ajust-001 |
| ajust-004 | Teste: Validar ajuste de passageiros | Backend - Test | 2 | ajust-003 |

**Total**: 10 horas

---

### 5️⃣ CONFIRMAÇÃO DE SAÍDA (6 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| conf-001 | Model: Campo de confirmação de saída | Backend - Domain | 1 | - |
| conf-002 | JPA: Adicionar coluna confirmado_saida na BD | Backend - Database | 1 | conf-001 |
| conf-003 | API: Endpoint confirmar saída do veículo | Backend - API | 2 | conf-002 |
| conf-004 | Handler: ConfirmarSaidaHandler | Backend - Application | 2 | conf-001 |
| conf-005 | UI: Botão Ok para confirmar saída | Frontend | 2 | conf-003 |
| conf-006 | Teste: Validar confirmação de saída | Backend - Test | 2 | conf-004 |

**Total**: 10 horas

---

### 6️⃣ HORÁRIO NOTURNO (5 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| noturno-001 | Model: Regra de agrupamento por horário | Backend - Domain | 2 | - |
| noturno-002 | API: Query agendamentos com grupo noturno | Backend - API | 2 | noturno-001 |
| noturno-003 | UI: Dividir viagens em duas seções | Frontend | 3 | noturno-002 |
| noturno-004 | UI: Marcação visual para veículos noturnos | Frontend | 2 | noturno-003 |
| noturno-005 | Teste: Validar agrupamento noturno | Backend - Test | 1 | noturno-001 |

**Total**: 10 horas

---

### 7️⃣ ORDENAÇÃO E MARCAÇÃO VISUAL (4 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| ordem-001 | API: Retornar agendamentos ordenados por horário | Backend - API | 1 | - |
| ordem-002 | UI: Ordenar veículos por horário na tela | Frontend | 2 | ordem-001 |
| ordem-003 | UI: Adicionar badges de horário | Frontend | 2 | ordem-002 |
| ordem-004 | Teste: Validar ordenação correta | Backend - Test | 1 | ordem-001 |

**Total**: 6 horas

---

### 8️⃣ VISUALIZAÇÃO RESIDÊNCIA (5 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| resid-001 | Model: Usar campo buscarNaResidencia existente | Backend - Domain | 0.5 | - |
| resid-002 | API: Retornar flag buscarNaResidencia no agendamento | Backend - API | 1 | resid-001 |
| resid-003 | UI: Ícone/marcação visual para buscar na residência | Frontend | 2 | resid-002 |
| resid-004 | UI: Filtro por buscar na residência | Frontend | 2 | resid-002 |
| resid-005 | Teste: Validar filtro e marcação | Backend - Test | 1 | resid-003 |

**Total**: 6.5 horas

---

### 9️⃣ PERMISSÕES DE LISTAS (9 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| perm-001 | Model: Adicionar mapping User-Lista | Backend - Domain | 2 | - |
| perm-002 | JPA: Tabela usuario_listas na BD | Backend - Database | 2 | perm-001 |
| perm-003 | API: Endpoint gerenciar listas do usuário | Backend - API | 3 | perm-002 |
| perm-004 | Handler: GerenciarListasUsuarioHandler | Backend - Application | 2 | perm-001 |
| perm-005 | Query: Listar listas por usuário | Backend - Adapter | 2 | perm-002 |
| perm-006 | API: Filtrar listas no GET agendamentos | Backend - API | 2 | perm-005 |
| perm-007 | UI: Tela de gerenciamento de listas por usuário | Frontend | 4 | perm-004 |
| perm-008 | UI: Filtrar listas no agendamento por permissão | Frontend | 2 | perm-006 |
| perm-009 | Teste: Validar permissões de listas | Backend - Test | 2 | perm-005 |

**Total**: 21 horas

---

### 🔟 RESPONSIVIDADE EM TABLET (4 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| tablet-001 | Frontend: Revisar layout responsivo | Frontend | 2 | - |
| tablet-002 | Frontend: Grid layout para carros lado a lado | Frontend | 3 | tablet-001 |
| tablet-003 | Frontend: Testar em tablet portrait/landscape | Frontend | 2 | tablet-002 |
| tablet-004 | Frontend: Ajustar tamanho de botões para toque | Frontend | 2 | tablet-001 |

**Total**: 9 horas

---

### 1️⃣1️⃣ PÁGINA DE USUÁRIO (6 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| user-001 | API: Endpoint GET /api/usuarios/{id} com dados completos | Backend - API | 2 | - |
| user-002 | API: Endpoint PUT /api/usuarios/{id}/atualizar-perfil | Backend - API | 3 | user-001 |
| user-003 | Handler: AtualizarPerfilUsuarioHandler | Backend - Application | 2 | user-001 |
| user-004 | UI: Tela de perfil do usuário | Frontend | 3 | user-001 |
| user-005 | UI: Botão Editar no perfil | Frontend | 2 | user-004 |
| user-006 | Teste: Validar atualização de perfil | Backend - Test | 2 | user-003 |

**Total**: 14 horas

---

### 1️⃣2️⃣ RELATÓRIO DE LISTAS (7 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| rel-lista-001 | Model: Agregações de dados de lista | Backend - Application | 3 | - |
| rel-lista-002 | API: Endpoint GET /api/relatorios/listas/{id} | Backend - API | 3 | rel-lista-001 |
| rel-lista-003 | API: Endpoint GET /api/relatorios/listas (múltiplas) | Backend - API | 3 | rel-lista-001 |
| rel-lista-004 | UI: Tela de seleção de listas para relatório | Frontend | 2 | rel-lista-002 |
| rel-lista-005 | UI: Exibir metrics de lista (viagens, passageiros) | Frontend | 3 | rel-lista-004 |
| rel-lista-006 | UI: Filtros de período no relatório | Frontend | 2 | rel-lista-004 |
| rel-lista-007 | Teste: Validar cálculo de métricas | Backend - Test | 2 | rel-lista-001 |

**Total**: 18 horas

---

### 1️⃣3️⃣ RELATÓRIO DE SEMANA (6 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| rel-semana-001 | Model: Agregações por dia da semana | Backend - Application | 3 | - |
| rel-semana-002 | API: Endpoint GET /api/relatorios/semana | Backend - API | 3 | rel-semana-001 |
| rel-semana-003 | UI: Gráfico de viagens por dia da semana | Frontend | 3 | rel-semana-002 |
| rel-semana-004 | UI: Tabela de horários por dia | Frontend | 2 | rel-semana-002 |
| rel-semana-005 | UI: Filtros para período e lista | Frontend | 2 | rel-semana-002 |
| rel-semana-006 | Teste: Validar agregação por dia | Backend - Test | 2 | rel-semana-001 |

**Total**: 15 horas

---

### 1️⃣4️⃣ ANÁLISE DE CIDADES/DESTINOS (6 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| cidade-001 | Model: Service de análise de destinos | Backend - Application | 3 | - |
| cidade-002 | API: Endpoint GET /api/relatorios/destinos | Backend - API | 2 | cidade-001 |
| cidade-003 | UI: Seção de análise de destinos na agendamento | Frontend | 3 | cidade-002 |
| cidade-004 | UI: Gráfico de destinos mais frequentes | Frontend | 2 | cidade-003 |
| cidade-005 | UI: Integração com mapa (opcional) | Frontend | 4 | cidade-003 |
| cidade-006 | Teste: Validar análise de destinos | Backend - Test | 2 | cidade-001 |

**Total**: 16 horas

---

### 1️⃣5️⃣ PROGRESSIVE WEB APP (PWA) (6 tasks)

| ID | Task | Layer | Horas | Dependência |
|---|---|---|---|---|
| pwa-001 | Frontend: Criar manifest.json | Frontend | 2 | - |
| pwa-002 | Frontend: Implementar Service Worker | Frontend | 4 | pwa-001 |
| pwa-003 | Frontend: Gerar ícones para múltiplas resoluções | Frontend | 2 | pwa-001 |
| pwa-004 | Frontend: Configurar tema de cores | Frontend | 1 | pwa-001 |
| pwa-005 | Frontend: Testar instalação em Android | Frontend | 2 | pwa-002 |
| pwa-006 | Frontend: Testar instalação em iOS | Frontend | 2 | pwa-002 |
| pwa-007 | Teste: Validar PWA offline | Frontend | 1 | pwa-002 |

**Total**: 14 horas

---

## 📅 Sugestões de Sprints

### Sprint 1: Fundação de Alta Prioridade (Semanas 1-2)
**Objetivo**: Implementar base para Emergência, Alta e Cancelamento

**Tasks**:
- emerg-001, emerg-002, emerg-004 (7h)
- canc-001, canc-002, canc-004 (6h)
- canc-005 (3h)

**Total**: ~16 horas

### Sprint 2: APIs de Alta Prioridade (Semanas 2-3)
**Objetivo**: Endpoints prontos para teste

**Tasks**:
- emerg-003 (4h)
- emerg-007 (3h)
- canc-003 (3h)
- canc-008 (3h)
- alta-001, alta-003 (4h)

**Total**: ~17 horas

### Sprint 3: UI de Alta Prioridade (Semanas 3-4)
**Objetivo**: Interfaces prontas para usar

**Tasks**:
- emerg-005, emerg-006 (6h)
- canc-006, canc-007 (4h)
- alta-004, alta-005 (6h)
- alta-002 (4h)
- alta-006 (3h)

**Total**: ~23 horas

### Sprint 4: Baixando Para Prioridade Normal
**Comece com**:
- Visualização Residência (6.5h) - Rápido e agrega valor
- Ordenação Visual (6h) - Melhora UX
- Confirmação de Saída (10h) - Importante para workflow

---

## 🎯 KPIs por Semana

| Sprint | Requisições | Tasks | Horas | Complexidade | ROI |
|---|---|---|---|---|---|
| 1 | 3 | 10 | 16 | Alta | Fundação crítica |
| 2 | 3 | 10 | 17 | Alta | Funcionalidades core |
| 3 | 3 | 15 | 23 | Média | UI/UX completa |
| 4+ | 13 | 55+ | 156+ | Variado | Incrementos contínuos |

---

## 📝 Notas Importantes

1. **Dependências**: Emergência e Alta compartilham o mesmo enum `tipo_saida`. Execute Emergência primeiro.
2. **Soft-Delete Pattern**: Cancelamento segue o mesmo padrão arquitetural já usado no projeto.
3. **Parallelização**: UI tasks podem rodar em paralelo com Backend tasks.
4. **Testes**: Incluídos em cada requisição. Execute após a task correspondente.
5. **PWA**: Pode ser implementado em paralelo sem impacto nas outras features.

---

## 🔗 Referências

- Documentação: [REQUISICOES_MELHORIAS.md](../REQUISICOES_MELHORIAS.md)
- Arquitetura: Hexagonal com Domain-Driven Design
- Stack: Java 17+ / Spring Boot / JPA / React/Frontend

