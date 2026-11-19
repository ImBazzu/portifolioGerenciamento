# 📌 Referência Rápida: APIs de Detalhes de Pessoa

## 🚀 URLs dos Endpoints

### Detalhes Completo com Histórico
```
GET /api/pessoa/{id}/detalhes-completo
```

### Apenas Histórico
```
GET /api/pessoa/{id}/historico-viagens
```

---

## 📋 Parâmetros Query

| Parâmetro | Tipo | Descrição | Exemplo |
|-----------|------|-----------|---------|
| `destinoBusca` | string | Filtrar por destino | `?destinoBusca=hospital` |
| `pagina` | int | Página (zero-indexed) | `?pagina=0` |
| `tamanho` | int | Registros por página | `?tamanho=10` |

---

## 💡 Exemplos Rápidos

### JavaScript
```javascript
// Sem filtro
const response = await fetch('/api/pessoa/{id}/detalhes-completo');
const data = await response.json();

// Com filtro
const url = new URL('/api/pessoa/{id}/detalhes-completo', window.location.origin);
url.searchParams.append('destinoBusca', 'hospital');
url.searchParams.append('pagina', '0');
url.searchParams.append('tamanho', '10');
const response = await fetch(url);
```

### cURL
```bash
curl "http://localhost:8080/api/pessoa/uuid/detalhes-completo?destinoBusca=hospital&pagina=0&tamanho=10"
```

### Python
```python
import requests

url = "http://localhost:8080/api/pessoa/uuid/detalhes-completo"
params = {
    "destinoBusca": "hospital",
    "pagina": 0,
    "tamanho": 10
}
response = requests.get(url, params=params)
data = response.json()
```

---

## 📊 Resposta

### Estrutura
```json
{
  "pessoa": { ... },
  "historicoViagens": {
    "conteudo": [ ... ],
    "paginaAtual": 0,
    "tamanhoPagina": 10,
    "totalElementos": 25,
    "totalPaginas": 3
  }
}
```

### Campo Pessoa
```json
{
  "id": "uuid",
  "nome": "João Silva",
  "cpf": "12345678900",
  "telefone": "11999999999",
  "endereco": "...",
  "observacao": "...",
  "especificacaoId": "uuid",
  "especificacaoNome": "Cadeirante",
  "prioridadeId": "uuid",
  "prioridadeNome": "Alta",
  "buscarNaResidencia": true,
  "quantidadeAcompanhantes": 1,
  "dataArquivado": null
}
```

### Campo Viagem (item do histórico)
```json
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
```

---

## ⚡ Casos de Uso Comuns

### Carregar tudo na primeira vez
```javascript
const { pessoa, historicoViagens } = await (
  await fetch(`/api/pessoa/${id}/detalhes-completo`)
).json();
```

### Buscar por destino
```javascript
const url = `/api/pessoa/${id}/detalhes-completo?destinoBusca=${destino}`;
const { historicoViagens } = await (await fetch(url)).json();
```

### Ir para próxima página
```javascript
const { historicoViagens } = await (
  await fetch(`/api/pessoa/${id}/detalhes-completo?pagina=${pagina + 1}`)
).json();
```

### Mudar tamanho de página
```javascript
const { historicoViagens } = await (
  await fetch(`/api/pessoa/${id}/detalhes-completo?tamanho=20`)
).json();
```

---

## ✅ Validações Importantes

| Validação | Descrição |
|-----------|-----------|
| **Paginação** | Começa em 0, não em 1 |
| **Busca** | Case-insensitive, parcial |
| **Ordenação** | Mais recentes primeiro (DESC) |
| **Sem filtro** | Se `totalElementos` = 0, `conteudo` = `[]` |
| **Tipos** | Horários em formato HH:MM:SS, datas em YYYY-MM-DD |

---

## 🎯 Checklist de Uso

- [ ] Usar `/detalhes-completo` quando precisar de pessoa + histórico
- [ ] Usar `/historico-viagens` apenas quando precisar de histórico isolado
- [ ] Adicionar `?pagina=` e `?tamanho=` para controlar paginação
- [ ] Adicionar `?destinoBusca=` para filtrar viagens
- [ ] Tratar array vazio em `conteudo` (pessoa sem viagens)
- [ ] Usar `totalPaginas` para controlar botões de navegação

---

## 🐛 Erros Comuns

| Erro | Causa | Solução |
|------|-------|--------|
| 404 | Pessoa não existe | Verificar UUID |
| 400 | UUID inválido | Validar formato |
| 500 | Erro no servidor | Ver logs |
| Vazio | Sem viagens | Verificar se pessoa tem agendamentos |

---

## 📞 Arquivos de Referência

- **Implementação Detalhada:** `IMPLEMENTACAO_HISTORICO_VIAGENS.md`
- **Guia de Testes:** `GUIA_TESTES_PRATICOS_DETALHES.md`
- **Resumo Executivo:** `RESUMO_ENTREGA_DETALHES_PESSOA.md`

---

**Última Atualização:** 19/11/2026
