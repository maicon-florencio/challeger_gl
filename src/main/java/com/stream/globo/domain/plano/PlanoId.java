package com.stream.globo.domain.plano;

import org.jspecify.annotations.NonNull;

public record PlanoId(String aggregateId) {

    public static PlanoId from(@NonNull String aggregateId){
        return new PlanoId(aggregateId);
    }

    public String getValue() {
        return this.aggregateId;
    }
}
