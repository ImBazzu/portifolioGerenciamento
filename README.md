# Portifólio de Gerenciamento — Secretaria de Transporte

Aplicação web para apoiar a gestão de uma secretaria de transporte: cadastro de pessoas e localidades, configuração de listas e regras, controle de veículos e organização de agendamentos de transporte.

O repositório contém dois módulos Java/Spring Boot:

- **`backend/`** — API REST, regras de negócio, autenticação e persistência.
- **`frontSpring/`** — interface web renderizada no servidor com Thymeleaf e interações HTMX.

## Funcionalidades

- **Pessoas:** cadastro, consulta, edição, arquivamento e reativação; os dados cadastrais incluem informações usadas no atendimento e no transporte.
- **Cidades e destinos:** manutenção de cidades, estado e tempo de viagem, além dos destinos associados; consulta e gerenciamento de registros arquivados.
- **Tipos de veículo:** cadastro, consulta, edição, histórico e arquivamento de tipos de veículo.
- **Listas e regras:** criação de listas, configuração de regras de cadastro e de separação, consulta e arquivamento.
- **Agendamento:** abertura de uma agenda por dia, inclusão de passageiros e veículos, verificação de veículos permitidos e separação automática, além da consulta de pendências e da associação de um agendamento a um veículo.
- **Acesso:** tela de login e API de autenticação com emissão e atualização de tokens.

As operações disponíveis dependem das regras de negócio e das validações implementadas no backend. A documentação OpenAPI expõe os contratos da API.

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
| Pessoas | `/pessoa` |
| Cidades | `/cidade` |
| Destinos | `/destino` |
| Tipos de veículo | `/veiculo` |
| Listas e regras | `/lista` |
| Agendamentos | `/agendamentos` |

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

Consulte os controllers e DTOs do backend para os parâmetros, corpos de requisição, respostas e validações de cada operação. Com o backend em execução, a interface Swagger/OpenAPI do Springdoc normalmente fica disponível em:

- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- Especificação OpenAPI: `http://localhost:8080/api/v3/api-docs`

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
- revise as permissões: a cadeia HTTP do backend está configurada atualmente com `anyRequest().permitAll()`. A existência de login e JWT não protege automaticamente todos os recursos; regras como `@PreAuthorize` atuam separadamente;
- restrinja o acesso ao banco e às rotas de administração conforme as regras de acesso desejadas;
- habilite HTTPS e revise CORS, logs e configurações de segurança.

## Observações

- As migrações Flyway são a fonte versionada da estrutura do banco; novas mudanças no schema devem ser feitas por novas migrações.
- A aplicação separa registros ativos e arquivados em funcionalidades como cidades, destinos, pessoas, veículos e listas.
- Este README documenta os módulos e comandos presentes no repositório; detalhes de payloads e regras específicas devem ser confirmados nos DTOs, controllers e testes correspondentes.
