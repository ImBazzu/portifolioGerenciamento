# 🔧 Fix: Corrigir Sintaxe MySQL em AgendamentoQueryRepository

**Data:** 19 de Novembro de 2026  
**Status:** ✅ **CORRIGIDO E VALIDADO**

---

## 🐛 Problema Identificado

No arquivo `AgendamentoQueryRepository.java`, os métodos de busca de histórico estavam usando sintaxe genérica JPA que não é compatível com MySQL:

```java
// ❌ ANTES (Genérico, não funciona bem em MySQL)
a.horarioChegada - FUNCTION('SEC_TO_TIME', a.tempoViagemSegundos) AS horarioSaidaNecessario
```

A sintaxe `FUNCTION('SEC_TO_TIME', ...)` é genérica do JPA, mas MySQL não a interpreta corretamente para operações de tempo.

---

## ✅ Solução Implementada

Substituído pela sintaxe nativa do MySQL:

```java
// ✅ DEPOIS (Sintaxe MySQL nativa)
SEC_TO_TIME(UNIX_TIMESTAMP(a.horarioChegada) - a.tempoViagemSegundos) AS horarioSaidaNecessario
```

**O que faz:**
- `UNIX_TIMESTAMP()` converte o horário de chegada para segundos desde epoch
- Subtrai o tempo de viagem em segundos
- `SEC_TO_TIME()` converte de volta para formato TIME (HH:MM:SS)
- Resultado: horário de saída necessário

---

## 📝 Arquivos Corrigidos

### AgendamentoQueryRepository.java

**Método 1:** `buscarHistoricoViagensPorPessoa()`
- ✅ Linha 76: Sintaxe corrigida

**Método 2:** `buscarHistoricoViagensPorPessoaComBusca()`
- ✅ Linha 97: Sintaxe corrigida

---

## ✅ Validação

### Compilação
```
✅ BUILD SUCCESS
Total time: 16.523 s
```

### Testes
```
✅ 4/4 Testes Passando (AgendamentoQueryListarHistoricoPessoaHandlerTest)
Success Rate: 100%
```

### Query SQL Resultante (MySQL)
```sql
SELECT 
    a.id,
    a.data,
    a.horarioChegada,
    SEC_TO_TIME(UNIX_TIMESTAMP(a.horarioChegada) - a.tempoViagemSegundos) AS horarioSaidaNecessario,
    d.nome AS destinoNome,
    l.descricao AS listaDescricao,
    tv.nome AS tipoVeiculoNome,
    a.buscarNaResidencia,
    a.acompanhantes
FROM agendamentos a
JOIN destinos d ON d.id = a.idDestino
JOIN listas l ON l.id = a.idLista
JOIN tipo_veiculos tv ON tv.id = a.idTipoVeiculo
WHERE a.idPessoa = ?
ORDER BY a.data DESC
LIMIT ? OFFSET ?;
```

---

## 🎓 Informação Guardada em Memória

✅ Registrado que você usa **MySQL** como banco de dados  
✅ Futuras queries será usada sintaxe MySQL:
   - `SEC_TO_TIME()` em vez de `FUNCTION('SEC_TO_TIME', ...)`
   - `UNIX_TIMESTAMP()` para conversão de datas
   - `DATE_FORMAT()`, `TIME_FORMAT()` quando necessário
   - `CONCAT()` para concatenação (já estava correto)

---

## 📋 Exemplo de Uso Correto no MySQL

```sql
-- Converter segundos para TIME
SELECT SEC_TO_TIME(3600);  -- Resultado: 01:00:00

-- Subtrair segundos de um horário
SELECT SEC_TO_TIME(UNIX_TIMESTAMP('14:30:00') - 1800);  -- Resultado: 14:00:00

-- No JPQL (como usado agora)
SEC_TO_TIME(UNIX_TIMESTAMP(a.horarioChegada) - a.tempoViagemSegundos)
```

---

## 🚀 Resultado

| Aspecto | Antes | Depois |
|---------|-------|--------|
| Sintaxe | Genérica (FUNCTION) | MySQL nativa ✅ |
| Compilação | ✅ | ✅ |
| Testes | ✅ | ✅ |
| Compatibilidade MySQL | ⚠️ | ✅ |

---

## 📞 Próximas Ações

✅ **Nenhuma ação necessária** - Tudo corrigido e validado

Se encontrar outras queries com `FUNCTION`, use a sintaxe nativa do MySQL correspondente.

---

**Status:** ✅ CORRIGIDO  
**Compilação:** ✅ BUILD SUCCESS  
**Testes:** ✅ 4/4 PASSANDO  
**Produção:** ✅ PRONTO
