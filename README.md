# La Couro API

## 1. Visão geral
A aplicação expõe serviços para cadastro de usuários, catálogo de produtos, controle de pedidos e movimentos de estoque de uma loja de tapetes. O ponto de entrada do Spring Boot é `LaCouroApplication`, que carrega os componentes descritos abaixo.

## 2. Camadas da aplicação
A solução segue uma divisão em camadas para isolar responsabilidades.

### 2.1 API
- `api/ApiExceptionHandler`: intercepta exceções lançadas pelos controladores e serviços para responder com payload padronizado contendo data, caminho, código HTTP e detalhes de erro.

### 2.2 Configuração
- `config/OpenApiConfig`: registra a documentação OpenAPI/Swagger e o esquema de autenticação Bearer.
- `config/SecurityConfig`: monta a cadeia de filtros do Spring Security, libera os endpoints públicos (`/api/auth/**` e documentação), define sessão stateless e registra o codificador de senha.

### 2.3 Segurança
- `security/JwtAuthFilter`: filtro que lê o cabeçalho `Authorization`, valida o token JWT e popula o `SecurityContext` com o identificador do usuário.
- `security/AuthUtils`: utilitário estático para recuperar o `UUID` do usuário autenticado a partir do contexto de segurança.
- `service/JwtService`: gera e valida tokens JWT assinados com chave simétrica.

### 2.4 Modelos
- `model/entity`: entidades JPA que representam as tabelas do banco (`Usuario`, `Produto`, `Pedido`, `ItemPedido`, `MovimentoEstoque`) e enums auxiliares (`Role`, `StatusPedido`, `TipoMovimento`). Cada entidade mantém campos de auditoria (`criadoPorId`, `atualizadoPorId`, `criadoEm`, etc.).
- `model/repo`: interfaces `JpaRepository` usadas pelos serviços para acessar os dados. Destacam-se `ProdutoRepository` (consulta produtos ativos), `PedidoRepository` (busca pedidos de um usuário com filtro de visibilidade) e `UsuarioRepository` (consulta por e-mail e status ativo).

### 2.5 DTOs (Data Transfer Objects)
- `dto/auth`: contratos para autenticação e registro (`LoginRequest`, `LoginResponse`, `RegisterRequest`) com validações declarativas.
- `dto/produto`: payloads de criação e atualização de produtos (`ProdutoCreateRequest`, `ProdutoUpdateRequest`).
- `dto/pedido`: objetos para criação de pedido e registro de pagamento (`CriarPedidoRequest`, `PagamentoRequest`). Os controladores convertem esses dados em estruturas internas dos serviços.

### 2.6 Serviços
- `service/AuthService`: autentica usuários verificando a senha codificada e emite o token JWT.
- `service/UsuarioService`: registra clientes e administradores, aplica regras de autorização para criação de administradores e permite desativar contas.
- `service/ProdutoService`: cuida do ciclo de vida do produto, persistindo quem criou/atualizou e controlando visibilidade.
- `service/EstoqueService`: ajusta o estoque com lançamentos de entrada e saída, valida quantidades e grava `MovimentoEstoque` associado.
- `service/PedidoService`: cria pedidos, calcula o valor total, aciona o estoque ao receber pagamento e controla cancelamentos.

### 2.7 Controladores REST
Os controladores expõem os endpoints sob `/api/**` e delegam aos serviços as regras de negócio.
- `controller/AuthController`: login e registro de clientes.
- `controller/UsuarioController`: operações administrativas de usuários (listagem, busca, criação de administradores, desativação).
- `controller/ProdutoController`: catálogo de produtos (listar ativos, CRUD limitado e alteração de visibilidade).
- `controller/EstoqueController`: lançamentos manuais de entrada e saída vinculados ao usuário autenticado.
- `controller/PedidoController`: fluxo de pedidos (listagem, criação, pagamento, cancelamento) convertendo DTOs em `PedidoService.ItemInput`.

## 3. Banco de dados
O projeto utiliza PostgreSQL e migrações Flyway. O arquivo `application.yml` define a conexão, habilita `ddl-auto: none` e aponta as migrações para `classpath:db/migration`.

As migrações:
- `V1__init.sql`: cria as tabelas `usuarios`, `produtos`, `pedidos`, `itens_pedido`, `movimentos_estoque` e insere um administrador padrão.
- `V2__itens_pedido_add_id.sql`: adiciona chave primária simples à tabela `itens_pedido` e mantém produto por pedido.
- `V3__sync_admin_password.sql`: garante que o usuário `admin@lacouro.com` exista, esteja ativo e utilize a senha padrão `Admin@123`.

## 4. Fluxo principal
1. Um cliente ou administrador autentica-se via `AuthController`, que aciona `AuthService` e `JwtService` para gerar o token.
2. O token é enviado nas requisições seguintes. `JwtAuthFilter` valida o token, carrega o usuário pelo repositório e grava seu `UUID` no contexto.
3. Controladores usam `AuthUtils` para obter o usuário atual, validar permissões e delegar para os serviços.
4. Os serviços manipulam as entidades através dos repositórios e registram os efeitos no banco. Erros são capturados pelo `ApiExceptionHandler`, que retorna respostas consistentes ao cliente.

## 5. Endpoints para teste no Postman

Todas as rotas (exceto autenticação) exigem o header `Authorization: Bearer <token>` obtido após o login. Parâmetros marcados como opcionais possuem valor padrão quando omitidos.

### 5.1 Autenticação (`/api/auth`)

| Método | Caminho              | Autorização | Corpo de requisição | Resposta esperada |
|--------|----------------------|-------------|---------------------|-------------------|
| POST   | `/api/auth/login`    | Público     | `{ "email": "user@dominio.com", "senha": "Senha@123" }` | `{ "token": "<JWT>" }` |
| POST   | `/api/auth/register` | Público     | `{ "nome": "Cliente", "email": "user@dominio.com", "senha": "Senha@123" }` | `{ "id": "<uuid>", "nome": "Cliente", "email": "user@dominio.com", "role": "CLIENTE" }` |

> **Dica:** após executar as migrações, já existe um administrador pronto para uso no Postman com `email: admin@lacouro.com` e `senha: Admin@123`.

### 5.2 Produtos (`/api/produtos`)

| Método | Caminho                             | Autorização | Corpo / parâmetros | Observações |
|--------|-------------------------------------|-------------|--------------------|-------------|
| GET    | `/api/produtos`                     | JWT         | —                  | Lista apenas produtos ativos. |
| GET    | `/api/produtos/{id}`                | JWT         | —                  | Retorna o produto pelo identificador. |
| POST   | `/api/produtos`                     | JWT (ADMIN) | `{ "nome": "Tapete", "preco": 99.9, "quantidadeEstoque": 10, ... }` | Criação restrita a administradores. |
| PUT    | `/api/produtos/{id}`                | JWT (ADMIN) | `{ "nome": "Tapete", "preco": 120.0, "ativo": true, ... }` | Atualiza dados gerais do produto. |
| PATCH  | `/api/produtos/{id}/visibilidade`   | JWT (ADMIN) | `?ativo=true/false` | Alterna a flag de visibilidade. |

### 5.3 Pedidos (`/api/pedidos`)

| Método | Caminho                                   | Autorização          | Corpo / parâmetros | Observações |
|--------|-------------------------------------------|----------------------|--------------------|-------------|
| GET    | `/api/pedidos`                            | JWT (ADMIN)          | —                  | Lista todos os pedidos. |
| GET    | `/api/pedidos/usuario/{usuarioId}`        | JWT (ADMIN ou dono)  | `?visiveis=true`   | Clientes só veem seus próprios pedidos. |
| GET    | `/api/pedidos/{id}`                       | JWT (ADMIN ou dono)  | —                  | Retorna um pedido específico. |
| POST   | `/api/pedidos`                            | JWT (ADMIN ou dono)  | `{ "usuarioId": "<uuid>", "itens": [{ "produtoId": "<uuid>", "quantidade": 2 }] }` | Usuário autenticado deve coincidir com `usuarioId` salvo se administrador. |
| POST   | `/api/pedidos/{id}/pagar`                 | JWT                  | `{ "metodo": "PIX", "referencia": "chave" }` | Debita estoque e altera status para pago. |
| POST   | `/api/pedidos/{id}/cancelar`              | JWT                  | —                  | Valida permissões e marca pedido como cancelado. |

### 5.4 Estoque (`/api/estoque`)

| Método | Caminho                 | Autorização | Corpo / parâmetros | Observações |
|--------|-------------------------|-------------|--------------------|-------------|
| POST   | `/api/estoque/entrada`  | JWT         | `?produtoId=<uuid>&qtd=5` | Credita quantidade no estoque. |
| POST   | `/api/estoque/saida`    | JWT         | `?produtoId=<uuid>&qtd=5` | Debita quantidade no estoque. |

### 5.5 Usuários (`/api/usuarios`)

| Método | Caminho                         | Autorização | Corpo / parâmetros | Observações |
|--------|---------------------------------|-------------|--------------------|-------------|
| GET    | `/api/usuarios`                 | JWT (ADMIN) | `?ativo=true`      | Lista contas conforme status. |
| GET    | `/api/usuarios/{id}`            | JWT (ADMIN) | —                  | Busca usuário pelo identificador. |
| POST   | `/api/usuarios/registrar-admin` | JWT (ADMIN) | `{ "nome": "Admin", "email": "admin@dominio.com", "senha": "Senha@123" }` | Cria novo administrador. |
| DELETE | `/api/usuarios/{id}`            | JWT (ADMIN) | —                  | Desativa a conta informada. |

LINK VIDEO

https://youtu.be/DZxdKFjQxlU
