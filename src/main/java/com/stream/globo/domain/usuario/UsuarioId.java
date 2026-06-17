package com.stream.globo.domain.usuario;

import org.jspecify.annotations.NonNull;

public record UsuarioId(String aggregateId) {

    public static UsuarioId from(@NonNull final String aggregateId){
        return new UsuarioId(aggregateId);
    }

    public String getValue(){
        return this.aggregateId;
    }
}
