# Guia de Testes - Dias da Semana e Intervalo de Repetição em Regras

## 🧪 Testes de Exemplo

### Scenario 1: Regra com Dias Específicos (Segunda a Sexta)

**Payload Request:**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": ["12345678-1234-1234-1234-123456789abc"],
      "idsCidades": [],
      "prioridades": ["BAIXA", "MODERADA"],
      "inicioHorario": "08:00:00",
      "finalHorario": "17:00:00",
      "obrigacaoAnexo": false,
      "descricao": "Agendamentos de segunda a sexta",
      "diasSemana": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
      "intervaloRepetacao": 1
    }
  ],
  "regrasSeparacao": []
}
```

**Comportamento Esperado:**
- ✅ Regra se aplica apenas de **segunda a sexta**
- ✅ Repetem **semanalmente** (cada 1 semana)
- ✅ Fim de semana (sábado/domingo): regra **não se aplica**

---

### Scenario 2: Regra a Cada 2 Semanas (Quinzenal)

**Payload Request:**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": ["87654321-4321-4321-4321-cba987654321"],
      "idsCidades": ["uuid-cidade-sao-paulo"],
      "prioridades": ["ALTA"],
      "inicioHorario": "09:00:00",
      "finalHorario": "16:00:00",
      "obrigacaoAnexo": true,
      "descricao": "Agendamentos em São Paulo a cada 2 semanas",
      "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
      "intervaloRepetacao": 2
    }
  ],
  "regrasSeparacao": []
}
```

**Comportamento Esperado:**
- ✅ Regra se aplica apenas **segunda, quarta e sexta**
- ✅ Repetem a **cada 2 semanas** (quinzenal)
- ✅ Exemplo: semana 1 → ativa, semana 2 → inativa, semana 3 → ativa

---

### Scenario 3: Regra para Todos os Dias (diasSemana vazio)

**Payload Request:**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": ["11111111-1111-1111-1111-111111111111"],
      "idsCidades": [],
      "prioridades": ["MODERADA"],
      "inicioHorario": "07:00:00",
      "finalHorario": "19:00:00",
      "obrigacaoAnexo": false,
      "descricao": "Agendamentos todos os dias",
      "diasSemana": [],
      "intervaloRepetacao": 1
    }
  ],
  "regrasSeparacao": []
}
```

**Comportamento Esperado:**
- ✅ Regra se aplica **todos os dias da semana**
- ✅ Repetem **semanalmente**
- ✅ Sem restrição de dias

---

### Scenario 4: Regra Mensal (4 Semanas)

**Payload Request:**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": ["22222222-2222-2222-2222-222222222222"],
      "idsCidades": [],
      "prioridades": ["ALTA", "MODERADA"],
      "inicioHorario": "10:00:00",
      "finalHorario": "15:00:00",
      "obrigacaoAnexo": false,
      "descricao": "Agendamentos mensais",
      "diasSemana": ["TUESDAY"],
      "intervaloRepetacao": 4
    }
  ],
  "regrasSeparacao": []
}
```

**Comportamento Esperado:**
- ✅ Regra se aplica apenas **às terças-feiras**
- ✅ Repetem a **cada 4 semanas** (aproximadamente mensal)

---

### Scenario 5: Teste de Validação - Intervalo Inválido

**Payload Request (SERÁ REJEITADO):**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": ["33333333-3333-3333-3333-333333333333"],
      "idsCidades": [],
      "prioridades": ["BAIXA"],
      "inicioHorario": "08:00:00",
      "finalHorario": "17:00:00",
      "obrigacaoAnexo": false,
      "descricao": "Teste com intervalo inválido",
      "diasSemana": ["MONDAY"],
      "intervaloRepetacao": 53
    }
  ],
  "regrasSeparacao": []
}
```

**Resposta Esperada (400 Bad Request):**
```json
{
  "titulo": "Validação falhou",
  "detalhes": "O intervalo de repetição deve estar entre 1 e 52 semanas"
}
```

---

### Scenario 6: Combinação Complexa

**Payload Request:**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": [
        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
        "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
      ],
      "idsCidades": [
        "cidade-rj",
        "cidade-mg"
      ],
      "prioridades": ["ALTA", "MODERADA", "BAIXA"],
      "inicioHorario": "06:00:00",
      "finalHorario": "20:00:00",
      "obrigacaoAnexo": true,
      "descricao": "Agendamentos em RJ e MG, segunda, quarta e sexta, a cada 3 semanas",
      "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
      "intervaloRepetacao": 3
    }
  ],
  "regrasSeparacao": []
}
```

**Comportamento Esperado:**
- ✅ 2 veículos aplicáveis
- ✅ Válido para cidades RJ e MG
- ✅ Todas as prioridades aceitas
- ✅ Horário ampliado (6h a 20h)
- ✅ Anexo obrigatório
- ✅ Apenas segunda, quarta, sexta
- ✅ A cada 3 semanas

---

## 🧪 Testes de Verificação de Regras

### Test Case 1: Verificar se Regra se Aplica ao Dia

**Entrada:**
- `diaSemana`: MONDAY
- `diasSemana da regra`: ["MONDAY", "WEDNESDAY", "FRIDAY"]

**Esperado:**
```
✅ Regra se aplica (resultado = true)
```

---

### Test Case 2: Dia Fora da Regra

**Entrada:**
- `diaSemana`: SUNDAY
- `diasSemana da regra`: ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]

**Esperado:**
```
❌ Regra NÃO se aplica (resultado = false)
```

---

### Test Case 3: Regra com diasSemana Vazio

**Entrada:**
- `diaSemana`: SUNDAY
- `diasSemana da regra`: []

**Esperado:**
```
✅ Regra se aplica (resultado = true - válida para qualquer dia)
```

---

## 📡 Endpoint de Teste

### URL:
```
PUT http://localhost:8080/api/lista/{id}/regras
```

### Headers:
```
Content-Type: application/json
Authorization: Bearer {seu-token-jwt}
```

### Exemplo com cURL:
```bash
curl -X PUT http://localhost:8080/api/lista/12345678-1234-1234-1234-123456789abc/regras \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer seu-token-aqui" \
  -d '{
    "regrasCadastro": [
      {
        "idsVeiculos": ["uuid-veiculo"],
        "idsCidades": [],
        "prioridades": ["MODERADA"],
        "inicioHorario": "08:00:00",
        "finalHorario": "17:00:00",
        "obrigacaoAnexo": false,
        "descricao": "Teste",
        "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
        "intervaloRepetacao": 2
      }
    ],
    "regrasSeparacao": []
  }'
```

---

## ✅ Validações Esperadas

| Campo | Validação | Exemplo Válido | Exemplo Inválido |
|-------|-----------|-----------------|------------------|
| `diasSemana` | Lista opcional, sem nulos | `["MONDAY"]` ou `[]` | `[null, "MONDAY"]` |
| `intervaloRepetacao` | 1-52 inteiros | `1`, `2`, `52` | `0`, `53`, `-1` |
| `idsVeiculos` | Obrigatório, não vazio | `["uuid"]` | `[]` ou `null` |
| `prioridades` | Obrigatório, não vazio | `["ALTA"]` | `[]` ou `null` |

---

## 🗄️ Consultas de Banco para Validação

### Verificar Regras Cadastradas:
```sql
SELECT 
    rc.id,
    rc.descricao,
    rc.intervalo_repetacao_semanas,
    GROUP_CONCAT(DISTINCT rcd.dia_semana) as dias_semana
FROM regras_cadastro rc
LEFT JOIN regra_cadastro_dias_semana rcd ON rc.id = rcd.regra_cadastro_id
WHERE rc.lista_id = '12345678-1234-1234-1234-123456789abc'
GROUP BY rc.id;
```

### Verificar Dias de uma Regra Específica:
```sql
SELECT dia_semana
FROM regra_cadastro_dias_semana
WHERE regra_cadastro_id = 'uuid-da-regra'
ORDER BY dia_semana;
```

### Verificar Intervalo de Repetição:
```sql
SELECT 
    id,
    descricao,
    intervalo_repetacao_semanas
FROM regras_cadastro
WHERE lista_id = 'uuid-da-lista'
AND intervalo_repetacao_semanas IS NOT NULL;
```

---

## 🔍 Troubleshooting

### Erro: "O intervalo de repetição deve estar entre 1 e 52 semanas"
- **Causa:** Valor fora do range permitido
- **Solução:** Use valores entre 1 e 52

### Erro: "A lista de dias da semana contém um valor inválido"
- **Causa:** Dia da semana inválido ou null
- **Solução:** Use DayOfWeek válidos: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

### Regra criada mas dias não aparecem
- **Causa:** Possível problema de persistência
- **Solução:** Verificar migration V17 foi executada (`SELECT * FROM regra_cadastro_dias_semana`)

---

## 📊 Status de Implementação

| Item | Status | Detalhes |
|------|--------|----------|
| ✅ Domain Logic | Completo | `RegraCadastro.java` |
| ✅ JPA Mapping | Completo | `RegraCadastroJpa.java` |
| ✅ DTO Validation | Completo | `RegraCadastroRequestDto.java` |
| ✅ Database Migration | Completo | `V17__adicionar_dias_semana_intervalo_regras.sql` |
| ✅ Mapper | Completo | `TipoListaBancoMapper.java` |
| ✅ Handler | Completo | `TipoListaCommandAtualizarRegrasHandler.java` |
| ✅ Controller | Completo | `TipoListaController.java` |
| ✅ Compilação | ✅ BUILD SUCCESS | Sem erros |

---

## 🎯 Próximas Validações

1. **Teste de Integração:** Executar requests contra a API
2. **Teste Unitário:** Validar lógica de verificação de dias
3. **Teste em Produção:** Validar agendamentos que usam essas regras
4. **Teste de Performance:** Validar índices criados em V17
