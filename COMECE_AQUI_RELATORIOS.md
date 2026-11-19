# Relatórios

Os relatórios exigem os dois serviços em execução: o frontend (`frontSpring`) e
o backend, configurado com MySQL e credenciais conforme as configurações do
projeto. Acesse `/relatorios` no endereço do **frontend**, não no da API. Por
padrão, o frontend usa a porta 80 e a API usa `http://localhost:8080/api`;
`API_BASE_URL` permite configurar outro endereço para a API.

## Dados disponíveis

- Agendamentos por data e destino.
- Pessoas cadastradas por especificação e pessoas com mais agendamentos.
- Totais de tipos de veículos cadastrados, ativos e arquivados.
- Agendamentos com e sem veículo atribuído.

O filtro de datas considera a data do agendamento. Totais cadastrais de pessoas
e tipos de veículos não mudam com o período. O sistema não registra os estados
de realização e cancelamento, portanto essas métricas são indisponíveis.
Filtros por pessoa, destino, veículo e especificação e paginação ainda não estão
implementados. Os documentos anteriores que anunciam esses recursos, outros
gráficos ou testes em produção não refletem a implementação atual.
