# Sprint 1: Quick Start Guide

**Duração Recomendada**: 2 semanas  
**Total de Horas**: ~56 horas  
**Foco**: Implementar fundações de ALTA prioridade (Emergência, Alta, Cancelamento)

---

## 📋 Tasks de Alto Impacto para Começar AGORA

Estas 14 tasks não possuem dependências e podem começar imediatamente:

### 🔴 ALTA PRIORIDADE (Comece aqui!)

#### Semana 1 - Fundação de Dados (Backend)

**Bloco 1: Modelos de Domínio** (~6 horas)

```
1. emerg-001: Model Adicionar tipo de saída (Emergência)
   ├─ Criar enum TipoSaida { NORMAL, EMERGENCIA, ALTA }
   ├─ Adicionar field: TipoSaida tipoSaida em Agendamento
   ├─ Criar builder/factory
   └─ Tempo: 2h

2. canc-001: Model Adicionar status de cancelamento
   ├─ Criar enum StatusViagem { ATIVO, CANCELADO }
   ├─ Adicionar fields: StatusViagem status, String motivoCancelamento
   ├─ Adicionar Instant dataArquivado (soft-delete)
   └─ Tempo: 2h

3. conf-001: Model Campo de confirmação de saída
   ├─ Adicionar field: Boolean confirmadoSaida
   ├─ Adicionar field: Instant dataConfirmacaoSaida
   ├─ Validações iniciais
   └─ Tempo: 1h

   SUBTOTAL SEMANA 1A: 5 horas | Responsável: Backend Arquiteto
```

**Bloco 2: Migrações de Banco de Dados** (~4 horas)

```
4. emerg-002: JPA Mapear tipo de saída no Agendamento
   ├─ Criar migration V18__adicionar_tipo_saida_agendamento.sql
   ├─ ALTER TABLE agendamento ADD COLUMN tipo_saida VARCHAR(20)
   ├─ Adicionar JPA annotations: @Enumerated
   └─ Tempo: 2h

5. canc-002: JPA Adicionar coluna status_viagem e motivo_cancelamento
   ├─ Criar migration V19__adicionar_status_cancelamento_agendamento.sql
   ├─ ALTER TABLE agendamento ADD COLUMN status_viagem VARCHAR(20)
   ├─ ALTER TABLE agendamento ADD COLUMN motivo_cancelamento TEXT
   ├─ ALTER TABLE agendamento ADD COLUMN data_arquivado DATETIME
   └─ Tempo: 2h

   SUBTOTAL SEMANA 1B: 4 horas | Responsável: DBA/Backend
```

#### Semana 2 - Lógica de Negócio (Backend)

**Bloco 3: Handlers e Regras de Negócio** (~5 horas)

```
6. emerg-004: Handler Criar AbrirVeiculoEmergenciaHandler
   ├─ Localização: src/main/java/br/com/imbazzu/secretariaTransporte/agendamento/application/handler
   ├─ Implementar: AbrirVeiculoEmergenciaCommand, AbrirVeiculoEmergenciaHandler
   ├─ Regras: 
   │  - Validar lista de emergência
   │  - Permitir qualquer tipo de veículo
   │  - Registrar tipoSaida = EMERGENCIA
   └─ Tempo: 3h

7. canc-004: Handler CancelarViagemHandler
   ├─ Localização: src/main/java/br/com/imbazzu/secretariaTransporte/agendamento/application/handler
   ├─ Implementar: CancelarViagemCommand, CancelarViagemHandler
   ├─ Regras:
   │  - Validar agendamento ativo
   │  - Soft-delete: status = CANCELADO, dataArquivado = now()
   │  - Capturar motivoCancelamento
   └─ Tempo: 2h

   SUBTOTAL SEMANA 2A: 5 horas | Responsável: Backend - Application Layer
```

**Bloco 4: Queries e Adaptadores** (~3 horas)

```
8. canc-005: Query Listar viagens (excluindo canceladas)
   ├─ Localização: src/main/java/br/com/imbazzu/secretariaTransporte/agendamento/adapter/out
   ├─ Atualizar AgendamentoRepository:
   │  - Adicionar @Query com filtro: WHERE status_viagem = 'ATIVO'
   │  - Atualizar findAll(), findByLista(), etc
   │  - Manter método findAllIncluindoCanceladas() para auditoria
   └─ Tempo: 3h

   SUBTOTAL SEMANA 2B: 3 horas | Responsável: Backend - Adapter Layer
```

---

### 🟡 NORMAL PRIORIDADE (Paralelo/Depois)

**Bloco 5: Configurações Iniciais** (~4 horas, sem dependência)

```
9. pwa-001: Frontend Criar manifest.json
   ├─ Localização: src/public/manifest.json (ou root)
   ├─ Incluir: name, short_name, start_url, display, theme_color, icons
   └─ Tempo: 2h

10. tablet-001: Frontend Revisar layout responsivo
    ├─ Auditar breakpoints atuais
    ├─ Documento: responsive-audit.md
    └─ Tempo: 2h

11. noturno-001: Model Regra de agrupamento por horário
    ├─ Localização: Backend - Service
    ├─ Criar: class HorarioNoturnoRule
    ├─ Lógica: isHorarioNoturno(LocalTime saida) -> saida.getHour() >= 20
    └─ Tempo: 2h

12. perm-001: Model Adicionar mapping User-Lista
    ├─ Localização: Backend - Domain
    ├─ Criar: UsuarioLista entity
    ├─ Fields: usuarioId, listaId
    └─ Tempo: 2h

    SUBTOTAL BLOCO 5: 8 horas | Paralelo ao Bloco 1-2
```

---

## 📌 Dependências Críticas

```
Sprint 1 Chain:
┌─ emerg-001 (2h)
│  └─ emerg-002 (2h) ◄─ AGUARDA
│     └─ emerg-003 (4h) [Sprint 2]
│        └─ emerg-005, emerg-006 [Sprint 3]
│
├─ canc-001 (2h)
│  └─ canc-002 (2h) ◄─ AGUARDA
│     └─ canc-005 (3h)
│        └─ canc-003 (3h) [Sprint 2]
│           └─ canc-006, canc-007 [Sprint 3]
│
└─ conf-001 (1h)
   └─ conf-002 (1h)
      └─ conf-003 (2h) [Sprint 2]
```

---

## 🎯 Checklist Sprint 1

### Semana 1 - Backend Foundation

- [ ] **Dia 1-2**: Models criados (emerg-001, canc-001, conf-001)
  - [ ] Enums definidos
  - [ ] Builders testados
  - [ ] Javadoc comentado

- [ ] **Dia 2-3**: Migrações criadas (emerg-002, canc-002, conf-002)
  - [ ] SQL testado em ambiente local
  - [ ] Rollback scripts prontos
  - [ ] Versão de migração documentada

- [ ] **Dia 3**: Paralelo - Validações iniciais
  - [ ] pwa-001 (manifest.json criado)
  - [ ] tablet-001 (audit completado)
  - [ ] noturno-001 (regra implementada)
  - [ ] perm-001 (model criado)

### Semana 2 - Handlers & Queries

- [ ] **Dia 4-5**: Handlers criados (emerg-004, canc-004)
  - [ ] Lógica de negócio testada
  - [ ] Exceções tratadas
  - [ ] Logs adicionados

- [ ] **Dia 5-6**: Queries atualizadas (canc-005)
  - [ ] Testes de filtro passando
  - [ ] Performance validada
  - [ ] Outros métodos atualizados

- [ ] **Dia 6-7**: Code Review & Documentação
  - [ ] Pull Requests revisados
  - [ ] Testes unitários passando
  - [ ] Documentação atualizada

---

## 🔧 Estrutura de Arquivos - Backend

```
backend/
├── src/main/java/br/com/imbazzu/secretariaTransporte/
│   ├── agendamento/
│   │   ├── core/domain/
│   │   │   ├── Agendamento.java (ADD: tipoSaida, status, confirmadoSaida)
│   │   │   ├── TipoSaida.java (NEW - ENUM)
│   │   │   └── StatusViagem.java (NEW - ENUM)
│   │   │
│   │   ├── application/
│   │   │   ├── handler/
│   │   │   │   ├── AbrirVeiculoEmergenciaHandler.java (NEW)
│   │   │   │   └── CancelarViagemHandler.java (NEW)
│   │   │   │
│   │   │   └── command/
│   │   │       ├── AbrirVeiculoEmergenciaCommand.java (NEW)
│   │   │       └── CancelarViagemCommand.java (NEW)
│   │   │
│   │   ├── adapter/
│   │   │   ├── in/
│   │   │   │   └── rest/
│   │   │   │       └── AgendamentoController.java (ADD endpoints Sprint 2)
│   │   │   │
│   │   │   └── out/
│   │   │       └── AgendamentoRepository.java (UPDATE queries)
│   │   │
│   │   └── infrastructure/
│   │       └── jpa/
│   │           └── AgendamentoJpa.java (ADD fields)
│   │
│   └── lista/core/domain/
│       ├── HorarioNoturnoRule.java (NEW)
│       └── PermissaoLista.java (NEW - para Perm-001)
│
└── src/main/resources/
    └── db/migration/
        ├── V18__adicionar_tipo_saida_agendamento.sql (NEW)
        ├── V19__adicionar_status_cancelamento_agendamento.sql (NEW)
        └── V20__criar_tabela_usuario_listas.sql (Perm-002, Sprint futuro)
```

---

## 🧪 Testes Unitários - Mínimo Necessário

```java
// src/test/java/br/com/imbazzu/secretariaTransporte/agendamento/core/domain/

@Test
void deveAbrirVeiculoComTipoSaidaEmergencia() {
    Agendamento agendamento = Agendamento.criar(...)
        .comTipoSaida(TipoSaida.EMERGENCIA);
    
    assertThat(agendamento.getTipoSaida()).isEqualTo(TipoSaida.EMERGENCIA);
}

@Test
void deveCancelarViagemComMotivo() {
    Agendamento agendamento = Agendamento.recriar(...);
    agendamento.cancelar("Passageiro solicitou");
    
    assertThat(agendamento.getStatus()).isEqualTo(StatusViagem.CANCELADO);
    assertThat(agendamento.getMotivoCancelamento()).isEqualTo("Passageiro solicitou");
}

@Test
void deveIndicarHorarioNoturno() {
    LocalTime noturno = LocalTime.of(20, 0);
    LocalTime diurno = LocalTime.of(10, 0);
    
    assertTrue(HorarioNoturnoRule.isNoturno(noturno));
    assertFalse(HorarioNoturnoRule.isNoturno(diurno));
}
```

---

## 📊 Métricas Sprint 1

| Métrica | Target | Status |
|---|---|---|
| Tasks Completadas | 14/14 | ⏳ |
| Horas Planejadas | 56h | ⏳ |
| Code Coverage | >80% | ⏳ |
| Pull Requests | 5-7 | ⏳ |
| Bugs Críticos | 0 | ⏳ |
| Documentação | 100% | ⏳ |

---

## 🚀 Como Começar Hoje Mesmo

1. **Crie um branch**: `git checkout -b feature/alta-prioridade-sprint1`
2. **Task emerg-001**: Comece com o enum `TipoSaida`
3. **Teste local**: `mvn clean test`
4. **Commit pequeno**: "feat: adicionar enum TipoSaida"
5. **Próxima task**: `emerg-002` - migração DB

**Tempo até poder fazer o primeiro commit**: ~30 min

---

## 📞 Contato & Dúvidas

- **Dúvidas de Arquitetura?** → Backend Arquiteto
- **Dúvidas de Banco de Dados?** → DBA/Backend
- **Dúvidas de UI/Frontend?** → Frontend Lead
- **Status Sprint?** → Daily Standup

