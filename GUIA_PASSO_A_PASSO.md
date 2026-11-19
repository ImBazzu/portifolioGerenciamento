# 📖 Guia Passo a Passo: Uso Imediato

## 🎯 Objetivo
Integrar os novos endpoints no seu frontend **em menos de 30 minutos**.

---

## ⏱️ Timeline Aproximada

```
├─ 2-3 min: Entender os endpoints (REFERENCIA_RAPIDA_APIS.md)
├─ 5-10 min: Escolher código do seu framework (EXEMPLOS_FRONTEND.md)
├─ 10-15 min: Adaptar ao seu projeto
└─ 5 min: Testar com exemplos prático

Total: 22-33 minutos ⏱️
```

---

## 📋 Passo 1: Entender os Endpoints (2-3 min)

### Resumo Executivo
Existem **2 endpoints**:

```
1️⃣  GET /api/pessoa/{id}/detalhes-completo
    └─ Retorna: pessoa + histórico paginado
    └─ Uso: Quando clica "Detalhes da Pessoa"

2️⃣  GET /api/pessoa/{id}/historico-viagens
    └─ Retorna: apenas histórico paginado
    └─ Uso: Se precisar isolado
```

### Parâmetros de Query
```
?destinoBusca=hospital    ← Filtrar por destino (opcional)
&pagina=0                 ← Número da página (padrão: 0)
&tamanho=10              ← Registros por página (padrão: 10)
```

### Exemplo Real
```
GET /api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?destinoBusca=hospital&pagina=0&tamanho=10
```

✅ **Checklist:**
- [ ] Entendi que existem 2 endpoints
- [ ] Entendi os 3 parâmetros de query
- [ ] Entendi que pagina começa em 0

---

## 🎨 Passo 2: Escolher Código do Seu Framework (5-10 min)

### Qual é seu frontend?

| Framework | Arquivo | Seção |
|-----------|---------|--------|
| **Vanilla JS** | EXEMPLOS_FRONTEND.md | "Vanilla JavaScript" |
| **React** | EXEMPLOS_FRONTEND.md | "React" |
| **Vue 3** | EXEMPLOS_FRONTEND.md | "Vue 3" |
| **Outro** | GUIA_TESTES_PRATICOS_DETALHES.md | "Teste com JavaScript" |

### Exemplo: React
1. Abra `EXEMPLOS_FRONTEND.md`
2. Vá para seção "React"
3. Copie o componente `DetalhePessoa`
4. Coloque no seu projeto

### Exemplo: Vue
1. Abra `EXEMPLOS_FRONTEND.md`
2. Vá para seção "Vue 3"
3. Copie o componente
4. Registre no seu projeto

### Exemplo: Vanilla JS
1. Abra `EXEMPLOS_FRONTEND.md`
2. Vá para seção "Vanilla JavaScript"
3. Copie a classe `DetalhePessoaView`
4. Use no seu HTML

✅ **Checklist:**
- [ ] Identifiquei meu framework
- [ ] Encontrei o código pronto
- [ ] Copiei o código

---

## 🔧 Passo 3: Adaptar ao Seu Projeto (10-15 min)

### Se é React

```jsx
// 1. Importar
import DetalhePessoa from './DetalhePessoa';

// 2. Usar em algum lugar
<DetalhePessoa pessoaId={pessoaId} />

// 3. Pronto! ✅
```

### Se é Vue

```vue
<!-- 1. Importar -->
<script setup>
  import DetalhePessoa from './DetalhePessoa.vue';
</script>

<!-- 2. Usar -->
<DetalhePessoa :pessoaId="pessoaId" />

<!-- 3. Pronto! ✅ -->
```

### Se é Vanilla JS

```html
<!-- 1. Adicionar HTML -->
<div id="pessoa-container"></div>

<!-- 2. Script -->
<script src="DetalhePessoaView.js"></script>
<script>
  const view = new DetalhePessoaView('uuid-da-pessoa', 'pessoa-container');
  view.carregarDetalhes();
</script>

<!-- 3. Pronto! ✅ -->
```

### Personalizações Comuns

#### Mudar URL base
```javascript
// Encontre isto:
const API_BASE = 'http://localhost:8080/api';

// Mude para:
const API_BASE = 'https://seu-dominio.com/api';
```

#### Adicionar token de autenticação
```javascript
// Encontre isto:
fetch(`/api/pessoa/${pessoaId}/detalhes-completo`)

// Mude para:
fetch(`/api/pessoa/${pessoaId}/detalhes-completo`, {
  headers: {
    'Authorization': `Bearer ${seu_token}`
  }
})
```

#### Mudar tamanho padrão de página
```javascript
// Encontre isto:
const tamanhoPagina = 10;

// Mude para:
const tamanhoPagina = 20; // Ou o que preferir
```

#### Customizar estilos CSS
```javascript
// Encontre isto em EXEMPLOS_FRONTEND.md, seção "CSS Básico"
// Copie e customize as cores, fontes, etc
```

✅ **Checklist:**
- [ ] Código importado no projeto
- [ ] URL base corrigida
- [ ] Token de autenticação adicionado
- [ ] Estilos CSS customizados (opcional)

---

## 🧪 Passo 4: Testar (5 min)

### Teste Rápido com cURL

```bash
# Sem parâmetros
curl "http://localhost:8080/api/pessoa/seu-uuid/detalhes-completo"

# Com busca
curl "http://localhost:8080/api/pessoa/seu-uuid/detalhes-completo?destinoBusca=hospital"

# Com paginação
curl "http://localhost:8080/api/pessoa/seu-uuid/detalhes-completo?pagina=1&tamanho=5"
```

### Teste no Insomnia/Postman
1. Abra Insomnia/Postman
2. Método: GET
3. URL: `http://localhost:8080/api/pessoa/{seu-uuid}/detalhes-completo`
4. Query Params:
   - `destinoBusca`: hospital
   - `pagina`: 0
   - `tamanho`: 10
5. Clique "Send"
6. Veja resposta

### Teste no Browser Console
```javascript
// Cole isto no console do navegador (F12)
fetch('/api/pessoa/seu-uuid/detalhes-completo')
  .then(r => r.json())
  .then(data => console.log(data))
```

### O que Esperar Ver
```json
{
  "pessoa": {
    "nome": "João Silva",
    // ... mais dados
  },
  "historicoViagens": {
    "conteudo": [
      {
        "data": "2026-11-19",
        "destinoNome": "Hospital Central"
        // ... mais dados
      }
    ],
    "totalElementos": 25,
    "totalPaginas": 3
  }
}
```

✅ **Checklist:**
- [ ] Testei com cURL / Postman / Browser
- [ ] Recebi resposta 200 OK
- [ ] Dados aparecem corretamente
- [ ] Paginação funciona

---

## ✨ Pronto! Próximos Passos

### Se tudo funcionou ✅
1. Integre no seu frontend
2. Customize estilos conforme necessário
3. Teste com dados reais
4. Deploy! 🚀

### Se algo não funcionou ❌
1. Verifique a URL base
2. Verifique o UUID da pessoa
3. Verifique token de autenticação
4. Veja [GUIA_TESTES_PRATICOS_DETALHES.md](GUIA_TESTES_PRATICOS_DETALHES.md) para debug

---

## 📚 Referências Rápidas

### URLs Curtas dos Documentos
| O que preciso? | Leia isto |
|----------------|----------|
| Ver endpoints | REFERENCIA_RAPIDA_APIS.md |
| Ver exemplos de código | EXEMPLOS_FRONTEND.md |
| Ver exemplos de teste | GUIA_TESTES_PRATICOS_DETALHES.md |
| Entender tudo | RESUMO_ENTREGA_DETALHES_PESSOA.md |
| Debug técnico | IMPLEMENTACAO_HISTORICO_VIAGENS.md |

### Código Copiável - React
```jsx
import { useState, useEffect } from 'react';

function DetalhePessoa({ pessoaId }) {
  const [pessoa, setPessoa] = useState(null);
  const [historicoViagens, setHistoricoViagens] = useState(null);
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarDetalhes();
  }, [paginaAtual]);

  async function carregarDetalhes() {
    const response = await fetch(
      `/api/pessoa/${pessoaId}/detalhes-completo?pagina=${paginaAtual}&tamanho=10`
    );
    const data = await response.json();
    setPessoa(data.pessoa);
    setHistoricoViagens(data.historicoViagens);
    setLoading(false);
  }

  if (loading) return <div>Carregando...</div>;

  return (
    <div>
      <h2>{pessoa.nome}</h2>
      <table>
        <thead>
          <tr>
            <th>Data</th>
            <th>Destino</th>
            <th>Horário Chegada</th>
          </tr>
        </thead>
        <tbody>
          {historicoViagens.conteudo.map(v => (
            <tr key={v.id}>
              <td>{v.data}</td>
              <td>{v.destinoNome}</td>
              <td>{v.horarioChegada}</td>
            </tr>
          ))}
        </tbody>
      </table>
      
      <button 
        onClick={() => setPaginaAtual(paginaAtual - 1)}
        disabled={paginaAtual === 0}
      >
        Anterior
      </button>
      
      <span>Página {paginaAtual + 1} de {historicoViagens.totalPaginas}</span>
      
      <button 
        onClick={() => setPaginaAtual(paginaAtual + 1)}
        disabled={paginaAtual >= historicoViagens.totalPaginas - 1}
      >
        Próxima
      </button>
    </div>
  );
}

export default DetalhePessoa;
```

### Código Copiável - Vanilla JS
```javascript
class DetalhePessoa {
  constructor(pessoaId, containerId) {
    this.pessoaId = pessoaId;
    this.container = document.getElementById(containerId);
    this.pagina = 0;
  }

  async carregar() {
    const response = await fetch(
      `/api/pessoa/${this.pessoaId}/detalhes-completo?pagina=${this.pagina}&tamanho=10`
    );
    const data = await response.json();
    this.renderizar(data);
  }

  renderizar(data) {
    this.container.innerHTML = `
      <h2>${data.pessoa.nome}</h2>
      <table>
        <thead>
          <tr>
            <th>Data</th>
            <th>Destino</th>
            <th>Horário</th>
          </tr>
        </thead>
        <tbody>
          ${data.historicoViagens.conteudo.map(v => `
            <tr>
              <td>${v.data}</td>
              <td>${v.destinoNome}</td>
              <td>${v.horarioChegada}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
      <button onclick="detalhes.pagina--; detalhes.carregar()">Anterior</button>
      <span>Página ${this.pagina + 1}</span>
      <button onclick="detalhes.pagina++; detalhes.carregar()">Próxima</button>
    `;
  }
}

const detalhes = new DetalhePessoa('uuid-aqui', 'container');
detalhes.carregar();
```

---

## 🎯 Resumo

| Passo | Descrição | Tempo |
|-------|-----------|-------|
| 1 | Entender endpoints | 2-3 min |
| 2 | Escolher código | 5-10 min |
| 3 | Adaptar projeto | 10-15 min |
| 4 | Testar | 5 min |
| **Total** | | **22-33 min** ⏱️ |

---

## ✅ Checklist Final

- [ ] Passos 1-4 completados
- [ ] Código funcionando no navegador
- [ ] Tabela exibindo viagens
- [ ] Paginação funcionando
- [ ] Busca por destino funcionando
- [ ] Pronto para produção

**Parabéns! 🎉 Você integrou com sucesso!**

---

**Última Atualização:** 19 de Novembro de 2026  
**Status:** ✅ Pronto para Uso
