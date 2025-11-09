-- Garante que a conta administrativa padrão esteja ativa e com a senha conhecida.
INSERT INTO usuarios (nome, email, senha, role, ativo, desativado_em)
VALUES (
    'Administrador',
    'admin@lacouro.com',
    '{bcrypt}$2b$10$AL7aOwy2JfOxr4se5ELBZuVomSaUi9XcXLhUf7EE7eLjW3T2jZ3ZK',
    'ADMIN',
    TRUE,
    NULL
)
ON CONFLICT (email) DO UPDATE
SET nome = EXCLUDED.nome,
    senha = EXCLUDED.senha,
    role = EXCLUDED.role,
    ativo = TRUE,
    desativado_em = NULL,
    atualizado_em = NOW();
