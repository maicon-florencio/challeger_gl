CREATE TABLE IF NOT EXISTS plano (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL UNIQUE,
    plano_type VARCHAR(50) NOT NULL UNIQUE,
    valor NUMERIC(10,2) NOT NULL,
    criacao_dt TIMESTAMP NOT NULL, -- Mapeia perfeitamente para LocalDateTime
    acessos_simultaneos INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ATIVO', 'INATIVO')),

    CONSTRAINT plano_valor_positivo CHECK (valor > 0),
    CONSTRAINT plano_acessos_validos CHECK (acessos_simultaneos > 0 AND acessos_simultaneos <= 100),
    CONSTRAINT plano_type_valido CHECK (plano_type IN ('BASICO', 'PREMIUM', 'FAMILIA'))
);

CREATE INDEX idx_plano_aggregate_id ON plano(aggregate_id);
CREATE INDEX idx_plano_type ON plano(plano_type);
CREATE INDEX idx_plano_status ON plano(status);

COMMENT ON TABLE plano IS 'Agregado Raiz de Plano - Base Isolada do Microsserviço de Catálogo';
COMMENT ON COLUMN plano.id IS 'Chave primária física interna (BIGSERIAL)';
COMMENT ON COLUMN plano.aggregate_id IS 'UUID de Domínio usado para integração externa';
COMMENT ON COLUMN plano.criacao_dt IS 'Data de criação gravada como LocalDateTime (Sem timezone)';