# ÍNDICE RÁPIDO - Refatoração Pessoa & Especificação

## 📚 Documentação

| Documento | Propósito | Quando Consultar |
|-----------|-----------|------------------|
| [`SUMARIO_EXECUTIVO.md`](./SUMARIO_EXECUTIVO.md) | Visão geral do projeto | ⭐ **Comece aqui** |
| [`RESUMO_ALTERACOES.md`](./RESUMO_ALTERACOES.md) | Detalhe das mudanças | Entender o que foi feito |
| [`GUIA_TESTES.md`](./GUIA_TESTES.md) | Como testar os endpoints | Testar a implementação |
| [`MIGRACAO_DETALHADA.md`](./MIGRACAO_DETALHADA.md) | Deploy e troubleshooting | Fazer deploy em produção |
| [`DIAGRAMA_ENTIDADES.md`](./DIAGRAMA_ENTIDADES.md) | Modelos e diagramas | Entender a arquitetura |

---

## 🗂️ Estrutura de Arquivos Criados

### Novo Módulo: `especificacao/`

**Localização**: `backend/src/main/java/br/com/imbazzu/secretariaTransporte/especificacao/`

#### Core Domain
```
especificacao/core/
├── domain/
│   └── Especificacao.java               ← Entidade principal
└── excecoes/
    ├── EspecificacaoNaoEncontradoException.java
    └── EspecificacaoDuplicadoException.java
```

#### Adapter/Persistence
```
especificacao/adpter/
├── out/
│   ├── entidadeJpa/
│   │   └── EspecificacaoJpa.java       ← Entidade JPA
│   ├── mapper/
│   │   └── EspecificacaoBancoMapper.java ← Domain ↔ JPA
│   ├── repositorio/
│   │   └── EspecificacaoRepository.java  ← Spring Data
│   └── jpaAdapter/
│       ├── EspecificacaoCommandAdapter.java  ← Write ops
│       └── EspecificacaoQueryAdapter.java    ← Read ops
└── in/
    └── controller/
        ├── EspecificacaoController.java
        └── dto/
            ├── EspecificacaoRequestDto.java
            └── EspecificacaoResponseDto.java
```

#### Application Layer
```
especificacao/application/
├── command/
│   ├── salvar/
│   │   ├── EspecificacaoCommandSalvar.java
│   │   └── EspecificacaoCommandSalvarHandler.java
│   ├── editar/
│   │   ├── EspecificacaoCommandEditar.java
│   │   └── EspecificacaoCommandEditarHandler.java
│   ├── arquivar/
│   │   ├── EspecificacaoCommandArquivar.java
│   │   └── EspecificacaoCommandArquivarHandler.java
│   ├── desarquivar/
│   │   ├── EspecificacaoCommandDesarquivar.java
│   │   └── EspecificacaoCommandDesarquivarHandler.java
│   └── deletar/
│       ├── EspecificacaoCommandDeletar.java
│       └── EspecificacaoCommandDeletarHandler.java
└── query/
    ├── buscar/
    │   ├── EspecificacaoQueryBuscar.java
    │   └── EspecificacaoQueryBuscarHandler.java
    ├── listar/
    │   ├── EspecificacaoQueryListar.java
    │   └── EspecificacaoQueryListarHandler.java
    └── listarArquivado/
        ├── EspecificacaoQueryListarArquivado.java
        └── EspecificacaoQueryListarArquivadoHandler.java
```

### Arquivos Modificados: `pessoa/`

```
pessoa/
├── core/domain/Pessoa.java (MODIFICADO)
├── adpter/
│   ├── out/
│   │   ├── entidadeJpa/PessoaJpa.java (MODIFICADO)
│   │   └── mapper/PessoaBancoMapper.java (MODIFICADO)
│   └── in/controller/
│       ├── PessoaController.java (MODIFICADO)
│       └── dto/
│           ├── PessoaRequestDto.java (MODIFICADO)
│           └── PessoaResponseDto.java (MODIFICADO)
└── application/comand/
    ├── salvar/
    │   ├── PessoaCommandSalvar.java (MODIFICADO)
    │   └── PessoaCommandSalvarHandler.java (MODIFICADO)
    └── editar/
        ├── PessoaCommandEditar.java (MODIFICADO)
        └── PessoaCommandEditarHandler.java (MODIFICADO)
```

### Migrações de Banco

```
backend/src/main/resources/db/migration/
├── V15__criar_tabela_especificacoes.sql (NOVO)
└── V16__adicionar_especificacao_pessoas.sql (NOVO)
```

---

## 🔗 Fluxos Principais

### Criar Especificação
```
Controller.criar(@RequestBody EspecificacaoRequestDto)
  ↓
Handler.handler(Command)
  ├─ Valida nome único
  ├─ Especificacao.criar(...)
  └─ Adapter.salvar()
     └─ Repository.saveAndFlush()
        └─ DB
```

### Listar Pessoas (com Especificação)
```
GET /api/pessoa/listar
  ↓
QueryBuscarHandler.handler(Query)
  ↓
Adapter.listarAtivas(nome, paginacao)
  ├─ Query com JOIN entre pessoas e especificacoes
  └─ Mapper.paraDomain()
     └─ ResponseDto com especificacaoId e especificacaoNome
```

---

## 📝 Payloads de Exemplo

### POST /api/especificacao
```json
{
  "nome": "Oncologico",
  "prioridade": "ALTA",
  "observacao": "Pacientes oncológicos com alta prioridade"
}
```

### POST /api/pessoa
```json
{
  "nome": "João Silva",
  "observacao": "Paciente em acompanhamento",
  "especificacaoId": "uuid-aqui",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 1,
  "endereco": "Rua Principal, 123",
  "cpf": "12345678901",
  "telefone": "11987654321"
}
```

### GET /api/pessoa/{id}
```json
{
  "id": "uuid",
  "nome": "João Silva",
  "observacao": "Paciente em acompanhamento",
  "especificacaoId": "uuid",
  "especificacaoNome": "Oncologico",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 1,
  "endereco": "Rua Principal, 123",
  "cpf": "12345678901",
  "telefone": "11987654321",
  "dataArquivado": null
}
```

---

## 🔧 Comandos Úteis

### Build
```bash
cd backend
mvn clean compile       # Compilar
mvn clean package       # Compilar + Package
mvn spring-boot:run     # Compilar + Rodar
```

### Testes
```bash
mvn test                # Rodar testes
mvn test -Dtest=EspecificacaoControllerTest  # Um teste específico
```

### Banco de Dados
```sql
-- Ver estrutura de especificações
DESC especificacoes;

-- Ver estrutura de pessoas (com novos campos)
DESC pessoas;

-- Verificar relacionamentos
SELECT p.id, p.nome, e.nome as especificacao, e.prioridade 
FROM pessoas p 
JOIN especificacoes e ON p.especificacao_id = e.id;
```

---

## 🚀 Roteiro de Implementação

### Fase 1: Preparação ✅
- [x] Código desenvolvido
- [x] Código compilado
- [x] Migrações criadas
- [x] Documentação completa

### Fase 2: Teste (Próximo)
- [ ] Executar `mvn test`
- [ ] Testar endpoints com Postman/Insomnia
- [ ] Validar migrações de banco
- [ ] Verificar dados históricos migrados

### Fase 3: Deploy
- [ ] Backup do banco
- [ ] Deploy em staging
- [ ] Testes de integração
- [ ] Deploy em produção

### Fase 4: Pós-Deploy
- [ ] Verificar logs
- [ ] Testar endpoints em produção
- [ ] Criar dados de seed
- [ ] Treinar time

---

## ❓ FAQ Rápida

**P: Onde está o novo módulo Especificação?**
A: Em `backend/src/main/java/br/com/imbazzu/secretariaTransporte/especificacao/`

**P: Que campos foram adicionados a Pessoa?**
A: `especificacao_id`, `buscar_na_residencia`, `quantidade_acompanhantes`

**P: O campo `prioridade` foi removido de Pessoa?**
A: Sim. Agora acessa via `pessoa.getEspecificacao().getPrioridade()`

**P: E se eu precisar rollback?**
A: Ver seção de Rollback em `MIGRACAO_DETALHADA.md`

**P: Como testar os endpoints?**
A: Ver `GUIA_TESTES.md`

**P: O código compila?**
A: ✅ Sim! BUILD SUCCESS confirmado.

---

## 📞 Suporte

### Dúvidas?
1. Leia [`SUMARIO_EXECUTIVO.md`](./SUMARIO_EXECUTIVO.md) primeiro
2. Consulte [`RESUMO_ALTERACOES.md`](./RESUMO_ALTERACOES.md) para detalhes
3. Para problemas, ver [`MIGRACAO_DETALHADA.md`](./MIGRACAO_DETALHADA.md)

### Bugs?
1. Verificar logs da aplicação
2. Executar `mvn clean compile` novamente
3. Restaurar banco e tentar de novo

---

**Last Updated**: 18 de Novembro de 2024
**Status**: ✅ Pronto para Deploy
