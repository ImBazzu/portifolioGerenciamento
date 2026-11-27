# Portifólio de Gerenciamento — Secretaria de Transporte

Aplicação web para apoiar a gestão de uma secretaria de transporte: cadastro de pessoas e localidades, configuração de listas e regras, controle de veículos e organização de agendamentos de transporte.

O repositório contém dois módulos Java/Spring Boot:

- **`backend/`** — API REST, regras de negócio, autenticação e persistência.
- **`frontSpring/`** — interface web renderizada no servidor com Thymeleaf e interações HTMX.

## Funcionalidades

- **Pessoas:** cadastro, consulta, edição, arquivamento e reativação; a listagem mostra a verificação e permite filtrar por especificação e situação de verificação.
- **Prioridades e especificações:** a prioridade define a busca na residência e a quantidade exata de acompanhantes. Uma especificação associa a pessoa à prioridade; as regras de listas selecionam as prioridades aceitas por veículo.
- **Cidades e destinos:** manutenção de cidades, estado e tempo de viagem (de 00:01 a 23:59), além dos destinos associados; consulta e gerenciamento de registros arquivados. Durações antigas de 24 horas ou mais são preservadas, mas precisam ser ajustadas para editar a cidade.
- **Tipos de veículo:** cadastro, consulta, edição, histórico e arquivamento de tipos de veículo.
- **Listas e regras:** criação de listas, configuração de regras de cadastro e de separação, consulta e arquivamento.
- **Agendamento:** abertura de uma agenda por dia, inclusão de passageiros e veículos, consulta do histórico do passageiro, verificação de veículos permitidos e separação automática. Ao abrir um veículo, é possível selecionar um passageiro pendente ou iniciar um novo agendamento.
- **Motoristas:** cadastro e edição de categorias e motoristas, com telefone único e uma categoria por motorista. Motoristas e categorias podem ser arquivados ou reativados; a exclusão definitiva é bloqueada enquanto houver vínculos. Veículos abertos podem receber um motorista ativo; a seleção permite combinar filtros de nome e categoria, e o mesmo motorista pode ser vinculado a vários veículos no dia.
- **Passageiros do dia:** filtros por texto, cidade, status, tipo de veículo e busca na residência; ordenação por nome, cidade, chegada, saída ou tipo de veículo. O botão de PDF exporta os resultados visíveis na ordem escolhida. O PDF fica disponível para visualização e download na mesma sessão por até uma hora; são mantidos os 10 relatórios mais recentes.
- **Tabelas paginadas:** navegação numerada com acesso à primeira e à última página, preservando os filtros de busca.
- **Investigações nos relatórios:** consulta individual de pessoas por nome ou CPF, incluindo arquivadas, com filtros de período, cidade, destino e atribuição de veículo. A análise de motoristas apresenta totais e cidades distintas por motorista, contando cada veículo uma vez por cidade, com passageiros, destinos e horários previstos; filtros de cidade e destino selecionam veículos que têm ao menos um passageiro correspondente.
- **Acesso:** autenticação, perfis cumulativos e gestão de usuários; cada usuário pode alterar seu nome de acesso e senha.

Os formulários de motoristas, categorias e usuários são abertos em modais. Na página de carros de quatro lugares, a abertura é direta e em seguida oferece a inserção de passageiro, sem solicitar identificação.

Nos cartões de carros, o menu de três pontos reúne motorista, PDF, confirmação de saída e exclusão; a inserção de passageiros continua disponível no cartão.

Na abertura de carros para alta ou emergência, a lista correspondente é selecionada automaticamente e não aparece no formulário de agendamento normal. A migração V31 aproveita listas ativas com esses nomes e preserva suas regras; quando não existem, cria as listas com regras iniciais para os tipos de veículo ativos e prioridades existentes, com tolerância de separação de zero para carros. Revise as regras ao adicionar prioridades ou tipos de veículo. Usuários com acesso restrito precisam ter essas listas liberadas na página de usuários. Faça backup do banco antes da atualização.

Na agenda, a saída confirmada destaca o veículo e pode ser desmarcada quando necessário. Desmarcar remove a confirmação e seu registro de horário e usuário; não apaga o veículo nem os passageiros.

As operações disponíveis dependem das regras de negócio e das validações implementadas no backend.

Os relatórios mostram agendamentos e atribuições atuais, não comprovam viagens realizadas. Reatribuições ou exclusões não ficam registradas como histórico de auditoria; nomes, categorias e demais cadastros associados refletem seus valores atuais. Os totais de passageiros não incluem acompanhantes.

Alterações de prioridade aplicam-se apenas a novos agendamentos. Agendamentos existentes mantêm os valores originais, inclusive durante a edição. Os antigos valores cadastrados diretamente na pessoa permanecem disponíveis nos detalhes para consulta, mas não determinam novos agendamentos.

Prioridades podem ser arquivadas e reativadas. Uma prioridade arquivada impede novos agendamentos das pessoas que a utilizam, sem apagar vínculos ou agendamentos existentes. A exclusão definitiva exige remover seus vínculos com especificações e regras de lista, inclusive arquivadas.

A listagem de prioridades exibe a quantidade de especificações vinculadas, incluindo as arquivadas.

As migrações criam as prioridades BAIXA, MODERADA, ALTA e URGENTE inicialmente sem busca na residência e sem acompanhantes, preservando as associações existentes. Faça backup do banco antes de atualizar e configure essas prioridades conforme a necessidade antes de criar novos agendamentos.

**Atenção:** a migração V21 exclui as tabelas legadas `passageiros` e `veiculos` e todos os seus dados, sem migrá-los. Faça backup antes de atualizar. Os históricos passam a usar os agendamentos e veículos de agenda existentes; os dados legados não aparecerão nesses históricos.

## Arquitetura

```text
Navegador
   │
   ▼
frontSpring — Spring MVC, Thymeleaf e HTMX
   │ chamadas HTTP / JSON
   ▼
backend — API REST, aplicações e domínio
   │
   ├── Spring Data JPA
   ├── Flyway
   └── MySQL
```

O backend organiza funcionalidades em módulos de domínio — como `pessoa`, `localidades`, `tipoVeiculo`, `lista`, `agendamento` e `seguranca` — com camadas de entrada, aplicação, domínio e persistência. A interface web mantém módulos próprios e consome a API do backend.

### Tecnologias

| Área | Tecnologias |
|---|---|
| Linguagem e build | Java 27, Maven Wrapper |
| API e aplicação | Spring Boot, Spring MVC, Spring Validation |
| Interface web | Spring MVC, Thymeleaf, HTMX |
| Persistência | Spring Data JPA, MySQL 8, Flyway |
| Segurança | Spring Security, JWT |
| Cache | Spring Cache, Caffeine |
| Testes | JUnit e Spring Boot Test |
| Empacotamento | Dockerfiles para backend e frontend; Docker Compose na raiz |

As versões dos frameworks e bibliotecas estão declaradas em `backend/pom.xml` e `frontSpring/pom.xml`.

## Requisitos

- **JDK 27**, conforme configurado nos dois `pom.xml`.
- MySQL 8 acessível localmente ou em outro host.
- Docker apenas se for executar testes que dependam de containers ou preparar imagens.

Os projetos incluem Maven Wrapper, então não é necessário instalar Maven separadamente. No Windows, os comandos abaixo usam PowerShell e `mvnw.cmd`.

## Executar localmente

### 1. Criar o banco de dados

Crie o banco `secretaria_transporte` no MySQL e configure um usuário com permissões para acessá-lo. Na inicialização, o backend aplica as migrações Flyway encontradas em `backend/src/main/resources/db/migration`.

Exemplo de criação do schema:

```sql
CREATE DATABASE secretaria_transporte
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar e iniciar o backend

Abra um terminal PowerShell na raiz do repositório, configure as variáveis de ambiente e inicie a aplicação:

```powershell
cd backend

$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_BANCO = "secretaria_transporte"
$env:DB_USER = "<usuario-do-mysql>"
$env:DB_PASSWORD = "<senha-do-mysql>"
$env:JWT_SECRETS = "<segredo-forte-para-tokens>"
$env:JWT_ASSINATURA = "<chave-de-assinatura>"

.\mvnw.cmd spring-boot:run
```

Substitua os valores entre `<...>` pelos valores do seu ambiente. Não use credenciais de exemplo em ambientes compartilhados ou de produção.

O backend escuta na porta **8080** e publica a API sob o contexto **`/api`**.

### 3. Configurar e iniciar a interface

Em outro terminal PowerShell, na raiz do repositório:

```powershell
cd frontSpring

$env:API_BASE_URL = "http://localhost:8080/api"
$env:SERVER_PORT = "8081"

.\mvnw.cmd spring-boot:run
```

Com essa configuração, abra **http://localhost:8081/login**. O frontend usa `http://localhost:8080/api` como URL padrão da API quando `API_BASE_URL` não é definida. A porta 8081 é usada no exemplo para evitar conflito com a porta 80 configurada por padrão no módulo web.

### Configurações principais

| Variável | Módulo | Descrição |
|---|---|---|
| `DB_HOST` | Backend | Host do MySQL |
| `DB_PORT` | Backend | Porta do MySQL |
| `DB_BANCO` | Backend | Nome do banco de dados |
| `DB_USER` | Backend | Usuário do banco |
| `DB_PASSWORD` | Backend | Senha do banco |
| `JWT_SECRETS` | Backend | Segredo usado na configuração de tokens |
| `JWT_ASSINATURA` | Backend | Chave/configuração de assinatura dos tokens |
| `API_BASE_URL` | Frontend | URL base da API; padrão `http://localhost:8080/api` |
| `SERVER_PORT` | Frontend | Porta HTTP da interface; padrão definido no módulo |

As configurações adicionais ficam em `backend/src/main/resources/application.properties` e `frontSpring/src/main/resources/application.properties`.

## API

O endereço base é `http://localhost:8080/api`. Os principais grupos de recursos são:

| Recurso | Prefixo |
|---|---|
| Autenticação e usuários | `/auth` |
| Gestão de usuários e conta | `/usuarios` |
| Pessoas | `/pessoa` |
| Prioridades | `/prioridades` |
| Especificações | `/especificacao` |
| Cidades | `/cidade` |
| Destinos | `/destino` |
| Tipos de veículo | `/veiculo` |
| Listas e regras | `/lista` |
| Agendamentos | `/agendamentos` |
| Motoristas | `/motoristas` |

Exemplos de operações:

```text
POST   /api/auth/login
POST   /api/auth/atualizarToken
GET    /api/cidade
POST   /api/pessoa
GET    /api/lista/listar
POST   /api/agendamentos/dias/{data}/abrir
POST   /api/agendamentos/dias/{data}/separacao-automatica
```

Na separação automática de carros, cidades diferentes podem compartilhar um veículo se cada par de cidades já tiver sido associado ao mesmo veículo em uma data anterior. A combinação é válida nos dois sentidos e não dispensa as regras de capacidade e horário.

Consulte os controllers e DTOs do backend para os parâmetros, corpos de requisição, respostas e validações de cada operação. O endpoint `GET /api/health` verifica se a aplicação está respondendo.

### Perfis de acesso

Os perfis são cumulativos: **Agendamento**, **Motoristas**, **Gestão operacional** e **Administração**. Somente gestão e administração podem confirmar ou retirar a validação de pessoas. Uma pessoa validada não pode ser editada, arquivada, restaurada ou excluída até que sua validação seja retirada. Somente administradores gerenciam usuários; o último administrador ativo não pode perder o perfil, ser arquivado ou excluído.

A migração V24 converte o perfil antigo `USUARIO` em `AGENDAMENTO` e mantém os administradores. Sessões anteriores à atualização precisam entrar novamente. A alteração de senha invalida as demais sessões; nomes e senhas são alterados em **Minha conta** mediante confirmação da senha atual.

A migração V25 vincula novos cadastros de pessoas e agendamentos ao usuário autenticado. O autor permanece o mesmo após edições e é exibido pelo nome atual; registros anteriores aparecem como **Autor desconhecido**.

A migração V26 permite arquivar motoristas, categorias e usuários. Registros vinculados não podem ser excluídos; usuários arquivados perdem acesso e sessões anteriores não voltam a valer após a reativação. O administrador não pode arquivar ou excluir a própria conta.

## Testes e build

Execute os testes de cada módulo separadamente:

```powershell
cd backend
.\mvnw.cmd test
```

```powershell
cd frontSpring
.\mvnw.cmd test
```

Para gerar os artefatos:

```powershell
cd backend
.\mvnw.cmd clean package
```

```powershell
cd frontSpring
.\mvnw.cmd clean package
```

Os arquivos JAR são gerados no diretório `target/` de cada módulo.

## Estrutura do repositório

```text
.
├── backend/
│   ├── src/main/java/              # API, domínio, aplicações e adaptadores
│   ├── src/main/resources/
│   │   ├── db/migration/           # Migrações Flyway
│   │   └── application.properties  # Configuração do backend
│   ├── src/test/                   # Testes do backend
│   ├── Dockerfile
│   ├── pom.xml
│   └── mvnw.cmd
├── frontSpring/
│   ├── src/main/java/              # Controllers e aplicações da interface
│   ├── src/main/resources/
│   │   ├── templates/              # Páginas e fragmentos Thymeleaf
│   │   ├── static/                 # CSS, JavaScript e imagens
│   │   └── application.properties
│   ├── src/test/                   # Testes da interface
│   ├── pom.xml
│   └── mvnw.cmd
├── mysql-init/                     # Inicialização do MySQL via Docker
└── docker-compose.yml
```

## Docker e implantação

O Docker Compose sobe o MySQL, o backend e o frontend. Configure as variáveis do arquivo `.env.prod` antes de iniciar os serviços:

```powershell
docker compose --env-file .env.prod up --build
```

O Compose aguarda o MySQL ficar saudável antes de iniciar o backend e aguarda a API antes de iniciar o frontend. A interface fica disponível em `http://localhost:8081` e a API em `http://localhost:8080/api`. Os dados do MySQL são persistidos no volume `mysql_data`.

O usuário da aplicação e sua senha são provisionados a partir de `DB_USER` e `DB_PASSWORD`; não mantenha credenciais em scripts SQL versionados. `MYSQL_ROOT_PASSWORD` é opcional para uma instalação nova e, se omitida, mantém a senha padrão de desenvolvimento `root` para compatibilidade com volumes criados pela configuração anterior. Defina uma senha própria em ambientes não locais; a configuração de senha do MySQL não altera volumes já inicializados.

Antes de publicar ou implantar o sistema:

- forneça credenciais e segredos por um gerenciador de segredos ou variáveis de ambiente protegidas;
- substitua credenciais de desenvolvimento e revise os scripts de inicialização do banco;
- use segredos JWT fortes e exclusivos por ambiente;
- revise os perfis e os usuários administrativos antes de disponibilizar o sistema;
- restrinja o acesso ao banco e às rotas de administração conforme as regras de acesso desejadas;
- habilite HTTPS e revise CORS, logs e configurações de segurança.

## Observações

- As migrações Flyway são a fonte versionada da estrutura do banco; novas mudanças no schema devem ser feitas por novas migrações.
- A aplicação separa registros ativos e arquivados em funcionalidades como cidades, destinos, pessoas, veículos e listas.
- Este README documenta os módulos e comandos presentes no repositório; detalhes de payloads e regras específicas devem ser confirmados nos DTOs, controllers e testes correspondentes.
