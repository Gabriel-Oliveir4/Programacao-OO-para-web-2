-- Banco V2 - ajustar itens_pedido para PK simples (id) + unicidade (pedido_id, produto_id)

ALTER TABLE itens_pedido ADD COLUMN IF NOT EXISTS id UUID;

UPDATE itens_pedido
SET id = gen_random_uuid()
WHERE id IS NULL;

ALTER TABLE itens_pedido ALTER COLUMN id SET NOT NULL;

-- troca PK composta por PK simples
ALTER TABLE itens_pedido DROP CONSTRAINT IF EXISTS itens_pedido_pkey;
ALTER TABLE itens_pedido ADD CONSTRAINT itens_pedido_pkey PRIMARY KEY (id);

-- garante que o mesmo produto não se repita no mesmo pedido
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_itens_pedido_pedido_produto'
    ) THEN
        ALTER TABLE itens_pedido
            ADD CONSTRAINT uk_itens_pedido_pedido_produto UNIQUE (pedido_id, produto_id);
    END IF;
END$$;
