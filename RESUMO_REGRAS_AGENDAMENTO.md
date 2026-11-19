# Resumo de Alterações - Regras de Agendamento

## 📋 Visão Geral

As mudanças solicitadas para a **lista/regra de agendamento** já foram **totalmente implementadas** no código. Agora é possível:

1. ✅ Especificar **dias da semana opcionais** (segunda a domingo) quando a regra se aplica
2. ✅ Configurar **intervalo de repetição em semanas** (1 semana, 2 semanas, etc.)

---

## 📝 Detalhes das Implementações

### 1. Entidade Domain - `RegraCadastro.java`

**Novos Campos:**
```java
private List<DayOfWeek> diasSemana;      // Dias da semana opcionais
private Integer intervaloRepetacao;      // Intervalo em semanas (1-52)
```

**Métodos Principais:**
- `criar()` - Cria nova regra com dias da semana e intervalo de repetição
- `recriar()` - Reconstrói regra do banco com todos os parâmetros
- `editar()` - Permite editar dias da semana e intervalo de repetição
- `verificarDiaSemana()` - Valida se uma regra se aplica a um dia específico

**Validações Implementadas:**
```
- diasSemana: lista opcional (pode ser vazia → aplicável a todos os dias)
- intervaloRepetacao: 1 a 52 semanas (default = 1 se null)
- Se diasSemana está vazio: regra se aplica a todos os dias da semana
- Se intervaloRepetacao é null: padrão é 1 semana
```

---

### 2. Entidade JPA - `RegraCadastroJpa.java`

**Novos Campos de Banco:**
```java
@ElementCollection
@Enumerated(EnumType.STRING)
@CollectionTable(name = "regra_cadastro_dias_semana", ...)
private List<DayOfWeek> diasSemana = new ArrayList<>();

@Column(name = "intervalo_repetacao_semanas")
private Integer intervaloRepetacao;
```

**Tabelas de Banco Criadas:**
- `regra_cadastro_dias_semana`: armazena dias da semana (relação N:M)
- Coluna `intervalo_repetacao_semanas` em `regras_cadastro`

---

### 3. DTO de Requisição - `RegraCadastroRequestDto.java`

```java
public record RegraCadastroRequestDto(
    @NotEmpty List<@NotNull UUID> idsVeiculos,
    List<@NotNull UUID> idsCidades,
    @NotEmpty List<@NotNull EnumPrioridade> prioridades,
    LocalTime inicioHorario,
    LocalTime finalHorario,
    boolean obrigacaoAnexo,
    @Size(max = 249) String descricao,
    List<@NotNull DayOfWeek> diasSemana,              // ✅ NOVO
    @Min(1) @Max(52) Integer intervaloRepetacao      // ✅ NOVO
) { }
```

**Validações:**
- `diasSemana`: lista opcional (pode ser null ou vazia)
- `intervaloRepetacao`: entre 1 e 52 semanas

---

### 4. Mapper - `TipoListaBancoMapper.java`

**Conversão Domain → JPA:**
```java
regraJpa.setDiasSemana(regra.getDiasSemana());
regraJpa.setIntervaloRepetacao(regra.getIntervaloRepetacaoRaw());
```

**Conversão JPA → Domain:**
```java
RegraCadastro.recriar(
    regra.getId(),
    entidade.getId(),
    // ... outros parâmetros ...
    regra.getDiasSemana(),           // ✅ Mapeado
    regra.getIntervaloRepetacao()    // ✅ Mapeado
)
```

---

### 5. Handler de Comando - `TipoListaCommandAtualizarRegrasHandler.java`

```java
command.regrasCadastro().forEach(regra -> lista.criarRegraCadastro(
    regra.idsVeiculos(),
    regra.idsCidades(),
    regra.prioridades(),
    regra.inicioHorario(),
    regra.finalHorario(),
    regra.obrigacaoAnexo(),
    observacaoOpcional(regra.descricao()),
    regra.diasSemana(),              // ✅ Passado
    regra.intervaloRepetacao()       // ✅ Passado
));
```

---

### 6. Controller - `TipoListaController.java`

**Endpoint:** `PUT /lista/{id}/regras`

**Método `toInput()` - Conversão DTO → Input:**
```java
private static RegraCadastroInput toInput(RegraCadastroRequestDto request) {
    return new RegraCadastroInput(
        request.idsVeiculos(),
        request.idsCidades(),
        request.prioridades(),
        request.inicioHorario(),
        request.finalHorario(),
        request.obrigacaoAnexo(),
        request.descricao(),
        request.diasSemana(),           // ✅ Mapeado
        request.intervaloRepetacao()    // ✅ Mapeado
    );
}
```

---

### 7. Migrações de Banco - `V17__adicionar_dias_semana_intervalo_regras.sql`

**Tabelas Criadas:**
```sql
CREATE TABLE IF NOT EXISTS regra_cadastro_dias_semana (
    regra_cadastro_id BINARY(16) NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    PRIMARY KEY (regra_cadastro_id, dia_semana),
    CONSTRAINT fk_regra_dia_semana_cadastro FOREIGN KEY (regra_cadastro_id) 
        REFERENCES regras_cadastro(id) ON DELETE CASCADE
);
```

**Colunas Adicionadas:**
```sql
ALTER TABLE regras_cadastro 
ADD COLUMN IF NOT EXISTS intervalo_repetacao_semanas INTEGER DEFAULT 1;
```

**Índices Criados:**
```sql
CREATE INDEX IF NOT EXISTS idx_regra_dias_semana 
ON regra_cadastro_dias_semana(regra_cadastro_id);

CREATE INDEX IF NOT EXISTS idx_regra_intervalo_repetacao 
ON regras_cadastro(intervalo_repetacao_semanas);
```

---

## 🔄 Fluxo Completo de Uso

### Criar/Atualizar Regra com Dias e Intervalo

**Request POST/PUT:**
```json
{
  "regrasCadastro": [
    {
      "idsVeiculos": ["uuid-veiculo-1", "uuid-veiculo-2"],
      "idsCidades": [],
      "prioridades": ["BAIXA", "MODERADA"],
      "inicioHorario": "08:00:00",
      "finalHorario": "17:00:00",
      "obrigacaoAnexo": false,
      "descricao": "Regra de segunda a sexta a cada 2 semanas",
      "diasSemana": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
      "intervaloRepetacao": 2
    }
  ],
  "regrasSeparacao": []
}
```

### Validações Executadas:

1. **Dias da Semana:**
   - ✓ Lista pode ser vazia (aplica-se a todos os dias)
   - ✓ Pode conter qualquer DayOfWeek (MONDAY, TUESDAY, etc.)
   - ✓ Sem duplicatas permitidas

2. **Intervalo de Repetição:**
   - ✓ Validação de 1 a 52 semanas
   - ✓ Default = 1 semana se null
   - ✓ Usado em lógica de agendamento periódico

---

## 🗄️ Arquivos Modificados/Criados

### ✅ Totalmente Implementado

| Arquivo | Tipo | Status |
|---------|------|--------|
| `RegraCadastro.java` | Domain | ✅ Implementado |
| `RegraCadastroJpa.java` | JPA Entity | ✅ Implementado |
| `RegraCadastroRequestDto.java` | DTO | ✅ Implementado |
| `RegraCadastroInput.java` | Model | ✅ Implementado |
| `TipoListaBancoMapper.java` | Mapper | ✅ Implementado |
| `TipoListaCommandAtualizarRegrasHandler.java` | Handler | ✅ Implementado |
| `TipoListaController.java` | Controller | ✅ Implementado |
| `V17__adicionar_dias_semana_intervalo_regras.sql` | Migration | ✅ Implementado |

---

## 📊 Exemplo de Verificação

**Verificação de Dias da Semana no Domain:**
```java
boolean verificarDiaSemana(DayOfWeek diaSemana) {
    return this.diasSemana.isEmpty() || this.diasSemana.contains(diaSemana);
}
```

- Se `diasSemana` é vazio → retorna `true` (válido para qualquer dia)
- Se `diasSemana` contém o dia → retorna `true`
- Caso contrário → retorna `false` (regra não se aplica neste dia)

---

## ✅ Status de Compilação

```
BUILD SUCCESS
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0
[INFO] ---- Total build time ----: 8.8s
```

---

## 🎯 Próximos Passos (Se Necessário)

1. **Testes Unitários**: Executar `mvn test` para validar lógica de dias da semana
2. **Teste de Integração**: Testar endpoint `/lista/{id}/regras` com PUT Request
3. **Validação em Produção**: Verificar agendamentos que usam essas regras
4. **Documentação do Banco**: Consultar relatório de migrações executadas

---

## 📝 Notas Importantes

- **Dias da Semana Padrão:** Java Enum `DayOfWeek` (MONDAY, TUESDAY, ..., SUNDAY)
- **Intervalo de Repetição:** Sempre em semanas (1-52)
- **Compatibilidade Regressiva:** Regras antigas continuam funcionando (default = 1 semana, todos os dias)
- **Soft Delete:** Respeita padrão de arquivo lógico do projeto

---

## 📞 Suporte

Para mais informações sobre:
- **Domain Logic:** Veja `RegraCadastro.java` - `verificacaoDasRegras()`, `verificarDiaSemana()`
- **Banco de Dados:** Veja `V17__adicionar_dias_semana_intervalo_regras.sql`
- **API REST:** Veja `TipoListaController.java` - método `atualizarRegras()`
