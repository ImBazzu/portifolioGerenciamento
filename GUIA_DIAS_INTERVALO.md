# 📖 Guia Prático: Dias da Semana e Intervalo de Repetição

## Visão Geral

Agora você pode criar regras de agendamento que se aplicam **somente em dias específicos** e se **repetem a cada N semanas**.

---

## 🎯 Casos de Uso

### Caso 1: Atendimento Regular Seg/Qua/Sex
**Cenário**: Paciente oncológico que faz tratamento 3 vezes por semana

```json
{
  "idsVeiculos": ["12345678-1234-1234-1234-123456789012"],
  "idsCidades": ["98765432-9876-9876-9876-987654321098"],
  "prioridades": ["ALTA"],
  "inicioHorario": "08:00:00",
  "finalHorario": "18:00:00",
  "obrigacaoAnexo": false,
  "descricao": "Oncológicos - atendimento 3x/semana",
  "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
  "intervaloRepetacao": 1
}
```

**Resultado**: 
- Busca na segunda, quarta e sexta
- Toda semana (intervalo = 1)
- Não busca terça, quinta, sábado ou domingo

---

### Caso 2: Atendimento Quinzenal (A cada 2 semanas)
**Cenário**: Paciente TEA que tem sessão a cada 2 semanas, sempre na terça

```json
{
  "idsVeiculos": ["12345678-1234-1234-1234-123456789012"],
  "idsCidades": ["98765432-9876-9876-9876-987654321098"],
  "prioridades": ["MODERADA"],
  "inicioHorario": "09:00:00",
  "finalHorario": "17:00:00",
  "obrigacaoAnexo": false,
  "descricao": "TEA - sessão quinzenal",
  "diasSemana": ["TUESDAY"],
  "intervaloRepetacao": 2
}
```

**Resultado**:
- Busca APENAS terça
- A cada 2 semanas (quinta-feira de uma semana, terça da outra)
- Não busca nos outros 6 dias da semana

---

### Caso 3: Sem Restrição de Dias (Aplica-se a qualquer dia)
**Cenário**: Paciente que pode ser agendado qualquer dia da semana

```json
{
  "idsVeiculos": ["12345678-1234-1234-1234-123456789012"],
  "idsCidades": ["98765432-9876-9876-9876-987654321098"],
  "prioridades": ["BAIXA"],
  "inicioHorario": "07:00:00",
  "finalHorario": "19:00:00",
  "obrigacaoAnexo": false,
  "descricao": "Paciente flexível - qualquer dia",
  "diasSemana": [],
  "intervaloRepetacao": 1
}
```

**Resultado**:
- Busca qualquer dia da semana
- Toda semana
- Sem restrições de dias

---

### Caso 4: Intervalo Mensal (4 semanas)
**Cenário**: Paciente que tem acompanhamento mensal na segunda

```json
{
  "idsVeiculos": ["12345678-1234-1234-1234-123456789012"],
  "idsCidades": ["98765432-9876-9876-9876-987654321098"],
  "prioridades": ["MODERADA"],
  "inicioHorario": "10:00:00",
  "finalHorario": "16:00:00",
  "obrigacaoAnexo": false,
  "descricao": "Acompanhamento mensal",
  "diasSemana": ["MONDAY"],
  "intervaloRepetacao": 4
}
```

**Resultado**:
- Busca APENAS segunda-feira
- A cada 4 semanas (mensal)
- Próximas datas: 1ª seg, skip 2-4 sem, 5ª seg (próximo mês)

---

## 📋 Mapeamento de Dias

```
"MONDAY"      → Segunda-feira
"TUESDAY"     → Terça-feira
"WEDNESDAY"   → Quarta-feira
"THURSDAY"    → Quinta-feira
"FRIDAY"      → Sexta-feira
"SATURDAY"    → Sábado
"SUNDAY"      → Domingo
```

---

## ⏳ Mapeamento de Intervalos

```
intervaloRepetacao = 1  → Toda semana
intervaloRepetacao = 2  → A cada 2 semanas (quinzenal)
intervaloRepetacao = 3  → A cada 3 semanas
intervaloRepetacao = 4  → A cada 4 semanas (mensal)
...
intervaloRepetacao = 52 → A cada ano
```

**Validação**: Deve estar entre 1 e 52

---

## 🔄 Exemplo de Atualização

```bash
PUT /api/lista/abcd1234-efgh5678-ijkl9012-mnop3456

{
  "nome": "Lista de Oncologia",
  "regrasCadastro": [
    {
      "id": "regra-uuid-aqui",  // ID da regra existente
      "idsVeiculos": ["12345678-1234-1234-1234-123456789012"],
      "idsCidades": ["98765432-9876-9876-9876-987654321098"],
      "prioridades": ["ALTA"],
      "inicioHorario": "08:00:00",
      "finalHorario": "18:00:00",
      "obrigacaoAnexo": false,
      "descricao": "Oncológicos - atualizado",
      "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
      "intervaloRepetacao": 1
    }
  ],
  "regrasSeparacao": [
    {
      "id": "regra-sep-uuid",
      "idTipoVeiculo": "tipo-veiculo-uuid",
      "tempoToleranciaSegundos": 300,
      "tempoToleranciaNanos": 0
    }
  ]
}
```

---

## 📊 Resposta ao Consultar Regra

```json
{
  "id": "lista-uuid",
  "nome": "Lista de Oncologia",
  "descricao": "Regras para pacientes oncológicos",
  "regrasCadastro": [
    {
      "id": "regra-uuid",
      "idsVeiculos": ["12345678-1234-1234-1234-123456789012"],
      "idsCidades": ["98765432-9876-9876-9876-987654321098"],
      "prioridades": ["ALTA"],
      "inicioHorario": "08:00",
      "finalHorario": "18:00",
      "obrigacaoAnexo": false,
      "descricao": "Oncológicos - atendimento 3x/semana",
      "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
      "intervaloRepetacao": 1
    }
  ],
  "regrasSeparacao": [...]
}
```

---

## 🎓 Lógica de Verificação

Quando um agendamento é criado, a regra verifica:

```
1. ✅ Horário está dentro do intervalo de horários?
2. ✅ Dia da semana está na lista de dias permitidos? (ou lista vazia = sim)
3. ✅ Prioridade do paciente está na lista?
4. ✅ Veículo está permitido?
5. ✅ Cidade está permitida? (ou lista vazia = sim)
6. ✅ Anexo obrigatório está presente?

Se TODAS as verificações passarem → Regra se aplica
Senão → Regra não se aplica
```

### Exemplo
```
Regra: 
  - Dias: MONDAY, WEDNESDAY, FRIDAY
  - Intervalo: 1 semana
  
Agendamento para:
  - 2026-11-21 (Sexta)
  - Prioridade: ALTA
  - Veículo: Permitido
  - Cidade: Permitida
  
Resultado: ✅ Regra se aplica (sexta está na lista)

Agendamento para:
  - 2026-11-24 (Segunda)
  - Prioridade: ALTA
  - Veículo: Permitido
  - Cidade: Permitida
  
Resultado: ✅ Regra se aplica (segunda está na lista)

Agendamento para:
  - 2026-11-25 (Terça)
  - Prioridade: ALTA
  - Veículo: Permitido
  - Cidade: Permitida
  
Resultado: ❌ Regra NÃO se aplica (terça NÃO está na lista)
```

---

## 🔧 Troubleshooting

### Problema: "dias_semana" vazio mas espero que se aplique
**Solução**: Dias vazios significa que a regra se aplica a **qualquer dia**. Isso é correto!

### Problema: Intervalo não está funcionando
**Solução**: O intervalo é armazenado em semanas. Verifique o cálculo de datas no sistema de agendamento.

### Problema: Rejeição de valor de intervalo
**Erro**: "O intervalo de repetição deve estar entre 1 e 52 semanas"
**Solução**: Use valores entre 1 e 52. Semanas negativas ou zero não são permitidas.

### Problema: Dias da semana em português
**Solução**: Use os enum em INGLÊS (MONDAY, TUESDAY, etc). Se precisar exibir em português, faça a tradução no frontend.

---

## 🌍 Tradução de Dias

Se quiser exibir ao usuário em português:

```
MONDAY      → Segunda-feira
TUESDAY     → Terça-feira
WEDNESDAY   → Quarta-feira
THURSDAY    → Quinta-feira
FRIDAY      → Sexta-feira
SATURDAY    → Sábado
SUNDAY      → Domingo
```

---

## 📝 Notas

✅ **Dias são opcionais**: Não precisa informar se quiser aplicar a qualquer dia
✅ **Intervalo tem padrão**: Se não informar, usa 1 (toda semana)
✅ **Sem breaking changes**: APIs antigas continuam funcionando
✅ **Validation automática**: Valores inválidos são rejeitados no banco

---

**Implementação concluída!** Use estes exemplos como referência para suas regras de agendamento.
