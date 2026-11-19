# Diagrama de Entidades e Relacionamentos

## 1. Modelo de Dados (Antes e Depois)

### ANTES (Estrutura Anterior)

```
┌─────────────────────────┐
│      PESSOAS            │
├─────────────────────────┤
│ id (PK): UUID           │
│ nome: VARCHAR(150)      │
│ telefone: VARCHAR(11)   │
│ prioridade: ENUM        │◄─── PROBLEMA: Prioridade é uma enum
│ observacao: VARCHAR(249)│     fixa, não permite customização
│ cpf: VARCHAR(11)        │
│ endereco: VARCHAR(250)  │
│ data_arquivado: DATE    │
└─────────────────────────┘

┌──────────────────────────────┐
│      AGENDAMENTOS            │
├──────────────────────────────┤
│ id (PK): UUID                │
│ pessoa_id (FK)   ────────┐   │
│ buscar_na_residencia: BOOL   │◄─── Dados que deveriam estar em Pessoa
│ acompanhantes: INT           │
│ ...outros campos             │
└──────────────────────────────┘
```

### DEPOIS (Estrutura Nova) ✓

```
┌──────────────────────────────┐
│    ESPECIFICACOES            │
├──────────────────────────────┤
│ id (PK): UUID                │
│ nome: VARCHAR(100) UNIQUE    │
│ prioridade: ENUM             │◄─── Prioridade agora é parte da
│ observacao: VARCHAR(249)     │     Especificação (customizável)
│ data_arquivado: DATE         │
└──────────────────────────────┘
            ▲
            │ 1
            │
            │ N
            │
┌──────────────────────────────────┐
│      PESSOAS                     │
├──────────────────────────────────┤
│ id (PK): UUID                    │
│ nome: VARCHAR(150)               │
│ telefone: VARCHAR(11)            │
│ especificacao_id (FK) ───┐       │◄─── Referencia Especificação
│ buscar_na_residencia: BOOL       │     (removido prioridade enum)
│ quantidade_acompanhantes: INT    │◄─── Movido de Agendamento
│ observacao: VARCHAR(249)         │
│ cpf: VARCHAR(11)                 │
│ endereco: VARCHAR(250)           │
│ data_arquivado: DATE             │
└──────────────────────────────────┘
            ▲
            │ 1
            │
            │ N
            │
┌────────────────────────────────────┐
│      AGENDAMENTOS                  │
├────────────────────────────────────┤
│ id (PK): UUID                      │
│ pessoa_id (FK) ────────┐           │
│ buscar_na_residencia: BOOL (audit) │◄─── Mantém para auditoria
│ acompanhantes: INT (audit)         │     (origem em Pessoa)
│ ...outros campos                   │
└────────────────────────────────────┘
```

## 2. Fluxo de Dados (Use Case)

### Criar/Editar Pessoa

```
1. ADMIN/USUARIO
   │
   ├─► POST /api/especificacao
   │   └─ Cria: "Oncologico" (ALTA), "TEA 1" (MODERADA), etc.
   │
   └─► POST /api/pessoa
       ├─ nome: "João Silva"
       ├─ especificacaoId: "uuid-oncologico" ◄─ Seleciona especificação
       ├─ buscarNaResidencia: true            ◄─ Novo campo
       ├─ quantidadeAcompanhantes: 2          ◄─ Novo campo
       └─ ...outros dados
       
2. DATABASE
   ├─ Insere em PESSOAS com FK para ESPECIFICACOES
   └─ Dados relacionados salvos corretamente

3. RESPONSE
   └─ GET /api/pessoa/{id} retorna:
      {
        "id": "uuid",
        "nome": "João Silva",
        "especificacaoId": "uuid-oncologico",
        "especificacaoNome": "Oncologico",    ◄─ Nome da especificação
        "buscarNaResidencia": true,
        "quantidadeAcompanhantes": 2,
        ...
      }
```

### Consultar Dados em Agendamento

```
ANTES (Problema):
  Agendamento.buscarNaResidencia ◄─ Duplicado em agendamento
  
  │
  └─ Inconsistência: dados podem estar diferentes
     entre Pessoa e Agendamento

DEPOIS (Solução):
  Pessoa.buscarNaResidencia ◄─ Fonte única de verdade
  │
  ├─ Puxar dados de Pessoa quando criar agendamento
  │  Agendamento.buscarNaResidencia = Pessoa.buscarNaResidencia
  │
  └─ Manter também em Agendamento para auditoria/histórico
     (mostra como estava quando agendamento foi criado)
```

## 3. Hierarquia de Classes Java

### Domain Layer

```
Especificacao (Domain)
├─ id: UUID
├─ nome: String
├─ prioridade: EnumPrioridade
├─ observacao: String
├─ dataArquivado: LocalDate
├─ criar(nome, prioridade, observacao): Especificacao
├─ editar(nome, prioridade, observacao): void
├─ arquivar(): void
└─ desarquivar(): void

Pessoa (Domain)
├─ id: UUID
├─ nome: Nome (VO)
├─ observacao: Observacao (VO)
├─ especificacao: Especificacao ◄─ NOVO (antes era EnumPrioridade)
├─ buscarNaResidencia: boolean ◄─ NOVO
├─ quantidadeAcompanhantes: int ◄─ NOVO
├─ telefone: Telefone (VO)
├─ endereco: Endereco (VO)
├─ cpf: Cpf (VO)
├─ dataArquivado: LocalDate
├─ criar(...): Pessoa
├─ editar(...): void
├─ arquivar(): void
└─ desarquivar(): void
```

### JPA Layer

```
EspecificacaoJpa (JPA Entity)
├─ id: UUID
├─ nome: String
├─ prioridade: EnumPrioridade
├─ observacao: String
├─ dataArquivado: LocalDate
└─ @Table(name = "especificacoes")

PessoaJpa (JPA Entity)
├─ id: UUID
├─ nome: String
├─ telefone: String
├─ especificacao: EspecificacaoJpa ◄─ @ManyToOne (FK)
├─ buscarNaResidencia: boolean
├─ quantidadeAcompanhantes: int
├─ observacao: String
├─ cpf: String
├─ endereco: String
├─ dataArquivado: LocalDate
└─ @Table(name = "pessoas")
   └─ @JoinColumn(name = "especificacao_id")
```

## 4. Fluxo de Mapeamento (Mapper)

```
EspecificacaoJpa (Banco)
  │
  │ EspecificacaoBancoMapper.paraDomain(jpa)
  │
  ▼
Especificacao (Domain)
  │
  ├─ Usada em Pessoa.Domain para validações
  │
  └─ Mapeada para response DTO
     │
     └─ EspecificacaoResponseDto (JSON)

─────────────────────────────────

PessoaJpa (Banco)
  │
  │ PessoaBancoMapper.paraDomain(jpa)
  │
  ▼
Pessoa (Domain)
  │
  │ Contém referência a Especificacao.Domain
  │
  └─ Mapeada para response DTO
     │
     └─ PessoaResponseDto (JSON)
        ├─ especificacaoId: UUID
        ├─ especificacaoNome: String
        ├─ buscarNaResidencia: boolean
        ├─ quantidadeAcompanhantes: int
        └─ ...outros campos
```

## 5. Casos de Uso - Sequência

### UC1: Cadastrar Especificação e Pessoa

```
Actor: Admin / Usuário do Sistema

1. Admin acessa /api/especificacao
   │
   ├─► POST com body:
   │   {
   │     "nome": "Oncologico",
   │     "prioridade": "ALTA",
   │     "observacao": "Pacientes oncológicos"
   │   }
   │
   └─► Response: 201 Created
       Location: /api/especificacao/{uuid}

2. Admin acessa /api/pessoa
   │
   ├─► POST com body:
   │   {
   │     "nome": "João Silva",
   │     "especificacaoId": "{uuid-oncologico}",
   │     "buscarNaResidencia": true,
   │     "quantidadeAcompanhantes": 1,
   │     "telefone": "11987654321"
   │   }
   │
   └─► Response: 201 Created
       Location: /api/pessoa/{uuid}

3. Sistema:
   ├─ Valida Especificacao existe
   ├─ Cria Pessoa com FK para Especificacao
   ├─ Salva informações de residência e acompanhantes
   └─ Retorna 201
```

### UC2: Buscar Pessoa para Agendamento

```
Actor: Sistema de Agendamento / API interna

1. GET /api/pessoa/{id}
   │
   ├─ Query: SELECT p.*, e.* FROM pessoas p
   │         JOIN especificacoes e ON p.especificacao_id = e.id
   │         WHERE p.id = ?
   │
   └─ Response: 200 OK
      {
        "id": "uuid-pessoa",
        "nome": "João Silva",
        "especificacaoId": "uuid-oncologico",
        "especificacaoNome": "Oncologico",
        "buscarNaResidencia": true,          ◄─ Use para agendar busca
        "quantidadeAcompanhantes": 1,        ◄─ Use para planejar veículo
        "telefone": "11987654321"
      }

2. Sistema de Agendamento:
   ├─ Pega pessoa.especificacao.prioridade (ALTA)
   ├─ Pega pessoa.buscarNaResidencia (true)
   ├─ Pega pessoa.quantidadeAcompanhantes (1)
   ├─ Usa essas informações para:
   │  ├─ Priorizar agendamento
   │  ├─ Planejar rota de busca
   │  └─ Escolher tipo de veículo
   │
   └─ Cria Agendamento com esses dados
      (manter também em agendamento para auditoria)
```

## 6. Validação e Constraints

```
ESPECIFICACAO
├─ nome: NOT NULL, UNIQUE, 3-100 chars
├─ prioridade: NOT NULL, CHECK (IN 'BAIXA','MODERADA','ALTA','URGENTE')
├─ data_arquivado: NULL OU DATE válida
└─ Soft Delete: consultas normais excluem dataArquivado IS NOT NULL

PESSOA
├─ nome: NOT NULL, 3-150 chars
├─ telefone: NOT NULL, 11 chars
├─ especificacao_id: NOT NULL, FK → especificacoes.id
├─ buscar_na_residencia: NOT NULL, boolean
├─ quantidade_acompanhantes: NOT NULL, int >= 0
├─ cpf: UNIQUE (nullable)
├─ data_arquivado: NULL OU DATE válida
└─ Soft Delete: consultas normais excluem dataArquivado IS NOT NULL
```

---

**Versão**: 1.0
**Data**: 2024
**Mantido por**: Arquitetura
