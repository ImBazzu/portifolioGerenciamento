# 🧪 Guia Prático de Testes: Detalhes de Pessoa com Histórico

## Teste com cURL

### 1. Teste Básico (Sem Filtro)

```bash
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer seu_token_aqui"
```

**Resposta esperada:** 200 OK com pessoa e histórico completo

---

### 2. Teste com Filtro de Destino

```bash
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?destinoBusca=hospital" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer seu_token_aqui"
```

**Resposta esperada:** 200 OK com apenas viagens para "Hospital..."

---

### 3. Teste com Paginação

```bash
# Primeira página (10 registros)
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?pagina=0&tamanho=10"

# Segunda página
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?pagina=1&tamanho=10"

# Apenas 5 registros por página
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?pagina=0&tamanho=5"
```

---

### 4. Teste Combinado (Filtro + Paginação)

```bash
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?destinoBusca=clinica&pagina=0&tamanho=10"
```

---

### 5. Teste do Endpoint Isolado (Apenas Histórico)

```bash
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/historico-viagens"
```

---

### 6. Teste com Pessoa Não Encontrada (Erro)

```bash
curl -X GET "http://localhost:8080/api/pessoa/00000000-0000-0000-0000-000000000000/detalhes-completo"
```

**Resposta esperada:** 404 Not Found

---

## Teste com JavaScript (Frontend)

### Setup Básico

```javascript
// Configurar base URL
const API_BASE = 'http://localhost:8080/api';
const PESSOA_ID = '550e8400-e29b-41d4-a716-446655440000';

// Token de autenticação (adicione o seu)
const TOKEN = 'seu_jwt_token_aqui';

const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${TOKEN}`
};
```

### Teste 1: Carregar Detalhes Simples

```javascript
async function teste1_carregarDetalhesSimples() {
  console.log('🧪 Teste 1: Carregar detalhes simples...');
  
  try {
    const response = await fetch(
      `${API_BASE}/pessoa/${PESSOA_ID}/detalhes-completo`,
      { headers }
    );
    
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    
    const data = await response.json();
    
    console.log('✅ Pessoa carregada:');
    console.log(`   Nome: ${data.pessoa.nome}`);
    console.log(`   Especificação: ${data.pessoa.especificacaoNome}`);
    console.log(`   Prioridade: ${data.pessoa.prioridadeNome}`);
    
    console.log('✅ Histórico carregado:');
    console.log(`   Total de viagens: ${data.historicoViagens.totalElementos}`);
    console.log(`   Viagens na página: ${data.historicoViagens.conteudo.length}`);
    
  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}
```

### Teste 2: Buscar por Destino

```javascript
async function teste2_buscarPorDestino() {
  console.log('🧪 Teste 2: Buscar por destino...');
  
  const destino = 'hospital';
  
  try {
    const params = new URLSearchParams({
      destinoBusca: destino,
      pagina: 0,
      tamanho: 10
    });
    
    const response = await fetch(
      `${API_BASE}/pessoa/${PESSOA_ID}/detalhes-completo?${params}`,
      { headers }
    );
    
    const data = await response.json();
    
    console.log(`✅ Busca por "${destino}": ${data.historicoViagens.totalElementos} viagens encontradas`);
    
    data.historicoViagens.conteudo.forEach((viagem, idx) => {
      console.log(`   ${idx + 1}. ${viagem.data} → ${viagem.destinoNome}`);
    });
    
  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}
```

### Teste 3: Paginação

```javascript
async function teste3_paginacao() {
  console.log('🧪 Teste 3: Testar paginação...');
  
  try {
    for (let pagina = 0; pagina < 3; pagina++) {
      const params = new URLSearchParams({
        pagina: pagina,
        tamanho: 5  // Reduzir para testar paginação mais facilmente
      });
      
      const response = await fetch(
        `${API_BASE}/pessoa/${PESSOA_ID}/detalhes-completo?${params}`,
        { headers }
      );
      
      const data = await response.json();
      
      if (data.historicoViagens.conteudo.length === 0) {
        console.log(`   Página ${pagina + 1}: Sem registros (fim da paginação)`);
        break;
      }
      
      console.log(`✅ Página ${pagina + 1}:`);
      console.log(`   Total de páginas: ${data.historicoViagens.totalPaginas}`);
      console.log(`   Registros: ${data.historicoViagens.conteudo.length}`);
      console.log(`   Primeiro destino: ${data.historicoViagens.conteudo[0].destinoNome}`);
    }
    
  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}
```

### Teste 4: Verificar Estrutura de Dados

```javascript
async function teste4_verificarEstrutura() {
  console.log('🧪 Teste 4: Verificar estrutura de dados...');
  
  try {
    const response = await fetch(
      `${API_BASE}/pessoa/${PESSOA_ID}/detalhes-completo`,
      { headers }
    );
    
    const data = await response.json();
    
    // Verificar pessoa
    const camposPessoa = ['id', 'nome', 'cpf', 'telefone', 'especificacaoNome', 'prioridadeNome'];
    const pessoaOk = camposPessoa.every(campo => campo in data.pessoa);
    console.log(`✅ Estrutura de Pessoa: ${pessoaOk ? 'OK' : 'ERRO'}`);
    
    // Verificar histórico
    const camposHistorico = ['conteudo', 'paginaAtual', 'tamanhoPagina', 'totalElementos', 'totalPaginas'];
    const historicoOk = camposHistorico.every(campo => campo in data.historicoViagens);
    console.log(`✅ Estrutura de Histórico: ${historicoOk ? 'OK' : 'ERRO'}`);
    
    // Verificar primeira viagem
    if (data.historicoViagens.conteudo.length > 0) {
      const viagem = data.historicoViagens.conteudo[0];
      const camposViagem = ['id', 'data', 'horarioChegada', 'destinoNome', 'tipoVeiculoNome'];
      const viagemOk = camposViagem.every(campo => campo in viagem);
      console.log(`✅ Estrutura de Viagem: ${viagemOk ? 'OK' : 'ERRO'}`);
    }
    
  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}
```

### Teste 5: Performance (Medir tempo)

```javascript
async function teste5_performance() {
  console.log('🧪 Teste 5: Performance...');
  
  const inicio = performance.now();
  
  try {
    const response = await fetch(
      `${API_BASE}/pessoa/${PESSOA_ID}/detalhes-completo?tamanho=100`,
      { headers }
    );
    
    const data = await response.json();
    const tempo = performance.now() - inicio;
    
    console.log(`✅ Tempo total: ${tempo.toFixed(2)}ms`);
    console.log(`   Dados carregados: ${data.historicoViagens.totalElementos} viagens`);
    
    if (tempo > 1000) {
      console.warn('⚠️ Aviso: Requisição demorou mais de 1 segundo');
    }
    
  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}
```

### Teste 6: Teste Completo (Suite)

```javascript
async function executarTodosTestes() {
  console.log('🚀 Iniciando suite de testes...\n');
  
  await teste1_carregarDetalhesSimples();
  console.log('');
  
  await teste2_buscarPorDestino();
  console.log('');
  
  await teste3_paginacao();
  console.log('');
  
  await teste4_verificarEstrutura();
  console.log('');
  
  await teste5_performance();
  console.log('');
  
  console.log('✅ Suite de testes concluída!');
}

// Executar
executarTodosTestes();
```

---

## Teste com Insomnia/Postman

### Configuração

1. **Criar nova Request**
   - **Nome:** Detalhes Pessoa
   - **Método:** GET
   - **URL:** `http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo`

2. **Headers**
   ```
   Content-Type: application/json
   Authorization: Bearer seu_token_aqui
   ```

3. **Query Params**
   | Key | Value | Descrição |
   |-----|-------|-----------|
   | `destinoBusca` | hospital | Opcional - filtrar por destino |
   | `pagina` | 0 | Opcional - número da página |
   | `tamanho` | 10 | Opcional - registros por página |

### Testar Diferentes Cenários

**Cenário 1: Sem parâmetros**
```
GET http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo
```

**Cenário 2: Com busca**
```
GET http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?destinoBusca=hospital
```

**Cenário 3: Com paginação**
```
GET http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?pagina=1&tamanho=5
```

**Cenário 4: Tudo junto**
```
GET http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?destinoBusca=clinica&pagina=0&tamanho=10
```

---

## Teste de Casos Extremos

### Teste: Pessoa sem viagens

```bash
curl -X GET "http://localhost:8080/api/pessoa/00000000-0000-0000-0000-000000000001/detalhes-completo"
```

**Esperado:** 200 OK com `historicoViagens.conteudo` vazio (`[]`)

---

### Teste: Busca com resultado vazio

```bash
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?destinoBusca=xyz123abc"
```

**Esperado:** 200 OK com `historicoViagens.conteudo` vazio

---

### Teste: ID inválido

```bash
curl -X GET "http://localhost:8080/api/pessoa/invalid-uuid/detalhes-completo"
```

**Esperado:** 400 Bad Request ou 404 Not Found

---

### Teste: Paginação além do limite

```bash
curl -X GET "http://localhost:8080/api/pessoa/550e8400-e29b-41d4-a716-446655440000/detalhes-completo?pagina=9999&tamanho=10"
```

**Esperado:** 200 OK com `historicoViagens.conteudo` vazio

---

## Validação de Resposta

```javascript
function validarRespostaDetalhes(response) {
  const erros = [];
  
  // Validar pessoa
  if (!response.pessoa) erros.push('Campo "pessoa" ausente');
  if (!response.pessoa?.id) erros.push('Campo "pessoa.id" ausente');
  if (!response.pessoa?.nome) erros.push('Campo "pessoa.nome" ausente');
  
  // Validar histórico
  if (!response.historicoViagens) erros.push('Campo "historicoViagens" ausente');
  if (!Array.isArray(response.historicoViagens?.conteudo)) 
    erros.push('Campo "historicoViagens.conteudo" não é array');
  
  // Validar campos de paginação
  if (typeof response.historicoViagens?.paginaAtual !== 'number')
    erros.push('Campo "paginaAtual" não é número');
  if (typeof response.historicoViagens?.totalElementos !== 'number')
    erros.push('Campo "totalElementos" não é número');
  if (typeof response.historicoViagens?.totalPaginas !== 'number')
    erros.push('Campo "totalPaginas" não é número');
  
  // Validar viagens
  response.historicoViagens.conteudo.forEach((viagem, idx) => {
    if (!viagem.id) erros.push(`Viagem ${idx}: campo "id" ausente`);
    if (!viagem.data) erros.push(`Viagem ${idx}: campo "data" ausente`);
    if (!viagem.destinoNome) erros.push(`Viagem ${idx}: campo "destinoNome" ausente`);
  });
  
  if (erros.length === 0) {
    console.log('✅ Resposta válida!');
  } else {
    console.error('❌ Erros encontrados:');
    erros.forEach(erro => console.error(`  - ${erro}`));
  }
  
  return erros.length === 0;
}
```

---

## Teste Completo de Integração

```javascript
async function testeIntegracaoCompleto() {
  console.log('🔄 Teste de Integração Completo\n');
  
  const pessoaId = '550e8400-e29b-41d4-a716-446655440000';
  
  try {
    // Passo 1: Carregar detalhes
    console.log('1️⃣ Carregando detalhes da pessoa...');
    let response = await fetch(
      `${API_BASE}/pessoa/${pessoaId}/detalhes-completo`
    );
    let data = await response.json();
    
    console.log(`   ✅ Pessoa: ${data.pessoa.nome}`);
    console.log(`   ✅ Total de viagens: ${data.historicoViagens.totalElementos}\n`);
    
    // Passo 2: Buscar por destino
    if (data.historicoViagens.totalElementos > 0) {
      const primeiroDestino = data.historicoViagens.conteudo[0].destinoNome;
      
      console.log('2️⃣ Buscando por destino: ' + primeiroDestino);
      response = await fetch(
        `${API_BASE}/pessoa/${pessoaId}/detalhes-completo?destinoBusca=${primeiroDestino}`
      );
      data = await response.json();
      
      console.log(`   ✅ Encontradas: ${data.historicoViagens.totalElementos} viagens\n`);
    }
    
    // Passo 3: Testar paginação
    console.log('3️⃣ Testando paginação...');
    response = await fetch(
      `${API_BASE}/pessoa/${pessoaId}/detalhes-completo?pagina=0&tamanho=5`
    );
    data = await response.json();
    
    console.log(`   ✅ Página 1 de ${data.historicoViagens.totalPaginas}`);
    console.log(`   ✅ Registros na página: ${data.historicoViagens.conteudo.length}\n`);
    
    // Passo 4: Teste endpoint isolado
    console.log('4️⃣ Testando endpoint isolado de histórico...');
    response = await fetch(
      `${API_BASE}/pessoa/${pessoaId}/historico-viagens`
    );
    data = await response.json();
    
    console.log(`   ✅ Histórico carregado: ${data.conteudo.length} registros\n`);
    
    console.log('✅ Teste de Integração Concluído com Sucesso!');
    
  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}
```

---

## Checklist de Teste

- [ ] Teste sem parâmetros retorna resultado
- [ ] Teste com `destinoBusca` filtra corretamente
- [ ] Teste com `pagina` e `tamanho` retorna paginação correta
- [ ] Teste com `destinoBusca` inválido retorna array vazio
- [ ] Teste com `pagina` além do limite retorna array vazio
- [ ] Resposta contém todos os campos esperados
- [ ] Paginação calcula `totalPaginas` corretamente
- [ ] Viagens ordenadas por data DESC (mais recentes primeiro)
- [ ] Tempo de resposta < 1 segundo
- [ ] Endpoint `/historico-viagens` funciona isoladamente

---

**Happy Testing! 🚀**
