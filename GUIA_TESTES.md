# Guia de Testes - Refatoração de Pessoa e Especificação

## 1. Iniciar a Aplicação

```bash
cd backend
mvn spring-boot:run
```

A aplicação iniciará e as migrações de banco serão executadas automaticamente.

## 2. Verificar Tabelas Criadas

```sql
-- Verificar tabela de especificações
SELECT * FROM especificacoes;

-- Verificar tabela de pessoas (com novos campos)
DESC pessoas;

-- Verificar relacionamento
SELECT p.id, p.nome, e.nome as especificacao, e.prioridade, 
       p.buscar_na_residencia, p.quantidade_acompanhantes
FROM pessoas p
JOIN especificacoes e ON p.especificacao_id = e.id;
```

## 3. Testes via Swagger/Postman

### 3.1 Criar Especificação

**Endpoint**: `POST /api/especificacao`

**Body**:
```json
{
  "nome": "Oncologico",
  "prioridade": "ALTA",
  "observacao": "Pacientes oncológicos"
}
```

**Response**: `201 Created`
```
Location: /api/especificacao/{id}
```

### 3.2 Listar Especificações

**Endpoint**: `GET /api/especificacao/listar?pagina=0&tamanho=20`

**Response**: `200 OK`
```json
{
  "conteudo": [
    {
      "id": "uuid",
      "nome": "Oncologico",
      "prioridade": "ALTA",
      "observacao": "Pacientes oncológicos",
      "dataArquivado": null
    }
  ],
  "pagina": 0,
  "tamanho": 20,
  "totalElementos": 1,
  "totalPaginas": 1
}
```

### 3.3 Criar Pessoa

**Endpoint**: `POST /api/pessoa`

**Body**:
```json
{
  "nome": "Maria Silva",
  "observacao": "Paciente em acompanhamento",
  "especificacaoId": "{uuid-da-especificacao-oncologico}",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 1,
  "endereco": "Rua Principal, 123",
  "cpf": "12345678901",
  "telefone": "11987654321"
}
```

**Response**: `201 Created`
```
Location: /api/pessoa/{id}
```

### 3.4 Buscar Pessoa

**Endpoint**: `GET /api/pessoa/{id}`

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "nome": "Maria Silva",
  "observacao": "Paciente em acompanhamento",
  "especificacaoId": "uuid-oncologico",
  "especificacaoNome": "Oncologico",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 1,
  "endereco": "Rua Principal, 123",
  "cpf": "12345678901",
  "telefone": "11987654321",
  "dataArquivado": null
}
```

### 3.5 Editar Pessoa

**Endpoint**: `PUT /api/pessoa/{id}`

**Body**:
```json
{
  "nome": "Maria Silva Santos",
  "observacao": "Paciente em acompanhamento - atualizado",
  "especificacaoId": "{uuid-da-outra-especificacao}",
  "buscarNaResidencia": false,
  "quantidadeAcompanhantes": 2,
  "endereco": "Rua Principal, 456",
  "cpf": "12345678901",
  "telefone": "11987654321"
}
```

**Response**: `204 No Content`

### 3.6 Listar Pessoas

**Endpoint**: `GET /api/pessoa/listar?pagina=0&tamanho=20`

**Response**: `200 OK` (com array de pessoas contendo novos campos)

## 4. Validações Esperadas

### 4.1 Especificação
- ✓ Nome deve ser único
- ✓ Nome obrigatório (3-100 caracteres)
- ✓ Prioridade obrigatória (BAIXA, MODERADA, ALTA, URGENTE)
- ✓ Pode ser arquivada/desarquivada
- ✓ Pode ser deletada

### 4.2 Pessoa
- ✓ Nome obrigatório (3-59 caracteres)
- ✓ Telefone obrigatório
- ✓ Especificação obrigatória (FK válida)
- ✓ buscarNaResidencia obrigatório (boolean)
- ✓ quantidadeAcompanhantes obrigatório (int >= 0)
- ✓ CPF único (opcional)
- ✓ Pode ser arquivada/desarquivada
- ✓ Pode ser deletada

## 5. Teste de Integração com Agendamento (Futuro)

Quando agendamento for atualizado:

```java
// Ao criar/buscar agendamento:
Pessoa pessoa = pessoaRepository.findById(agendamento.getIdPessoa());

// Puxar informações de Pessoa (não mais de Agendamento)
boolean buscarNaResidencia = pessoa.isBuscarNaResidencia();
int acompanhantes = pessoa.getQuantidadeAcompanhantes();
Especificacao esp = pessoa.getEspecificacao();
EnumPrioridade prioridade = esp.getPrioridade();

// Usar essas informações
```

## 6. Dados de Teste (Optional - SQL)

```sql
-- Criar especificações de teste
INSERT INTO especificacoes (id, nome, prioridade, observacao, data_arquivado) VALUES
(UUID(), 'Oncologico', 'ALTA', 'Pacientes com câncer', NULL),
(UUID(), 'TEA 1', 'MODERADA', 'Transtorno do Espectro Autista Nível 1', NULL),
(UUID(), 'Dialise', 'URGENTE', 'Pacientes em tratamento de diálise', NULL),
(UUID(), 'Reumatologia', 'MODERADA', 'Doenças reumáticas', NULL);

-- Criar pessoa de teste
INSERT INTO pessoas (id, nome, especificacao_id, buscar_na_residencia, quantidade_acompanhantes, 
                     telefone, endereco, cpf, observacao, data_arquivado) VALUES
(UUID(), 'João Teste', (SELECT id FROM especificacoes WHERE nome='Oncologico'),
 true, 1, '11987654321', 'Rua Teste, 123', '12345678901', 'Pessoa de teste', NULL);
```

## 7. Verificação de Logs

Buscar por mensagens de erro durante:
- Inicialização (migrações)
- Criação de Especificação (duplicação de nome)
- Criação de Pessoa (especificação não encontrada)
- Busca de dados relacionados

## 8. Checklist Final

- [ ] Aplicação inicia sem erros
- [ ] Tabelas criadas corretamente
- [ ] CRUD de Especificação funciona
- [ ] CRUD de Pessoa funciona com novos campos
- [ ] Relacionamento Pessoa ↔ Especificação funciona
- [ ] Validações funcionam corretamente
- [ ] Respostas da API contêm novos campos
- [ ] Dados históricos foram migrados para especificação padrão

---

**Observação**: Todos os endpoints requerem autenticação via Bearer Token (security requirement).
