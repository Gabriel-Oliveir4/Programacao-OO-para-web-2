-- Banco V1

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE usuarios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','CLIENTE')),
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  desativado_em TIMESTAMP,
  criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMP
);

CREATE TABLE produtos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(120) NOT NULL,
  tamanho VARCHAR(40),
  cor VARCHAR(40),
  preco NUMERIC(12,2) NOT NULL CHECK (preco > 0),
  quantidade_estoque INT NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
  foto_url VARCHAR(255),
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  criado_por_id UUID REFERENCES usuarios(id),
  atualizado_por_id UUID REFERENCES usuarios(id),
  criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMP
);

CREATE TABLE pedidos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id UUID NOT NULL REFERENCES usuarios(id),
  status VARCHAR(20) NOT NULL CHECK (status IN ('CRIADO','PAGO','CANCELADO')),
  valor_total NUMERIC(12,2) NOT NULL DEFAULT 0,
  visivel BOOLEAN NOT NULL DEFAULT TRUE,
  pago_em TIMESTAMP,
  pagamento_metodo VARCHAR(20),
  pagamento_referencia VARCHAR(120),
  criado_por_id UUID REFERENCES usuarios(id),
  atualizado_por_id UUID REFERENCES usuarios(id),
  criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMP
);

CREATE TABLE itens_pedido (
  pedido_id UUID NOT NULL REFERENCES pedidos(id),
  produto_id UUID NOT NULL REFERENCES produtos(id),
  quantidade INT NOT NULL CHECK (quantidade > 0),
  preco_unitario NUMERIC(12,2) NOT NULL CHECK (preco_unitario > 0),
  criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (pedido_id, produto_id)
);

CREATE TABLE movimentos_estoque (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  produto_id UUID NOT NULL REFERENCES produtos(id),
  tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA','AJUSTE')),
  quantidade INT NOT NULL,
  motivo VARCHAR(100),
  realizado_por_id UUID REFERENCES usuarios(id),
  criado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO usuarios (nome, email, senha, role)
VALUES (
  'Administrador',
  'admin@lacouro.com',
  '{bcrypt}$2a$10$Jt9wJfHrEDKX6ZzYzHj5Te8tdDE0XfQEhMctvQfy3rmU2D3qMpeAu',
  'ADMIN'
);
