CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    criacao_dt TIMESTAMP NOT NULL, -- Mapeia perfeitamente para LocalDateTime
    status VARCHAR(20) NOT NULL CHECK (status IN ('ATIVO', 'INATIVO')),

    CONSTRAINT usuario_cpf_formato CHECK (cpf ~ '^\d{11}$'),
    CONSTRAINT usuario_email_valido CHECK (email LIKE '%@%')
);

CREATE INDEX idx_usuario_aggregate_id ON usuario(aggregate_id);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_cpf ON usuario(cpf);
CREATE INDEX idx_usuario_status ON usuario(status);

COMMENT ON TABLE usuario IS 'Agregado Raiz de Usuário - Base Isolada do Microsserviço de Usuários';
COMMENT ON COLUMN usuario.id IS 'Chave primária física interna';
COMMENT ON COLUMN usuario.aggregate_id IS 'UUID de Domínio usado para integração externa';
COMMENT ON COLUMN usuario.criacao_dt IS 'Data de criação gravada como LocalDateTime';