CREATE TABLE IF NOT EXISTS assinatura (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL UNIQUE,

    -- Referências lógicas aos outros Aggregates de outros microsserviços.
    -- NÃO há FOREIGN KEY física aqui, pois os dados estão em bancos distintos.
    plano_aggregate_id VARCHAR(36) NOT NULL,
    usuario_aggregate_id VARCHAR(36) NOT NULL,

    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    criacao_dt TIMESTAMP NOT NULL, -- Mapeia perfeitamente para LocalDateTime
    status VARCHAR(20) NOT NULL CHECK (status IN ('ATIVO', 'INATIVO')),

    CONSTRAINT assinatura_datas_validas CHECK (data_fim >= data_inicio)
);

-- Índices otimizados para as buscas de negócio e conciliação de dados
CREATE INDEX idx_assinatura_aggregate_id ON assinatura(aggregate_id);
CREATE INDEX idx_assinatura_plano_id ON assinatura(plano_aggregate_id);
CREATE INDEX idx_assinatura_usuario_id ON assinatura(usuario_aggregate_id);
CREATE INDEX idx_assinatura_status ON assinatura(status);
CREATE INDEX idx_assinatura_periodo ON assinatura(data_inicio, data_fim);
CREATE INDEX idx_assinatura_usuario_status ON assinatura(usuario_aggregate_id, status);

COMMENT ON TABLE assinatura IS 'Agregado Raiz de Assinatura - Base Isolada do Microsserviço de Assinaturas';
COMMENT ON COLUMN assinatura.id IS 'Chave primária física interna (BIGSERIAL)';
COMMENT ON COLUMN assinatura.aggregate_id IS 'UUID de Domínio desta assinatura';
COMMENT ON COLUMN assinatura.plano_aggregate_id IS 'ID lógico de referência do Plano';
COMMENT ON COLUMN assinatura.usuario_aggregate_id IS 'ID lógico de referência do Usuário';
COMMENT ON COLUMN assinatura.criacao_dt IS 'Data de criação gravada como LocalDateTime';