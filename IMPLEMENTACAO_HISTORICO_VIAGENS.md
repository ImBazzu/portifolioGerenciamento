# Refatoração e Implementação: Histórico de Viagens de Pessoas

## Visão Geral

Foi implementada uma nova funcionalidade que permite visualizar os detalhes de uma pessoa com seu histórico completo de viagens. Quando o frontend clica em "detalhes da pessoa", agora é possível:

1. Visualizar todas as informações do passageiro
2. Ver uma tabela paginada do histórico de viagens
3. Buscar/filtrar viagens por destino

## Endpoints Implementados

### 1. Detalhes Completo da Pessoa com Histórico
**GET** `/pessoa/{id}/detalhes-completo`

Retorna a pessoa com seu histórico de viagens em uma única chamada.

**Parâmetros de Query:**
- `destinoBusca` (string, opcional): Buscar viagens por destino (default: "")
- `pagina` (int, opcional): Número da página (default: 0)
- `tamanho` (int, opcional): Tamanho da página (default: 10)

**Resposta (200 OK):**
```json
{
  "pessoa": {
    "id": "uuid",
    "nome": "João Silva",
    "observacao": "Cliente especial",
    "especificacaoId": "uuid",
    "especificacaoNome": "Cadeirante",
    "prioridadeNome": "Alta",
    "prioridadeId": "uuid",
    "buscarNaResidenciaPrioridade": true,
    "quantidadeAcompanhantesPrioridade": 2,
    "buscarNaResidencia": true,
    "quantidadeAcompanhantes": 1,
    "dataArquivado": null,
    "endereco": "Rua Principal, 123",
    "cpf": "12345678900",
    "telefone": "11999999999"
  },
  "historicoViagens": {
    "conteudo": [
      {
        "id": "uuid",
        "data": "2026-11-19",
        "horarioChegada": "14:30:00",
        "horarioSaidaNecessario": "13:30:00",
        "destinoNome": "Hospital Central",
        "listaDescricao": "Transporte Especial",
        "tipoVeiculoNome": "Van Adaptada",
        "buscarNaResidencia": true,
        "acompanhantes": 1
      }
    ],
    "paginaAtual": 0,
    "tamanhoPagina": 10,
    "totalElementos": 25,
    "totalPaginas": 3
  }
}
```

### 2. Histórico de Viagens (Apenas)
**GET** `/pessoa/{id}/historico-viagens`

Retorna apenas o histórico de viagens com paginação.

**Parâmetros de Query:**
- `destinoBusca` (string, opcional): Filtrar por destino (default: "")
- `pagina` (int, opcional): Número da página (default: 0)
- `tamanho` (int, opcional): Tamanho da página (default: 10)

**Resposta (200 OK):**
```json
{
  "conteudo": [
    {
      "id": "uuid",
      "data": "2026-11-19",
      "horarioChegada": "14:30:00",
      "horarioSaidaNecessario": "13:30:00",
      "destinoNome": "Hospital Central",
      "listaDescricao": "Transporte Especial",
      "tipoVeiculoNome": "Van Adaptada",
      "buscarNaResidencia": true,
      "acompanhantes": 1
    }
  ],
  "paginaAtual": 0,
  "tamanhoPagina": 10,
  "totalElementos": 25,
  "totalPaginas": 3
}
```

## Arquivos Criados/Modificados

### Arquivos Criados:

1. **HistoricoViagemResponseDto.java**
   - DTO que representa uma viagem no histórico
   - Contém informações de data, horário, destino, tipo veículo, etc.

2. **HistoricoViagemProjecao.java**
   - Interface de projeção JPA para mapear resultados da query

3. **AgendamentoQueryListarHistoricoPessoa.java**
   - Query object para encapsular parâmetros de busca
   - Suporta filtro por destino e paginação

4. **AgendamentoQueryListarHistoricoPessoaHandler.java**
   - Handler que processa a query e retorna resultado paginado
   - Mapeia projeções para DTOs

5. **PessoaDetalhesResponseDto.java**
   - DTO com detalhes completos da pessoa
   - Inclui informações de especificação e prioridade

6. **PessoaComHistoricoResponseDto.java**
   - DTO que encapsula pessoa + histórico de viagens

### Arquivos Modificados:

1. **AgendamentoQueryRepository.java**
   - Alterado para estender `JpaRepository` ao invés de `Repository`
   - Adicionados 2 novos métodos para buscar histórico:
     - `buscarHistoricoViagensPorPessoa()` - sem filtro
     - `buscarHistoricoViagensPorPessoaComBusca()` - com filtro por destino

2. **PessoaController.java**
   - Novo endpoint: `GET /{id}/detalhes-completo`
   - Novo endpoint: `GET /{id}/historico-viagens`
   - Novo método helper: `toResponseDetalhes()`
   - Injeção do `AgendamentoQueryListarHistoricoPessoaHandler`

## Fluxo de Dados

```
Frontend (clica em detalhes)
    ↓
GET /pessoa/{id}/detalhes-completo?destinoBusca=...&pagina=0&tamanho=10
    ↓
PessoaController.buscarDetalhesCompleto()
    ↓
1. PessoaQueryBuscar → Carrega dados da pessoa
2. AgendamentoQueryListarHistoricoPessoa → Busca histórico paginado
    ↓
AgendamentoQueryRepository.buscarHistoricoViagensPorPessoa()
    ↓
Banco de dados (JPQL query com JOINs)
    ↓
HistoricoViagemProjecao (mapeado)
    ↓
HistoricoViagemResponseDto (convertido)
    ↓
PessoaComHistoricoResponseDto (retornado)
    ↓
Frontend (exibe pessoa + tabela paginada)
```

## Recursos de Busca e Paginação

### Busca por Destino
- Implementada com `LIKE` no SQL (case-insensitive parcial)
- Exemplo: `/pessoa/{id}/detalhes-completo?destinoBusca=hospital`
- Retorna todas as viagens para destinos contendo "hospital"

### Paginação
- Implementada com Spring Data's `PageRequest`
- Parâmetros: `pagina` (zero-indexed) e `tamanho`
- Resposta inclui: `totalElementos` e `totalPaginas` para facilitar paginação no frontend
- Ordenação: viagens mais recentes primeiro (`ORDER BY a.data DESC`)

## Exemplo de Uso no Frontend

```javascript
// Buscar detalhes com histórico
fetch('/api/pessoa/uuid-123/detalhes-completo?pagina=0&tamanho=10')
  .then(r => r.json())
  .then(data => {
    console.log('Pessoa:', data.pessoa);
    console.log('Viagens:', data.historicoViagens.conteudo);
    console.log('Total:', data.historicoViagens.totalElementos);
  });

// Buscar com filtro
fetch('/api/pessoa/uuid-123/detalhes-completo?destinoBusca=hospital&pagina=0&tamanho=10')
  .then(r => r.json())
  .then(data => {
    // Viagens filtradas para "Hospital"
  });
```

## Consulta SQL Executada

```sql
SELECT 
    a.id,
    a.data,
    a.horarioChegada,
    (a.horarioChegada - SEC_TO_TIME(a.tempoViagemSegundos)) AS horarioSaidaNecessario,
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
  AND d.nome LIKE CONCAT('%', ?, '%')  -- opcional, apenas com filtro
ORDER BY a.data DESC
LIMIT ? OFFSET ?
```

## Benefícios da Implementação

✅ **Separação de Responsabilidades**
- Query, Handler e Repository separados
- Fácil de testar e manter

✅ **Paginação Eficiente**
- Consulta ao banco com LIMIT/OFFSET
- Não carrega todas as viagens na memória

✅ **Busca Flexível**
- Pode buscar por destino
- Suporta queries complexas no futuro

✅ **Performance**
- JOINs otimizados
- Índices em foreign keys

✅ **API Limpa**
- 2 endpoints com propósitos claros
- Respostas estruturadas e previsíveis

## Testes Recomendados

1. **Buscar detalhes de pessoa com histórico vazio**
2. **Buscar com filtro de destino válido**
3. **Buscar com filtro de destino inválido**
4. **Paginação com múltiplas páginas**
5. **Ordem decrescente de datas (mais recentes primeiro)**

## Próximas Melhorias (Sugestões)

- [ ] Adicionar filtro por data (data início/fim)
- [ ] Adicionar filtro por tipo de veículo
- [ ] Adicionar filtro por lista
- [ ] Cache de histórico para pessoas frequentes
- [ ] Relatório de viagens (exportar CSV/PDF)
- [ ] Análise de padrões de viagem
