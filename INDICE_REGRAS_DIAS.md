# 🚀 INÍCIO RÁPIDO: Dias da Semana em Regras de Agendamento

## 1️⃣ Comece Aqui (5 minutos)

Leia primeiro: **[RESUMO_REGRAS_DIAS_INTERVALO.md](./RESUMO_REGRAS_DIAS_INTERVALO.md)**

---

## 2️⃣ Exemplos Práticos (10 minutos)

Consulte: **[GUIA_DIAS_INTERVALO.md](./GUIA_DIAS_INTERVALO.md)**

Aprenda como:
- Criar regra com dias específicos
- Usar intervalo de repetição
- Atualizar regras existentes
- Validar dados

---

## 3️⃣ Detalhes Técnicos (20 minutos)

Consulte: **[IMPLEMENTACAO_REGRAS_AGENDAMENTO.md](./IMPLEMENTACAO_REGRAS_AGENDAMENTO.md)**

Veja:
- Arquitetura implementada
- Arquivos modificados
- Validações
- Fluxo de dados

---

## 4️⃣ Changelog (5 minutos)

Consulte: **[CHANGELOG_REGRAS_DIAS.md](./CHANGELOG_REGRAS_DIAS.md)**

Revise:
- O que foi adicionado
- O que foi alterado
- Exemplo de uso
- Arquivos modificados

---

## 🎯 Uso Rápido

### Criar regra para seg/qua/sex cada semana:
```json
{
  "diasSemana": ["MONDAY", "WEDNESDAY", "FRIDAY"],
  "intervaloRepetacao": 1
}
```

### Criar regra para terça a cada 2 semanas:
```json
{
  "diasSemana": ["TUESDAY"],
  "intervaloRepetacao": 2
}
```

### Sem restrição de dias:
```json
{
  "diasSemana": [],
  "intervaloRepetacao": 1
}
```

---

## 🧪 Teste em 3 Passos

```bash
# 1. Compilar
mvn clean compile

# 2. Rodar
mvn spring-boot:run

# 3. Testar endpoint
curl -X POST http://localhost:8080/api/lista \
  -H "Content-Type: application/json" \
  -d '{"nome": "Teste", "regrasCadastro": [...]}'
```

---

## 📚 Índice Completo

| Documento | Tempo | Propósito |
|---|---|---|
| [RESUMO_REGRAS_DIAS_INTERVALO.md](./RESUMO_REGRAS_DIAS_INTERVALO.md) | 5 min | Visão geral completa |
| [GUIA_DIAS_INTERVALO.md](./GUIA_DIAS_INTERVALO.md) | 10 min | Exemplos práticos |
| [IMPLEMENTACAO_REGRAS_AGENDAMENTO.md](./IMPLEMENTACAO_REGRAS_AGENDAMENTO.md) | 20 min | Detalhes técnicos |
| [CHANGELOG_REGRAS_DIAS.md](./CHANGELOG_REGRAS_DIAS.md) | 5 min | Histórico de mudanças |

---

## ✅ Validação Rápida

```
✓ Código compila?             mvn clean compile
✓ Endpoints funcionam?        curl localhost:8080/api/lista
✓ Banco correto?              SELECT * FROM regra_cadastro_dias_semana
✓ Lógica de dias?             Verificar agendamento com dia específico
```

---

## 🆘 Problemas Comuns

| Problema | Solução |
|---|---|
| Intervalo rejeitado | Use 1-52 |
| Dias não funcionam | Verifique DayOfWeek (MONDAY, etc) |
| Tabela não criada | Execute migração V17 |
| Compilação falha | Verifique imports de DayOfWeek |

---

## 🎓 Aprenda Mais

**Se quer entender a arquitetura:**
→ Leia [IMPLEMENTACAO_REGRAS_AGENDAMENTO.md](./IMPLEMENTACAO_REGRAS_AGENDAMENTO.md)

**Se quer ver casos de uso:**
→ Leia [GUIA_DIAS_INTERVALO.md](./GUIA_DIAS_INTERVALO.md)

**Se quer revisar mudanças:**
→ Leia [CHANGELOG_REGRAS_DIAS.md](./CHANGELOG_REGRAS_DIAS.md)

---

## 🔗 Links Úteis

- 📄 Especificação de dias: Enum Java `DayOfWeek`
- 📄 Intervalo: Integer 1-52 (semanas)
- 📄 Tabela: `regra_cadastro_dias_semana`
- 📄 Coluna: `intervalo_repetacao_semanas`

---

## ⏱️ Tempo Total de Leitura

- **Mínimo**: 5 minutos (apenas sumário)
- **Recomendado**: 20 minutos (sumário + guia prático)
- **Completo**: 40 minutos (tudo)

---

**🎉 Implementação Concluída!**

Pronto para começar? Leia o [RESUMO_REGRAS_DIAS_INTERVALO.md](./RESUMO_REGRAS_DIAS_INTERVALO.md) em 5 minutos.

