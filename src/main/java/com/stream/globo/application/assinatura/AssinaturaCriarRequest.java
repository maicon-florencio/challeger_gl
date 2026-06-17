package com.stream.globo.application.assinatura;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AssinaturaCriarRequest(
        @JsonProperty("usuarioId")
        @NotNull(message = "Não pode ser nulo.")
        String usuarioAggregateId,
        @JsonProperty("planoId")
        @NotNull(message = "Não pode ser nulo.")
        String planoAggregateId
) {
}
