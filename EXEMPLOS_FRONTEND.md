# 💻 Exemplos de Uso no Frontend

## 📌 Vanilla JavaScript (Puro)

### Componente Simples

```javascript
class DetalhePessoaView {
  constructor(pessoaId, containerId) {
    this.pessoaId = pessoaId;
    this.container = document.getElementById(containerId);
    this.paginaAtual = 0;
    this.tamanhoPagina = 10;
    this.destinoBusca = '';
  }

  async carregarDetalhes() {
    try {
      const params = this.montarParams();
      const response = await fetch(
        `/api/pessoa/${this.pessoaId}/detalhes-completo?${params}`
      );
      
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      
      const data = await response.json();
      this.renderizar(data);
      
    } catch (error) {
      console.error('Erro ao carregar:', error);
      this.mostrarErro('Falha ao carregar detalhes');
    }
  }

  montarParams() {
    const params = new URLSearchParams({
      destinoBusca: this.destinoBusca,
      pagina: this.paginaAtual,
      tamanho: this.tamanhoPagina
    });
    return params.toString();
  }

  renderizar(data) {
    this.container.innerHTML = `
      <div class="detalhes-pessoa">
        <h2>${data.pessoa.nome}</h2>
        <p><strong>CPF:</strong> ${data.pessoa.cpf}</p>
        <p><strong>Especificação:</strong> ${data.pessoa.especificacaoNome}</p>
        <p><strong>Prioridade:</strong> ${data.pessoa.prioridadeNome}</p>
        
        <div class="historico-section">
          <h3>Histórico de Viagens (${data.historicoViagens.totalElementos})</h3>
          
          <input 
            type="text" 
            placeholder="Buscar por destino..."
            id="busca-destino"
            value="${this.destinoBusca}"
          />
          
          ${this.renderizarTabela(data.historicoViagens)}
          ${this.renderizarPaginacao(data.historicoViagens)}
        </div>
      </div>
    `;
    
    this.anexarEventos();
  }

  renderizarTabela(historico) {
    if (historico.conteudo.length === 0) {
      return '<p>Nenhuma viagem encontrada</p>';
    }

    const linhas = historico.conteudo.map(viagem => `
      <tr>
        <td>${viagem.data}</td>
        <td>${viagem.horarioChegada}</td>
        <td>${viagem.horarioSaidaNecessario}</td>
        <td>${viagem.destinoNome}</td>
        <td>${viagem.tipoVeiculoNome}</td>
        <td>${viagem.acompanhantes}</td>
      </tr>
    `).join('');

    return `
      <table>
        <thead>
          <tr>
            <th>Data</th>
            <th>Horário Chegada</th>
            <th>Horário Saída Necessário</th>
            <th>Destino</th>
            <th>Veículo</th>
            <th>Acompanhantes</th>
          </tr>
        </thead>
        <tbody>
          ${linhas}
        </tbody>
      </table>
    `;
  }

  renderizarPaginacao(historico) {
    return `
      <div class="paginacao">
        <button 
          id="btn-anterior" 
          ${this.paginaAtual === 0 ? 'disabled' : ''}
        >
          ← Anterior
        </button>
        
        <span>
          Página ${this.paginaAtual + 1} de ${historico.totalPaginas}
        </span>
        
        <button 
          id="btn-proxima" 
          ${this.paginaAtual >= historico.totalPaginas - 1 ? 'disabled' : ''}
        >
          Próxima →
        </button>
      </div>
    `;
  }

  anexarEventos() {
    // Buscar por destino
    const inputBusca = document.getElementById('busca-destino');
    if (inputBusca) {
      inputBusca.addEventListener('input', (e) => {
        this.destinoBusca = e.target.value;
        this.paginaAtual = 0; // Resetar para primeira página
        this.carregarDetalhes();
      });
    }

    // Botões de paginação
    document.getElementById('btn-anterior')?.addEventListener('click', () => {
      this.paginaAtual--;
      this.carregarDetalhes();
    });

    document.getElementById('btn-proxima')?.addEventListener('click', () => {
      this.paginaAtual++;
      this.carregarDetalhes();
    });
  }

  mostrarErro(mensagem) {
    this.container.innerHTML = `<div class="erro">${mensagem}</div>`;
  }
}

// Uso:
const view = new DetalhePessoaView('uuid-da-pessoa', 'container');
view.carregarDetalhes();
```

---

## ⚛️ React

### Componente Funcional com Hooks

```jsx
import { useState, useEffect } from 'react';

function DetalhePessoa({ pessoaId }) {
  const [pessoa, setPessoa] = useState(null);
  const [historicoViagens, setHistoricoViagens] = useState(null);
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [tamanho] = useState(10);
  const [destinoBusca, setDestinoBusca] = useState('');
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    carregarDetalhes();
  }, [paginaAtual, destinoBusca]);

  async function carregarDetalhes() {
    try {
      setLoading(true);
      setErro(null);

      const params = new URLSearchParams({
        destinoBusca,
        pagina: paginaAtual,
        tamanho
      });

      const response = await fetch(
        `/api/pessoa/${pessoaId}/detalhes-completo?${params}`
      );

      if (!response.ok) throw new Error('Falha ao carregar');

      const data = await response.json();
      setPessoa(data.pessoa);
      setHistoricoViagens(data.historicoViagens);

    } catch (err) {
      setErro(err.message);
    } finally {
      setLoading(false);
    }
  }

  if (loading) return <div>Carregando...</div>;
  if (erro) return <div className="erro">{erro}</div>;
  if (!pessoa) return <div>Sem dados</div>;

  return (
    <div className="detalhes-pessoa">
      <h2>{pessoa.nome}</h2>
      
      <div className="info-pessoa">
        <p><strong>CPF:</strong> {pessoa.cpf}</p>
        <p><strong>Telefone:</strong> {pessoa.telefone}</p>
        <p><strong>Especificação:</strong> {pessoa.especificacaoNome}</p>
        <p><strong>Prioridade:</strong> {pessoa.prioridadeNome}</p>
      </div>

      <div className="historico-section">
        <h3>Histórico de Viagens</h3>

        <input
          type="text"
          placeholder="Buscar por destino..."
          value={destinoBusca}
          onChange={(e) => {
            setDestinoBusca(e.target.value);
            setPaginaAtual(0);
          }}
        />

        {historicoViagens?.conteudo.length === 0 ? (
          <p>Nenhuma viagem encontrada</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Data</th>
                <th>Chegada</th>
                <th>Saída Necessária</th>
                <th>Destino</th>
                <th>Veículo</th>
                <th>Acompanhantes</th>
              </tr>
            </thead>
            <tbody>
              {historicoViagens?.conteudo.map((viagem) => (
                <tr key={viagem.id}>
                  <td>{viagem.data}</td>
                  <td>{viagem.horarioChegada}</td>
                  <td>{viagem.horarioSaidaNecessario}</td>
                  <td>{viagem.destinoNome}</td>
                  <td>{viagem.tipoVeiculoNome}</td>
                  <td>{viagem.acompanhantes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        <div className="paginacao">
          <button
            onClick={() => setPaginaAtual(paginaAtual - 1)}
            disabled={paginaAtual === 0}
          >
            ← Anterior
          </button>

          <span>
            Página {paginaAtual + 1} de {historicoViagens?.totalPaginas}
          </span>

          <button
            onClick={() => setPaginaAtual(paginaAtual + 1)}
            disabled={paginaAtual >= (historicoViagens?.totalPaginas || 1) - 1}
          >
            Próxima →
          </button>
        </div>
      </div>
    </div>
  );
}

export default DetalhePessoa;
```

---

## 🖖 Vue 3

### Componente Vue

```vue
<template>
  <div class="detalhes-pessoa">
    <div v-if="loading">Carregando...</div>
    <div v-else-if="erro" class="erro">{{ erro }}</div>
    <div v-else>
      <h2>{{ pessoa.nome }}</h2>

      <div class="info-pessoa">
        <p><strong>CPF:</strong> {{ pessoa.cpf }}</p>
        <p><strong>Especificação:</strong> {{ pessoa.especificacaoNome }}</p>
        <p><strong>Prioridade:</strong> {{ pessoa.prioridadeNome }}</p>
      </div>

      <div class="historico-section">
        <h3>Histórico de Viagens</h3>

        <input
          v-model="destinoBusca"
          type="text"
          placeholder="Buscar por destino..."
          @input="resetarPaginacao"
        />

        <table v-if="historicoViagens.conteudo.length > 0">
          <thead>
            <tr>
              <th>Data</th>
              <th>Chegada</th>
              <th>Saída Necessária</th>
              <th>Destino</th>
              <th>Veículo</th>
              <th>Acompanhantes</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="viagem in historicoViagens.conteudo" :key="viagem.id">
              <td>{{ viagem.data }}</td>
              <td>{{ viagem.horarioChegada }}</td>
              <td>{{ viagem.horarioSaidaNecessario }}</td>
              <td>{{ viagem.destinoNome }}</td>
              <td>{{ viagem.tipoVeiculoNome }}</td>
              <td>{{ viagem.acompanhantes }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else>Nenhuma viagem encontrada</p>

        <div class="paginacao">
          <button
            @click="irParaPagina(paginaAtual - 1)"
            :disabled="paginaAtual === 0"
          >
            ← Anterior
          </button>

          <span>
            Página {{ paginaAtual + 1 }} de {{ historicoViagens.totalPaginas }}
          </span>

          <button
            @click="irParaPagina(paginaAtual + 1)"
            :disabled="paginaAtual >= historicoViagens.totalPaginas - 1"
          >
            Próxima →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  props: {
    pessoaId: String
  },
  setup(props) {
    const pessoa = ref(null);
    const historicoViagens = ref({ conteudo: [] });
    const paginaAtual = ref(0);
    const tamanho = ref(10);
    const destinoBusca = ref('');
    const loading = ref(true);
    const erro = ref(null);

    async function carregarDetalhes() {
      try {
        loading.value = true;
        erro.value = null;

        const params = new URLSearchParams({
          destinoBusca: destinoBusca.value,
          pagina: paginaAtual.value,
          tamanho: tamanho.value
        });

        const response = await fetch(
          `/api/pessoa/${props.pessoaId}/detalhes-completo?${params}`
        );

        if (!response.ok) throw new Error('Falha ao carregar');

        const data = await response.json();
        pessoa.value = data.pessoa;
        historicoViagens.value = data.historicoViagens;

      } catch (err) {
        erro.value = err.message;
      } finally {
        loading.value = false;
      }
    }

    function irParaPagina(novaPagina) {
      paginaAtual.value = novaPagina;
      carregarDetalhes();
    }

    function resetarPaginacao() {
      paginaAtual.value = 0;
      carregarDetalhes();
    }

    onMounted(carregarDetalhes);

    return {
      pessoa,
      historicoViagens,
      paginaAtual,
      destinoBusca,
      loading,
      erro,
      irParaPagina,
      resetarPaginacao
    };
  }
};
</script>

<style scoped>
.detalhes-pessoa {
  padding: 20px;
}

.info-pessoa {
  margin: 20px 0;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.historico-section {
  margin-top: 30px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
}

table th, table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

table th {
  background-color: #f9f9f9;
  font-weight: bold;
}

.paginacao {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```

---

## 🎨 CSS Básico

```css
.detalhes-pessoa {
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.info-pessoa {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.info-pessoa p {
  margin: 8px 0;
}

.historico-section h3 {
  color: #333;
  margin-bottom: 15px;
}

input[type="text"] {
  width: 300px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
}

table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

table thead {
  background-color: #f8f9fa;
}

table th, table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #dee2e6;
}

table tbody tr:hover {
  background-color: #f8f9fa;
}

.paginacao {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.paginacao button {
  padding: 10px 20px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.paginacao button:hover:not(:disabled) {
  background-color: #764ba2;
}

.paginacao button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.erro {
  background-color: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  border: 1px solid #f5c6cb;
}
```

---

## 🔗 Hooks Úteis

### Hook de Busca com Debounce (React)

```javascript
import { useState, useCallback, useRef } from 'react';

function useDetalhePessoa(pessoaId) {
  const [pessoa, setPessoa] = useState(null);
  const [historicoViagens, setHistoricoViagens] = useState({ conteudo: [] });
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [destinoBusca, setDestinoBusca] = useState('');
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState(null);
  const timeoutRef = useRef(null);

  const carregarDetalhes = useCallback(async (pagina = 0, busca = '') => {
    try {
      setLoading(true);
      setErro(null);

      const params = new URLSearchParams({
        destinoBusca: busca,
        pagina: pagina,
        tamanho: 10
      });

      const response = await fetch(
        `/api/pessoa/${pessoaId}/detalhes-completo?${params}`
      );

      if (!response.ok) throw new Error('Erro na requisição');

      const data = await response.json();
      setPessoa(data.pessoa);
      setHistoricoViagens(data.historicoViagens);

    } catch (err) {
      setErro(err.message);
    } finally {
      setLoading(false);
    }
  }, [pessoaId]);

  const buscar = useCallback((termo) => {
    setDestinoBusca(termo);
    setPaginaAtual(0);

    // Debounce de 500ms
    clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => {
      carregarDetalhes(0, termo);
    }, 500);
  }, [carregarDetalhes]);

  const irParaPagina = useCallback((pagina) => {
    setPaginaAtual(pagina);
    carregarDetalhes(pagina, destinoBusca);
  }, [carregarDetalhes, destinoBusca]);

  return {
    pessoa,
    historicoViagens,
    paginaAtual,
    destinoBusca,
    loading,
    erro,
    buscar,
    irParaPagina,
    carregarDetalhes
  };
}

export default useDetalhePessoa;
```

---

## 🎯 Uso Rápido

### Vanilla JS
```javascript
const view = new DetalhePessoaView('uuid', 'container');
view.carregarDetalhes();
```

### React
```jsx
<DetalhePessoa pessoaId="uuid" />
```

### Vue
```vue
<DetalhePessoa :pessoaId="uuid" />
```

---

**Escolha o exemplo que melhor se encaixa no seu projeto! 🚀**
