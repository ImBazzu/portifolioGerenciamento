# 🚀 QUICK START - Como Começar Agora

## ⚡ 5 Minutos - Comece Aqui

### 1️⃣ **Entender o que foi feito** (2 min)

Leia este resumo:
```
✅ Checkpoint 1: Nova entidade Especificacao (replace do enum prioridade)
✅ Checkpoint 2: Dias da semana e intervalo de repetição em regras

Resultado: Código compilado, migrações prontas, 12+ documentos
```

### 2️⃣ **Compilar localmente** (1 min)

```bash
cd backend
mvn clean compile -q
# Resultado: BUILD SUCCESS em ~8 segundos
```

### 3️⃣ **Ver os documentos principais** (2 min)

```
Raiz do projeto:
  - RESUMO_EXECUTIVO_FINAL.md ← LEIA PRIMEIRO
  - INDICE_DOCUMENTACAO_COMPLETA.md ← Índice de tudo
  - GUIA_TESTES.md ← Teste endpoints
  - GUIA_TESTES_REGRAS.md ← Teste regras novo
```

---

## 📚 15 Minutos - Entender Tudo

### **Tempo alocado:**
- 5 min: Leia RESUMO_EXECUTIVO_FINAL.md
- 5 min: Leia GUIA_TESTES.md (exemplos de requisições)
- 5 min: Compile + teste uma requisição

### **O que você entenderá:**
✅ Arquitetura implementada
✅ Novas features (Especificacao + Dias/Intervalo)
✅ Como fazer requisições
✅ Próximos passos

---

## 💻 30 Minutos - Validar Localmente

### **1. Compilar (5 min)**
```bash
cd backend
mvn clean package -DskipTests -q
# Resultado: secretaria-transporte-0.0.2.jar (66.3 MB)
```

### **2. Iniciar banco de dados (5 min)**
```bash
# Se usar docker-compose
docker-compose up -d postgresql

# Ou conectar ao banco existente
# Flyway executará as migrações automaticamente:
# - V15: Criar tabela especificacoes
# - V16: Adicionar FK em pessoas
# - V17: Adicionar dias e intervalo em regras
```

### **3. Iniciar aplicação (10 min)**
```bash
cd backend
mvn spring-boot:run

# Aguarde até ver: "Started Application in X seconds"
# API estará disponível em: http://localhost:8080
```

### **4. Testar 1 endpoint (10 min)**

**Criar Especificacao:**
```bash
curl -X POST http://localhost:8080/api/especificacao \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer seu-token" \
  -d '{
    "nome": "Oncologico",
    "prioridade": "ALTA",
    "observacao": "Pacientes oncológicos"
  }'
```

**Esperado:** Response HTTP 201 Created com ID da Especificacao

---

## 🎯 1 Hora - Validação Completa

### **Checklist de Validação**

#### **Especificacao CRUD** (20 min)
- [ ] POST - Criar especificacao
- [ ] GET - Buscar por ID
- [ ] PUT - Editar
- [ ] PATCH - Arquivar
- [ ] PATCH - Desarquivar
- [ ] DELETE - Deletar
- [ ] GET /listar - Listar todas
- [ ] GET /listar/arquivado - Listar arquivadas

#### **Pessoa com Especificacao** (20 min)
- [ ] POST - Criar pessoa com especificação
- [ ] GET - Buscar pessoa
- [ ] PUT - Editar pessoa (atualizar especificação)
- [ ] Validar relacionamento Pessoa ↔ Especificacao

#### **Regra com Dias/Intervalo** (20 min)
- [ ] PUT /lista/{id}/regras - Criar com diasSemana
- [ ] PUT /lista/{id}/regras - Criar com intervaloRepetacao
- [ ] GET /lista/{id} - Verificar dados retornados
- [ ] Validar combinação dias + intervalo

---

## 📖 Documentação por Perfil

### **👨‍💻 Desenvolvedor**
```
1. RESUMO_ALTERACOES.md - Veja o que mudou
2. DIAGRAMA_ENTIDADES.md - Entenda a arquitetura
3. GUIA_TESTES.md - Veja exemplos
4. Código: backend/src/main/java/br/com/imbazzu/secretariaTransporte/
```

### **🧪 QA/Testador**
```
1. GUIA_TESTES.md - Endpoints de Especificacao
2. GUIA_TESTES_REGRAS.md - Endpoints de Regras com dias/intervalo
3. Executar todos os scenarios
4. Validar validações (erros esperados)
```

### **🚀 DevOps**
```
1. MIGRACAO_DETALHADA.md - Instruções de deploy
2. Migrações: V15, V16, V17 em db/migration/
3. JAR: secretaria-transporte-0.0.2.jar (66.3 MB)
4. Validar porta (8080) e banco de dados
```

### **👔 Product Owner**
```
1. RESUMO_PORTUGUES.md - Resumo prático
2. GUIA_TESTES.md - Veja funcionalidades
3. DIAGRAMA_ENTIDADES.md - Fluxos de negócio
4. STATUS_FINAL_CHECKPOINT.md - Status de tudo
```

---

## 🔗 Links Rápidos para Documentos

| Documento | Descrição | Leia se... |
|-----------|-----------|-----------|
| **RESUMO_EXECUTIVO_FINAL.md** | Visão geral com métricas | Quer entender tudo em 5 min |
| **INDICE_DOCUMENTACAO_COMPLETA.md** | Índice centralizado | Quer saber onde está cada coisa |
| **GUIA_TESTES.md** | Endpoints com exemplos | Quer testar localmente |
| **GUIA_TESTES_REGRAS.md** | Testes de regras novo | Quer testar dias/intervalo |
| **DIAGRAMA_ENTIDADES.md** | Modelos e fluxos | Quer entender arquitetura |
| **RESUMO_ALTERACOES.md** | Detalhes de cada arquivo | Quer conhecer o código |
| **MIGRACAO_DETALHADA.md** | Deploy e troubleshooting | Quer fazer deploy |
| **RESUMO_PORTUGUES.md** | Resumo executivo | Quer apresentar ao cliente |
| **STATUS_FINAL_CHECKPOINT.md** | Status final | Quer validar completude |
| **VERIFICACAO_FINAL.md** | Build + verificação | Quer garantir que tudo funciona |

---

## 📊 Arquitetura em 1 Minuto

```
┌─ Pessoa ──────────────────────────────┐
│ - Nome, CPF, Endereço, etc           │
│ + especificacao (FK)                 │ ◄─── NOVO
│ + buscarNaResidencia (boolean)       │ ◄─── NOVO
│ + quantidadeAcompanhantes (int)      │ ◄─── NOVO
└────────────────────────────────────────┘
              ↓ (N:1)
┌─ Especificacao ────────────────────────┐
│ - Nome (Oncologico, TEA 1, etc)       │ ◄─── NOVO
│ - Prioridade (ALTA, MODERADA, BAIXA)  │
│ - Observacao                           │
│ - dataArquivado (soft delete)         │
└────────────────────────────────────────┘

┌─ RegraCadastro ───────────────────────┐
│ - ... (campos existentes)              │
│ + diasSemana (seg-dom opcionais)      │ ◄─── NOVO
│ + intervaloRepetacao (1-52 semanas)   │ ◄─── NOVO
└────────────────────────────────────────┘
```

---

## 🚨 Troubleshooting Rápido

### **Erro: "Especificacao não encontrada"**
```
Causa: ID inválido ou especificacao não existe
Solução: Crie uma especificacao primeiro antes de usar
```

### **Erro: "O intervalo de repetição deve estar entre 1 e 52"**
```
Causa: Valor fora do range permitido
Solução: Use 1-52 para intervaloRepetacao
```

### **Erro: "A lista de dias contém um valor inválido"**
```
Causa: DayOfWeek inválido ou null
Solução: Use: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
```

### **Erro: "BUILD FAILURE"**
```
Causa: Dependências não instaladas
Solução: mvn clean install && mvn clean compile
```

---

## 📝 Payloads Pronto para Copiar/Colar

### **Criar Especificacao**
```json
{
  "nome": "Oncologico",
  "prioridade": "ALTA",
  "observacao": "Pacientes oncológicos"
}
```

### **Criar Pessoa com Especificacao**
```json
{
  "nome": "Maria Santos",
  "especificacaoId": "uuid-oncologico",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 2
}
```

### **Criar Regra com Dias e Intervalo**
```json
{
  "regrasCadastro": [{
    "idsVeiculos": ["uuid-veiculo"],
    "idsCidades": [],
    "prioridades": ["MODERADA"],
    "inicioHorario": "08:00:00",
    "finalHorario": "17:00:00",
    "obrigacaoAnexo": false,
    "descricao": "Segunda a sexta a cada 2 semanas",
    "diasSemana": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "intervaloRepetacao": 2
  }],
  "regrasSeparacao": []
}
```

---

## ✅ Checklist - Comece Agora!

- [ ] Leia RESUMO_EXECUTIVO_FINAL.md (5 min)
- [ ] Execute `mvn clean compile` (10 min)
- [ ] Leia GUIA_TESTES.md (10 min)
- [ ] Teste 1 endpoint com cURL (5 min)
- [ ] Leia INDICE_DOCUMENTACAO_COMPLETA.md (5 min)
- [ ] Próximo: Testes de integração completa

---

## 🎯 Próximos Passos

### **Hoje**
1. Compile o código
2. Leia documentação principal
3. Teste 1-2 endpoints

### **Amanhã**
1. Teste todos os endpoints
2. Valide relacionamentos de dados
3. Teste soft delete

### **Esta Semana**
1. QA testa todos os scenarios
2. Product Owner valida funcionalidades
3. Decisão de deploy

### **Próxima Semana**
1. Deploy em staging
2. Testes de aceitação
3. Deploy em produção

---

## 📞 Help

```
Dúvida?               Consulte
════════════════════════════════════════
"Por onde começo?"    → RESUMO_EXECUTIVO_FINAL.md
"Como compilo?"       → VERIFICACAO_FINAL.md
"Como testo?"         → GUIA_TESTES.md
"Qual é a arquitetura?" → DIAGRAMA_ENTIDADES.md
"Onde está X?"        → INDICE_DOCUMENTACAO_COMPLETA.md
"Como faço deploy?"   → MIGRACAO_DETALHADA.md
"Qual é o status?"    → STATUS_FINAL_CHECKPOINT.md
```

---

## 🎉 Pronto? Vamos Lá!

```
1. mvn clean compile
2. Leia RESUMO_EXECUTIVO_FINAL.md
3. Teste endpoints em GUIA_TESTES.md
4. Report sua validação
5. Comece o deploy

Sucesso! 🚀
```

---

**Tempo recomendado para leitura completa:** 1 hora
**Tempo para validação completa:** 2 horas
**Tempo para deploy:** 30 minutos

Vamos começar? 🚀
