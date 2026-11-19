# Resumo das Alterações - Refatoração de Pessoa e Implementação de Especificação

## Visão Geral

Este documento resume as alterações realizadas para:
1. **Mover dados de agendamento para Pessoa**: `buscarNaResidencia` e `quantidadeAcompanhantes` agora são atributos de Pessoa
2. **Substituir prioridade por Especificação**: Em vez de uma enum, agora usar uma tabela/entidade Especificação
3. **Criar relacionamento Pessoa ↔ Especificação**: Cada pessoa tem uma especificação que, por sua vez, possui um nível de prioridade

## Estrutura de Dados

### Nova Entidade: Especificacao

**Tabela**: `especificacoes`

```sql
CREATE TABLE especificacoes (
    id              BINARY(16)      NOT NULL PRIMARY KEY,
    nome            VARCHAR(100)    NOT NULL UNIQUE,
    prioridade      VARCHAR(20)     NOT NULL,
    observacao      VARCHAR(249)    NULL,
    data_arquivado  DATE            NULL
);
```

**Campos**:
- `id`: UUID único
- `nome`: Identificador da especificação (ex: "Oncologico", "TEA 1", "Dialise", etc.)
- `prioridade`: Nível de prioridade (BAIXA, MODERADA, ALTA, URGENTE)
- `observacao`: Observações opcionais
- `data_arquivado`: Data de arquivamento (soft delete)

**Fluxo**:
1. Administrador cadastra Especificações (ex: "Oncologico ALTA", "TEA 1 MODERADA")
2. Especificação fica disponível para seleção ao cadastrar Pessoa

### Entidade Pessoa - Alterações

**Novos campos**:
- `especificacao_id` (FK → Especificacao): Substitui o campo `prioridade` (enum)
- `buscar_na_residencia` (BOOLEAN): Se a pessoa deve ser buscada em casa
- `quantidade_acompanhantes` (INT): Quantas pessoas acompanham o paciente

**Campo removido**:
- `prioridade` (enum) - Substituído por `especificacao_id`

### Fluxo de Uso

```
1. CADASTRO DE ESPECIFICAÇÕES (uma única vez)
   ├─ Admin cria "Oncologico" → Prioridade ALTA
   ├─ Admin cria "TEA 1" → Prioridade MODERADA
   └─ Admin cria "Dialise" → Prioridade URGENTE

2. CADASTRO DE PESSOA
   ├─ Preenche dados básicos (nome, telefone, endereço)
   ├─ Seleciona Especificação (ex: "Oncologico")
   ├─ Informa se busca na residência (true/false)
   ├─ Informa quantidade de acompanhantes
   └─ Dados salvos em PESSOAS com FK para ESPECIFICACOES

3. AGENDAMENTO
   ├─ Ao criar agendamento, buscar Pessoa por ID
   ├─ Puxar de Pessoa: especificação (que tem prioridade), buscarNaResidencia, quantidadeAcompanhantes
   ├─ Usar essas informações para processamento
   └─ Manter também em Agendamento para auditoria/histórico (opcional)
```

## Arquivos Criados

### Módulo Especificação

**Core Domain**:
- `especificacao/core/domain/Especificacao.java` - Entidade domain
- `especificacao/core/excecoes/EspecificacaoNaoEncontradoException.java`
- `especificacao/core/excecoes/EspecificacaoDuplicadoException.java`

**Adapter/JPA**:
- `especificacao/adpter/out/entidadeJpa/EspecificacaoJpa.java` - Entidade JPA
- `especificacao/adpter/out/mapper/EspecificacaoBancoMapper.java` - Mapper domain ↔ JPA
- `especificacao/adpter/out/repositorio/EspecificacaoRepository.java` - Repository
- `especificacao/adpter/out/jpaAdapter/EspecificacaoCommandAdapter.java` - Adapter para operações de escrita
- `especificacao/adpter/out/jpaAdapter/EspecificacaoQueryAdapter.java` - Adapter para leitura

**Application Handlers - Commands**:
- `especificacao/application/command/salvar/` - Salvar nova especificação
- `especificacao/application/command/editar/` - Editar especificação existente
- `especificacao/application/command/arquivar/` - Arquivar especificação
- `especificacao/application/command/desarquivar/` - Reativar especificação
- `especificacao/application/command/deletar/` - Deletar especificação

**Application Handlers - Queries**:
- `especificacao/application/query/buscar/` - Buscar por ID
- `especificacao/application/query/listar/` - Listar especificações ativas
- `especificacao/application/query/listarArquivado/` - Listar especificações arquivadas

**Controller**:
- `especificacao/adpter/in/controller/EspecificacaoController.java` - REST endpoints
- `especificacao/adpter/in/controller/dto/EspecificacaoRequestDto.java` - DTO para requisição
- `especificacao/adpter/in/controller/dto/EspecificacaoResponseDto.java` - DTO para resposta

### Arquivos Modificados

**Pessoa - Domain**:
- `pessoa/core/domain/Pessoa.java` - Atualizada com novos campos e referência a Especificacao

**Pessoa - Adapter/JPA**:
- `pessoa/adpter/out/entidadeJpa/PessoaJpa.java` - Adicionar FK para Especificacao, novos campos
- `pessoa/adpter/out/mapper/PessoaBancoMapper.java` - Atualizar mapeamento

**Pessoa - Application**:
- `pessoa/application/comand/salvar/PessoaCommandSalvar.java` - Atualizar DTO
- `pessoa/application/comand/salvar/PessoaCommandSalvarHandler.java` - Atualizar lógica
- `pessoa/application/comand/editar/PessoaCommandEditar.java` - Atualizar DTO
- `pessoa/application/comand/editar/PessoaCommandEditarHandler.java` - Atualizar lógica

**Pessoa - Controller**:
- `pessoa/adpter/in/controller/PessoaController.java` - Atualizar chamadas
- `pessoa/adpter/in/controller/dto/PessoaRequestDto.java` - Adicionar novos campos
- `pessoa/adpter/in/controller/dto/PessoaResponseDto.java` - Adicionar novos campos

### Migrações de Banco de Dados

- `V15__criar_tabela_especificacoes.sql` - Cria tabela de especificações
- `V16__adicionar_especificacao_pessoas.sql` - Adiciona colunas a pessoas e cria especificação padrão

## Endpoints REST

### Especificações

```
POST   /api/especificacao                    - Criar especificação
GET    /api/especificacao/{id}               - Buscar por ID
GET    /api/especificacao/listar             - Listar ativas
GET    /api/especificacao/listar/arquivado   - Listar arquivadas
PUT    /api/especificacao/{id}               - Editar
PATCH  /api/especificacao/{id}/arquivar      - Arquivar
PATCH  /api/especificacao/{id}/desarquivar   - Reativar
DELETE /api/especificacao/{id}               - Deletar
```

### Pessoas (Alterados)

```
POST   /api/pessoa                           - Criar pessoa (novo payload)
GET    /api/pessoa/{id}                      - Buscar por ID
GET    /api/pessoa/listar                    - Listar ativas
GET    /api/pessoa/listar/arquivado          - Listar arquivadas
PUT    /api/pessoa/{id}                      - Editar (novo payload)
PATCH  /api/pessoa/{id}/arquivar             - Arquivar
PATCH  /api/pessoa/{id}/desarquivar          - Reativar
DELETE /api/pessoa/{id}                      - Deletar
```

## Payload de Exemplo

### Criar Especificação

```json
POST /api/especificacao
{
  "nome": "Oncologico",
  "prioridade": "ALTA",
  "observacao": "Pacientes oncológicos com alta prioridade"
}
```

**Response**:
```json
201 Created
Location: /api/especificacao/{id}
```

### Criar Pessoa

```json
POST /api/pessoa
{
  "nome": "João Silva",
  "observacao": "Paciente de diálise",
  "especificacaoId": "uuid-da-especificacao-oncologico",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 2,
  "endereco": "Rua A, 123",
  "cpf": "12345678901",
  "telefone": "11987654321"
}
```

### Listar Pessoas

```json
GET /api/pessoa/listar?nome=&pagina=0&tamanho=20

Response:
{
  "conteudo": [
    {
      "id": "uuid",
      "nome": "João Silva",
      "observacao": "Paciente de diálise",
      "especificacaoId": "uuid-oncologico",
      "especificacaoNome": "Oncologico",
      "buscarNaResidencia": true,
      "quantidadeAcompanhantes": 2,
      "endereco": "Rua A, 123",
      "cpf": "12345678901",
      "telefone": "11987654321",
      "dataArquivado": null
    }
  ],
  "pagina": 0,
  "tamanho": 20,
  "totalElementos": 1,
  "totalPaginas": 1
}
```

## Uso em Agendamento

Ao criar/buscar um agendamento, a lógica deve ser:

```java
// 1. Buscar pessoa
Pessoa pessoa = pessoaRepository.findById(pessoaId);

// 2. Puxar informações de Pessoa (não de Agendamento)
boolean buscarNaResidencia = pessoa.isBuscarNaResidencia();
int quantidadeAcompanhantes = pessoa.getQuantidadeAcompanhantes();
Especificacao especificacao = pessoa.getEspecificacao();
EnumPrioridade prioridade = especificacao.getPrioridade();

// 3. Usar essas informações para processamento
// (manter também em Agendamento para auditoria, se desejar)
```

## Considerações de Compatibilidade

### Dados Históricos

A migração `V16` cria uma especificação padrão ("Padrão" com prioridade MODERADA) e associa todas as pessoas existentes a ela. Isso mantém a compatibilidade com dados históricos.

### Campo `prioridade` em Pessoa

O campo `prioridade` (enum) foi removido de `Pessoa.java` e da tabela, já que agora está em `Especificacao`. A coluna na tabela de banco pode ser mantida por alguns releases para compatibilidade, se necessário.

## Próximos Passos (Opcional)

1. **Testes de integração** para validar fluxo completo
2. **Atualizar documentação de API** (Swagger)
3. **Implementar validação** de Especificação obrigatória em Pessoa
4. **Dados de seed** para Especificações padrões (se aplicável ao projeto)
5. **Auditoria**: Registrar mudanças de Especificação em Pessoa

---

**Data**: 2024
**Status**: Implementado ✓
