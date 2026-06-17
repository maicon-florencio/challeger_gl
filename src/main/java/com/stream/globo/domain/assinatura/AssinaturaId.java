package com.stream.globo.domain.assinatura;

import jakarta.validation.constraints.NotNull;

public record AssinaturaId(String aggregateId) {

    public static AssinaturaId from(@NotNull String aggregateId) {
        return new AssinaturaId(aggregateId);
    }

    public String getValue(){
        return this.aggregateId;
    }
}
