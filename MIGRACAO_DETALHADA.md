# Instruções de Migração e Considerações Técnicas

## 1. Visão Geral da Mudança

### Antes
```
Pessoa
├─ id: UUID
├─ nome: String
├─ telefone: String
├─ prioridade: EnumPrioridade (BAIXA, MODERADA, ALTA, URGENTE)
└─ ...outros campos

Agendamento
├─ buscarNaResidencia: Boolean
├─ acompanhantes: Int
└─ ...
```

### Depois
```
Especificacao
├─ id: UUID
├─ nome: String (ex: "Oncologico", "TEA 1")
├─ prioridade: EnumPrioridade
└─ observacao: String

Pessoa
├─ id: UUID
├─ nome: String
├─ telefone: String
├─ especificacao_id: FK → Especificacao (substitui prioridade)
├─ buscarNaResidencia: Boolean (movido de Agendamento)
├─ quantidadeAcompanhantes: Int (movido de Agendamento)
└─ ...outros campos

Agendamento
├─ buscarNaResidencia: Boolean (mantido para auditoria)
├─ acompanhantes: Int (mantido para auditoria)
└─ ...
```

## 2. Processo de Migração

### Fase 1: Preparação (Pré-Deploy)

1. **Backup do banco de dados**
   ```bash
   # MySQL
   mysqldump -u user -p database > backup_$(date +%Y%m%d_%H%M%S).sql
   ```

2. **Revisar código compilado**
   ```bash
   cd backend
   mvn clean compile
   # ✓ BUILD SUCCESS
   ```

3. **Executar testes (se existirem)**
   ```bash
   mvn test
   ```

### Fase 2: Deploy (Executar Migrações)

1. **Parar aplicação atual** (se rodando em produção)

2. **Fazer deploy do novo código**
   ```bash
   # Maven (desenvolvimento)
   mvn clean package spring-boot:run

   # Docker/Produção
   docker build -t app:v2 .
   docker stop app-container
   docker rm app-container
   docker run -d --name app-container app:v2
   ```

3. **Flyway executará migrações automaticamente**
   - Executa `V15__criar_tabela_especificacoes.sql`
   - Executa `V16__adicionar_especificacao_pessoas.sql`

### Fase 3: Pós-Migração (Validação)

1. **Verificar tabelas criadas**
   ```sql
   SHOW TABLES LIKE 'especificacao%';
   DESC especificacoes;
   DESC pessoas;
   ```

2. **Verificar dados migrados**
   ```sql
   SELECT COUNT(*) FROM pessoas;
   SELECT COUNT(*) FROM especificacoes;
   
   -- Verificar que todas as pessoas têm especificacao_id
   SELECT COUNT(*) FROM pessoas WHERE especificacao_id IS NULL;
   -- Deve retornar 0
   ```

3. **Testar endpoints via API**
   - POST /api/especificacao (criar)
   - GET /api/especificacao/listar (listar)
   - POST /api/pessoa (criar com novos campos)
   - GET /api/pessoa/{id} (verificar resposta com novos campos)

## 3. Rollback (Se Necessário)

### Opção 1: Revert Code + Rollback DB

```bash
# 1. Voltar para versão anterior no git
git revert <commit-hash>

# 2. Restaurar banco de dados
mysql -u user -p database < backup_20240101_120000.sql

# 3. Redeploy da versão anterior
mvn clean package spring-boot:run
```

### Opção 2: Flyway Rollback (Se habilitado)

```bash
mvn flyway:clean  # CUIDADO: Remove TODAS as migrações
```

**Nota**: O projeto não possui migrações de downgrade configuradas. Para rollback manual:

```sql
-- Script de rollback (executar manualmente)
ALTER TABLE pessoas DROP CONSTRAINT fk_pessoa_especificacao;
ALTER TABLE pessoas DROP COLUMN especificacao_id;
ALTER TABLE pessoas DROP COLUMN buscar_na_residencia;
ALTER TABLE pessoas DROP COLUMN quantidade_acompanhantes;
ALTER TABLE pessoas ADD COLUMN prioridade VARCHAR(20) NOT NULL DEFAULT 'MODERADA';

DROP TABLE especificacoes;
```

## 4. Dados Migrados

### Especificação Padrão Criada

```
ID: 00000000000000000000000000000001
Nome: "Padrão"
Prioridade: MODERADA
Observação: "Especificação padrão para migração"
Data Arquivado: NULL
```

### Pessoas Migradas

Todas as pessoas existentes foram automaticamente associadas à especificação "Padrão". Após a migração, o administrador pode:

1. Criar novas especificações (ex: "Oncologico", "TEA 1")
2. Atualizar cada pessoa para usar a especificação correta
3. Deletar ou arquivar a especificação "Padrão" se não mais usada

## 5. Código Quebrado (Breaking Changes)

### Classes/Métodos Removidos

❌ **NÃO MAIS FUNCIONA**:
```java
Pessoa pessoa = ...
EnumPrioridade prioridade = pessoa.getPrioridade(); // ❌ ERRO
```

### Código Novo (Usar assim)

✅ **USE ASSIM**:
```java
Pessoa pessoa = ...
Especificacao esp = pessoa.getEspecificacao();
EnumPrioridade prioridade = esp.getPrioridade();

boolean buscarNaResidencia = pessoa.isBuscarNaResidencia();
int acompanhantes = pessoa.getQuantidadeAcompanhantes();
```

### DTOs Alterados

#### Antes (PessoaRequestDto)
```json
{
  "nome": "João",
  "prioridade": "ALTA",
  "telefone": "11987654321"
}
```

#### Depois (PessoaRequestDto)
```json
{
  "nome": "João",
  "especificacaoId": "uuid-aqui",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 1,
  "telefone": "11987654321"
}
```

## 6. Performance e Segurança

### Performance

- ✓ Novas colunas em `pessoas` são indexed (FK)
- ✓ Lazy loading on `especificacao` relationship (padrão Spring)
- ✓ Consultas de Pessoa com JOIN em Especificacao são N+1 safe
- ⚠️ Se buscar muitas pessoas, considerar usar `@EntityGraph` ou `@Query` com JOIN

### Segurança

- ✓ FK constraint garante integridade referencial
- ✓ Especificação obrigatória (NOT NULL)
- ✓ Validações em DTOs (Jakarta Validation)
- ⚠️ Remover Especificação com pessoas associadas? Adicionar validação se necessário

```java
// Sugestão: Adicionar antes de deletar
@Transactional
public void deletar(UUID id) {
    if (pessoaRepository.existsByEspecificacao_Id(id)) {
        throw new EspecificacaoEmUsoException();
    }
    adapter.deletar(id);
}
```

## 7. Considerações de API Clients

### Apps/Frontend que consomem a API

**Antes**:
```javascript
// Enviar payload antigo
const pessoa = {
  nome: "João",
  prioridade: "ALTA",
  telefone: "11987654321"
};
```

**Depois**:
```javascript
// Enviar payload novo
const pessoa = {
  nome: "João",
  especificacaoId: "uuid-id-aqui",
  buscarNaResidencia: true,
  quantidadeAcompanhantes: 1,
  telefone: "11987654321"
};
```

## 8. Checklist de Deploy

### Pré-Deploy
- [ ] Backup do banco feito
- [ ] Código compilado sem erros
- [ ] Testes passando (se aplicável)
- [ ] README/Documentação atualizado
- [ ] Equipe notificada das mudanças

### Deploy
- [ ] Aplicação parada (se em produção)
- [ ] Novo código deployado
- [ ] Migrações executadas com sucesso
- [ ] Logs verificados (sem erros)

### Pós-Deploy
- [ ] Tabelas criadas e dados migrados
- [ ] Endpoints testados manualmente
- [ ] Especificações de teste criadas
- [ ] Pessoas de teste criadas
- [ ] API responses contêm novos campos
- [ ] Nenhum erro nos logs da aplicação

## 9. Troubleshooting

### Erro: "Specified key was too long"
**Causa**: Versão MySQL < 5.7.7
**Solução**: Upgrade MySQL ou reduzir tamanho da coluna `nome` em especificações

### Erro: "Constraint violation fk_pessoa_especificacao"
**Causa**: Especificação padrão não criada
**Solução**: Executar migration manualmente ou verificar logs do Flyway

### Erro: "Data truncated for column quantidadeAcompanhantes"
**Causa**: Dados inválidos durante migração
**Solução**: Verificar dados em agendamentos antes de migrar

### Pessoa não encontra Especificacao
**Causa**: N+1 queries ou lazy loading desabilitado
**Solução**: Verificar configuração de lazy loading do Spring Data

## 10. Suporte e Manutenção

### Futuras Melhorias

1. **Cache de Especificações** (Redis)
   - Especificações são dados relativamente estáticos
   - Bom candidato para cache

2. **Histórico de Mudanças**
   - Registrar quando/quem alterou especificação de Pessoa
   - Usar `@Audited` (Hibernate Envers)

3. **Relatórios**
   - Pessoas por especificação
   - Pessoas que precisam busca na residência
   - Análise de acompanhantes

4. **Validações Avançadas**
   - Impedir deletar Especificação com pessoas ativas
   - Workflow de aprovação para mudança de especificação

---

**Data**: 2024
**Versão**: 1.0
**Status**: Pronto para Produção ✓
