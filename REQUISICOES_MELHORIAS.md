# Requisições de Melhorias - Sistema de Agendamento de Transportes

## 1. Funcionalidades de Abertura de Veículos 4 Lugares

### 1.1 Motivo 01: Ajustes de Separação e Inclusão
- **Descrição**: Facilitar abertura de veículos para ajustar separação automática ou inserir passageiros não previamente listados
- **Prioridade**: Normal
- **Status**: Planejado

### 1.2 Motivo 02: Emergência
- **Descrição**: Facilitar abertura de veículos para chamadas de emergência
- **Requisitos**:
  - Informar **horário de saída do veículo** (não horário de chegada)
  - Integrar com lista "Emergencia" existente (passageiro pode ir em qualquer tipo de veículo)
  - Facilitar a entrada de dados (reduzir passos de preenchimento)
- **Prioridade**: Alta
- **Status**: Planejado
- **Observação**: Possibilidade de conciliar com veículos já agendados

### 1.3 Motivo 03: Alta Hospitalar
- **Descrição**: Facilitar abertura de veículos para saídas de pacientes do hospital
- **Requisitos**:
  - Informar **horário de saída do veículo** (não horário de chegada)
  - Integrar com lista "Alta" existente
  - Facilitar entrada de dados
- **Prioridade**: Alta
- **Status**: Planejado
- **Observação**: Possibilidade de conciliar com veículos já agendados

---

## 2. Cancelamento de Viagens

- **Descrição**: Implementar status de "Cancelamento" para viagens, em vez de apenas deletar registros
- **Requisitos**:
  - Rastrear motivo do cancelamento (opcional)
  - Manter auditoria de cancelamentos
  - Permitir visualização de viagens canceladas em relatórios
- **Prioridade**: Alta
- **Status**: Planejado
- **Impacto**: Dados (adicionar status/enum, soft-delete)

---

## 3. Confirmação de Saída do Veículo

- **Descrição**: Adicionar confirmação "Ok" para validar saída do veículo
- **Requisitos**:
  - Botão/campo de confirmação de saída
  - Registrar timestamp de confirmação
  - Possível validação de regras antes da confirmação
- **Prioridade**: Normal
- **Status**: Planejado

---

## 4. Regra de Horário: Veículos Noturnos

- **Descrição**: Veículos que saem às 20:00 (ou posterior) devem aparecer antes das 05:30 na tela de agendamento
- **Requisitos**:
  - Regra de agrupamento visual: "Viagens Noturnas" / "Horário Normal"
  - Ordenar por horário de saída dentro de cada grupo
- **Prioridade**: Normal
- **Status**: Planejado

---

## 5. Ordenação e Marcação Visual de Veículos

- **Descrição**: Melhorar visualização de veículos na tela de agendamento
- **Requisitos**:
  - Ordenação primária: horário de saída
  - Marcação visual diferenciada para:
    - Veículos antes das 05:30
    - Veículos depois das 05:30
  - Possivelmente usar cores ou badges visuais
- **Prioridade**: Normal
- **Status**: Planejado

---

## 6. Visualização de Buscas na Residência

- **Descrição**: Facilitar visualização de passageiros que serão buscados na residência
- **Requisitos**:
  - Marcar visualmente passageiros com `buscarNaResidencia = true`
  - Ícone, cor ou badge indicativo
  - Facilitar filtros por "Buscar na Residência"
- **Prioridade**: Normal
- **Status**: Planejado

---

## 7. Permissões de Listas por Usuário

- **Descrição**: Gestores podem escolher quais listas aparecem na agendamento para cada usuário
- **Requisitos**:
  - Tela de administração de permissões de listas por usuário
  - Apenas usuários com função "Gestão" ou superior podem editar
  - Listas filtradas dinamicamente por usuário na tela de agendamento
- **Prioridade**: Normal
- **Status**: Planejado
- **Impacto**: RBAC/Permissões, dados

---

## 8. Responsividade em Tablet

- **Descrição**: Adaptar layout para tablets com melhor experiência visual
- **Requisitos**:
  - Layout responsivo para resolução de tablet
  - Carros dispostos lado a lado (grid layout)
  - Manter funcionalidade em telas menores
  - Testar em orientações portrait/landscape
- **Prioridade**: Normal
- **Status**: Planejado
- **Impacto**: Frontend (CSS/Layout)

---

## 9. Página de Usuário

- **Descrição**: Exibir informações do usuário logado com opção de edição
- **Requisitos**:
  - Tela com dados do usuário (nome, email, função, etc.)
  - Botão para editar dados
  - Validar permissões de edição
  - Atualizar perfil sem logout
- **Prioridade**: Normal
- **Status**: Planejado

---

## 10. Relatório de Listas

- **Descrição**: Estudo e análise de listas com dados agregados
- **Requisitos**:
  - Visualizar dados de uma lista específica
  - Visualizar dados de múltiplas listas agrupadas
  - Métricas: quantidade de viagens, passageiros, rotas, etc.
  - Filtros por período
- **Prioridade**: Normal
- **Status**: Planejado
- **Impacto**: Backend (relatórios), Frontend (UI)

---

## 11. Relatório de Semana

- **Descrição**: Análise de viagens agrupadas por período
- **Requisitos**:
  - Quantidade de viagens por dia da semana
  - Horários de cada viagem (ou horários agrupados)
  - Visualizar tendências semanais
  - Filtros por lista, período, destino, etc.
- **Prioridade**: Normal
- **Status**: Planejado
- **Impacto**: Backend (relatórios), Frontend (gráficos)

---

## 12. Análise de Cidade e Destinos

- **Descrição**: Adicionar seção de estudo de cidades e destinos na agendamento
- **Requisitos**:
  - Visualizar cidades e destinos mais frequentes
  - Dados históricos de viagens por destino
  - Possível integração com mapas
  - Filtros por período
- **Prioridade**: Normal
- **Status**: Planejado
- **Impacto**: Backend (dados), Frontend (UI/mapas)

---

## 13. Progressive Web App (PWA)

- **Descrição**: Permitir instalação do site como aplicativo nativo
- **Requisitos**:
  - Implementar `manifest.json`
  - Service Worker para offline
  - Ícones para múltiplas resoluções
  - Tema de cores e nome da aplicação
  - Splash screens (opcional)
  - Testable em Android e iOS
- **Prioridade**: Normal
- **Status**: Planejado
- **Impacto**: Frontend (PWA configuration)

---

## Resumo de Prioridades

### Alta
- Motivo 02 (Emergência)
- Motivo 03 (Alta Hospitalar)
- Cancelamento de Viagens

### Normal
- Motivo 01 (Ajustes)
- Confirmação de Saída
- Regra de Horário Noturno
- Ordenação e Marcação Visual
- Visualização Residência
- Permissões de Listas
- Responsividade Tablet
- Página de Usuário
- Relatórios (Listas, Semana)
- Análise Cidades/Destinos
- PWA

---

## Próximos Passos

1. **Validar prioridades** com stakeholders
2. **Estimar esforço** para cada requisição
3. **Priorizar por valor** e **complexidade**
4. **Iniciar** com requisições de alta prioridade
5. **Quebrar** em tasks menores (sprints/ciclos)
