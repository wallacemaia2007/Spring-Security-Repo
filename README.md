# 🔐 Spring Security — Autenticação com Sessão HTTP

> Projeto desenvolvido durante o Bootcamp **NTT Data** na **DIO**, explorando autenticação baseada em sessão com Spring Security, controle de acesso por roles e padrões de arquitetura limpa.

---

## 📚 Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Conceitos Aplicados](#conceitos-aplicados)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Autenticação e Segurança](#autenticação-e-segurança)
- [Endpoints](#endpoints)
- [Como Executar](#como-executar)

---

## Visão Geral

Esta aplicação demonstra o uso do **Spring Security** para proteger uma API REST com autenticação baseada em **sessão HTTP**, controle de acesso por **roles** (`ROLE_USER` e `ROLE_ADMIN`) e persistência de dados com **Spring Data JPA + H2**.

O domínio de negócio é simples — um CRUD de **Proposals** — mas toda a camada de segurança e a estrutura de código são o foco principal.

---

## Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.6 | Framework base |
| Spring Security | (incluso no Boot) | Autenticação e autorização |
| Spring Data JPA | (incluso no Boot) | Persistência de dados |
| H2 Database | runtime | Banco em memória para desenvolvimento |
| Lombok | (incluso no Boot) | Redução de boilerplate |
| Springdoc OpenAPI | 3.0.2 | Documentação Swagger |
| Spring Cloud OpenFeign | 2025.1.1 | Comunicação HTTP entre serviços |

---

## Conceitos Aplicados

### 1. `SecurityFilterChain`

O coração da configuração de segurança. Em vez de estender `WebSecurityConfigurerAdapter` (abordagem depreciada), usamos o bean `SecurityFilterChain`:

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http, RestUsernamePasswordAuthenticationFilter auth) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        .securityContext(context -> context.requireExplicitSave(false))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/auth/login", "/h2-console/**").permitAll()
            .anyRequest().authenticated())
        .addFilterAt(auth, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

**O que cada parte faz:**

- `.csrf(AbstractHttpConfigurer::disable)` — desabilita CSRF (adequado para APIs REST stateless ou com sessão controlada)
- `.headers(...sameOrigin())` — permite que o H2 Console seja exibido em iframe (necessário para o console funcionar)
- `.securityContext(...requireExplicitSave(false))` — o contexto de segurança é salvo automaticamente na sessão após autenticação
- `.addFilterAt(auth, ...)` — substitui o filtro padrão pelo nosso filtro customizado que aceita JSON

---

### 2. Autenticação via JSON — `UsernamePasswordAuthenticationFilter` customizado

O Spring Security, por padrão, espera credenciais via `application/x-www-form-urlencoded`. Para aceitar **JSON no corpo da requisição**, criamos um filtro customizado:

```java
@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
    LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    var token = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
    return getAuthenticationManager().authenticate(token);
}
```

O `AuthenticationManager` é obtido via `AuthenticationConfiguration`, sem precisar declará-lo manualmente como bean.

---

### 3. `UserDetailsService` com JPA

Implementamos a interface `UserDetailsService` para que o Spring Security saiba como carregar um usuário do banco:

```java
@Override
public UserDetails loadUserByUsername(String username) {
    return repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
}
```

A entidade `User` implementa `UserDetails` diretamente, expondo as authorities (roles) a partir do enum `UserRole`.

---

### 4. Autorização por Roles com `@PreAuthorize`

Com `@EnableMethodSecurity` habilitado na configuração, podemos usar anotações diretamente nos métodos dos controllers:

```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String getBrand() {
    return "Hello World ADMIN";
}
```

| Annotation | Comportamento |
|---|---|
| `@PreAuthorize("hasRole('ADMIN')")` | Permite apenas `ROLE_ADMIN` |
| `@PreAuthorize("hasRole('USER')")` | Permite apenas `ROLE_USER` |
| Sem anotação (`.anyRequest().authenticated()`) | Qualquer usuário autenticado |

---

### 5. `PasswordEncoder` com BCrypt

Senhas nunca são armazenadas em texto puro. O `BCryptPasswordEncoder` aplica um hash adaptativo com salt automático:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Os usuários de seed são criados no startup via `CommandLineRunner`:

```java
@Bean
CommandLineRunner run(UserRepository repository, PasswordEncoder encoder) {
    return args -> {
        if (repository.count() == 0) {
            // cria admin e user com senhas encodadas
        }
    };
}
```

---

### 6. Padrão Strategy para controle de acesso em listagens

O `ProposalController` decide **o que o usuário pode ver** com base na sua role, usando o padrão **Strategy**:

```java
private static AcessScope acessScope(UserRole userRole) {
    return userRole == UserRole.ROLE_ADMIN ? AcessScope.ALL : AcessScope.OWN;
}
```

- **ADMIN** → `AllStrategy` → busca todas as proposals
- **USER** → `OwnStrategy` → busca apenas as próprias proposals

A `Factory` mapeia o `AcessScope` para a `Strategy` correta via injeção de lista de beans:

```java
public Factory(List<Strategy> strategies) {
    this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(Strategy::getAcessScope, strategy -> strategy));
}
```

---

### 7. Arquitetura em Camadas (Domain-Driven)

O projeto separa claramente as responsabilidades:

```
proposal/
├── domain/
│   ├── entity/          # Proposal, Owner, OwnerId, ProposalId (objetos de valor)
│   └── repository/      # Interface do repositório (porta de saída)
├── application/
│   ├── usecase/         # CreateProposalUseCase, ListProposalUseCase
│   ├── input/           # DTOs de entrada da camada de aplicação
│   ├── output/          # DTOs de saída da camada de aplicação
│   └── (Strategy, Factory, AllStrategy, OwnStrategy)
└── infra/
    ├── http/            # Controllers, Requests, Responses
    └── persistence/     # Entidades JPA, Repositórios JPA
```

Isso garante que o domínio não depende de frameworks, e as implementações de infraestrutura são plugáveis.

---

## Endpoints

### Autenticação

```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "user",
    "password": "user"
}
```

> Retorna `200 OK` e cria uma sessão HTTP. O cookie `JSESSIONID` é usado nas próximas requisições.

---

### Proposals

| Método | Endpoint | Role necessária | Descrição |
|---|---|---|---|
| `POST` | `/proposals` | `ROLE_USER` | Cria uma nova proposal |
| `GET` | `/proposals` | `ROLE_ADMIN` ou `ROLE_USER` | Lista proposals (admin vê todas, user vê as próprias) |

### Demais

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/` | Autenticado | Retorna saudação com ID do usuário |
| `GET` | `/user` | `ROLE_USER` | Rota exclusiva para usuários |
| `GET` | `/admin` | `ROLE_ADMIN` | Rota exclusiva para admins |
| `GET` | `/h2-console/**` | Público | Console do banco H2 |

---

## Como Executar

**Pré-requisitos:** Java 21, Maven

```bash
# Clonar o repositório
git clone <url-do-repo>
cd security

# Executar
./mvnw spring-boot:run
```

**Usuários pré-cadastrados:**

| Username | Password | Role |
|---|---|---|
| `admin` | `admin` | `ROLE_ADMIN` |
| `user` | `user` | `ROLE_USER` |

**H2 Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:securitydb`
- Username: `sa`
- Password: *(vazio)*

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## 📖 Referências

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [DIO — Digital Innovation One](https://www.dio.me/)
- Bootcamp NTT Data — Java com Spring Boot
