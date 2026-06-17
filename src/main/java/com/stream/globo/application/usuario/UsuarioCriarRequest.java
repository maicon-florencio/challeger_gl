package com.stream.globo.application.usuario;

import jakarta.validation.constraints.NotBlank;

public record UsuarioCriarRequest(
        @NotBlank(message = "Não pode ser nulo ou vazio.")
        String nome,
        @NotBlank(message = "Não pode ser nulo ou vazio.")
        String email,
        @NotBlank(message = "Não pode ser nulo ou vazio.")
        String cpf) {
}
