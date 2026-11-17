# 💰 PFinanças 2.0 — Sistema de Gestão Financeira Pessoal

Sistema de gerenciamento financeiro pessoal desenvolvido em **Java 21**, utilizando **Spring Boot**, **PostgreSQL**, **Spring Data JPA** e **Spring Security com autenticação JWT**.

O projeto foi desenvolvido durante a formação avançada em Java do **Capacita Brasil**, com o objetivo de aplicar conceitos de Programação Orientada a Objetos, persistência de dados, arquitetura em camadas, desenvolvimento de APIs REST e segurança de aplicações.

A versão 2.0 representa a evolução do projeto original, desenvolvido com JavaFX e SQLite, para uma arquitetura backend baseada em serviços REST.

---

## 🎯 Objetivo

Permitir que usuários gerenciem suas finanças pessoais por meio do cadastro de receitas, despesas e categorias, acompanhando o saldo disponível e consultando relatórios financeiros.

O sistema possui autenticação individual e separação dos dados financeiros por usuário.

## 🚀 Funcionalidades

- Cadastro de usuários com senha protegida por BCrypt.
- Autenticação utilizando JSON Web Token (JWT).
- Gerenciamento de categorias financeiras.
- Cadastro, listagem, atualização e exclusão de transações.
- Registro de receitas e despesas.
- Consulta de saldo financeiro.
- Validação de saldo antes de registrar despesas.
- Relatórios por período, categoria e tipo de transação.
- Resumo financeiro com receitas, despesas e saldo.
- Validação de dados e tratamento de exceções.
- Documentação interativa da API com Swagger/OpenAPI.
- Testes automatizados com JUnit e Mockito.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 3.3.4 | Desenvolvimento da API REST |
| Spring Web | Controllers e endpoints HTTP |
| Spring Data JPA | Persistência de entidades |
| Hibernate | Mapeamento objeto-relacional |
| PostgreSQL | Banco de dados relacional |
| Spring Security | Controle de autenticação e acesso |
| JWT (JJWT) | Autenticação por tokens |
| BCrypt | Criptografia unidirecional de senhas |
| Bean Validation | Validação de dados |
| Swagger / OpenAPI | Documentação da API |
| Maven | Gerenciamento de dependências |
| JUnit e Mockito | Testes automatizados |
| JavaFX e SQLite | Tecnologias utilizadas na versão desktop original |

## 🏗️ Arquitetura do projeto

A aplicação segue uma organização em camadas, separando as responsabilidades do sistema.

```text
src/
├── main/
│   ├── java/com/pfinancas/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── servico/
│   │   └── FinTrackApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/pfinancas/
```

**Responsabilidades das principais camadas:**

- **Controller:** recebe requisições HTTP e retorna respostas REST.
- **Service (`servico`):** implementa as regras de negócio.
- **Repository:** realiza operações de persistência com Spring Data JPA.
- **Model:** representa as entidades do sistema.
- **DTO:** define os dados de entrada e saída da API.
- **Security:** realiza a autenticação e validação dos tokens JWT.
- **Config:** centraliza as configurações de segurança, senhas e OpenAPI.
- **Exception:** trata erros e padroniza respostas HTTP.

O repositório também mantém componentes da versão desktop original, como `app`, `controle`, `dao` e arquivos JavaFX.

---

## ⚙️ Requisitos para execução

Antes de iniciar o projeto, é necessário instalar:

- JDK 21.
- Apache Maven.
- PostgreSQL.
- Git (opcional, para clonar o repositório).

## 🗄️ Configuração do PostgreSQL

Crie um usuário e um banco de dados no PostgreSQL.

Exemplo de comandos SQL, executados com uma conta administrativa:

```sql
CREATE USER fintrack_user WITH PASSWORD 'defina_uma_senha_segura';

CREATE DATABASE fintrack OWNER fintrack_user;
```

O arquivo `src/main/resources/application.properties` utiliza a seguinte configuração:

```properties
spring.application.name=fintrack

spring.datasource.url=jdbc:postgresql://localhost:5433/fintrack
spring.datasource.username=fintrack_user
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.port=8080

jwt.secret=${JWT_SECRET}
jwt.expiration-ms=3600000
```

**Importante:** o projeto utiliza a porta PostgreSQL `5433`. Caso a instalação utilize outra porta, ajuste a URL de conexão.

As credenciais não devem ser armazenadas diretamente no código-fonte.

## ▶️ Executando a aplicação

No terminal Linux, acesse a pasta do projeto e configure as variáveis de ambiente:

```bash
read -rsp "Senha do PostgreSQL: " DB_PASSWORD
echo
export DB_PASSWORD

export JWT_SECRET="$(openssl rand -base64 32)"
```

Em seguida, execute:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em:

`http://localhost:8080`

A chave JWT gerada nesse exemplo é temporária. Reiniciar a aplicação com uma nova chave invalida os tokens emitidos anteriormente.

---

## 🔐 Autenticação JWT

A API utiliza autenticação baseada em tokens JWT.

O fluxo de autenticação consiste em:

1. Cadastrar um usuário.
2. Realizar o login utilizando e-mail e senha.
3. Receber o token JWT.
4. Enviar o token nas requisições aos endpoints protegidos.

Exemplo do cabeçalho HTTP:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

As rotas de autenticação são públicas. As rotas de categorias, transações e relatórios exigem autenticação.

## 🌐 Endpoints da API

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/auth/cadastro` | Cadastrar usuário |
| POST | `/api/v1/auth/login` | Autenticar usuário |

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/v1/categorias` | Listar categorias |
| POST | `/api/v1/categorias` | Cadastrar categoria |

### Transações

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/v1/transacoes` | Listar transações |
| GET | `/api/v1/transacoes/saldo` | Consultar saldo |
| POST | `/api/v1/transacoes` | Cadastrar transação |
| PUT | `/api/v1/transacoes/{id}` | Atualizar transação |
| DELETE | `/api/v1/transacoes/{id}` | Excluir transação |

### Relatórios financeiros

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/v1/transacoes/relatorios/periodo` | Consultar por período |
| GET | `/api/v1/transacoes/relatorios/categoria` | Consultar por categoria |
| GET | `/api/v1/transacoes/relatorios/tipo` | Consultar por tipo |
| GET | `/api/v1/transacoes/relatorios/resumo` | Consultar resumo financeiro |

## 📌 Exemplos de requisições

**Cadastro de usuário**

`POST /api/v1/auth/cadastro`

```json
{
  "nome": "Usuario Exemplo",
  "email": "usuario@example.com",
  "senha": "senha12345"
}
```

**Login**

`POST /api/v1/auth/login`

```json
{
  "email": "usuario@example.com",
  "senha": "senha12345"
}
```

**Cadastro de categoria**

`POST /api/v1/categorias`

```json
{
  "nome": "Alimentação"
}
```

**Cadastro de transação**

`POST /api/v1/transacoes`

```json
{
  "descricao": "Salário mensal",
  "valor": 3500.00,
  "tipo": "RECEITA",
  "data": "2026-11-16",
  "categoriaId": 1
}
```

O `categoriaId` deve corresponder a uma categoria cadastrada pelo usuário autenticado.

## 📖 Documentação Swagger

Com a aplicação em execução, acesse:

`http://localhost:8080/swagger-ui/index.html`

A interface Swagger permite visualizar os endpoints, consultar os parâmetros, verificar os códigos HTTP documentados e realizar requisições de teste.

Para testar endpoints protegidos, utilize o botão **Authorize** e informe um token JWT válido.

A especificação OpenAPI também pode ser consultada em:

`http://localhost:8080/v3/api-docs`

## 🧪 Testes automatizados

O projeto utiliza JUnit e Mockito para verificar o comportamento de entidades, serviços e regras de negócio.

Para executar os testes:

```bash
mvn clean test
```

Os testes incluem verificações relacionadas a autenticação, categorias, transações, validações financeiras e componentes da versão original.

## 🛡️ Regras de negócio e segurança

- Cada usuário acessa suas próprias categorias e transações.
- Senhas são armazenadas utilizando BCrypt.
- Endpoints financeiros exigem autenticação JWT.
- Transações devem possuir valores positivos.
- Despesas não podem ultrapassar o saldo disponível.
- Atualizações e exclusões de transações respeitam as regras de saldo.
- Requisições inválidas recebem respostas HTTP apropriadas.
- Credenciais de banco de dados e chaves JWT são configuradas por variáveis de ambiente.

## 📈 Evolução do projeto

**Versão 1.0 — Aplicação desktop**

Desenvolvida em Java, com interface JavaFX e persistência local utilizando SQLite. O foco inicial foi consolidar conceitos de POO, interfaces gráficas e organização de código.

**Versão 2.0 — API REST**

Evolução para Spring Boot e PostgreSQL, incorporando arquitetura em camadas, autenticação JWT, persistência com JPA, endpoints REST, relatórios e documentação Swagger.

## 👨‍💻 Autor

**Arthur Siebra**

Projeto desenvolvido como atividade prática e de aperfeiçoamento durante o curso avançado de Java do **Capacita Brasil**.

GitHub: [ArthurSiebra](https://github.com/ArthurSiebra)

---

*PFinanças 2.0 — Projeto acadêmico de gerenciamento financeiro pessoal.*