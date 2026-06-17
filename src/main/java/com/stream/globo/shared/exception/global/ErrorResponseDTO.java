package com.stream.globo.shared.exception.global;

import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

public record ErrorResponseDTO(LocalDateTime timestamp, Integer status, String error, Set<FieldErrorDTO> fieldErrorDTOs) {

    public static ErrorResponseDTO criar(@NonNull final Integer status, @NonNull final String error, @NonNull final Set<FieldErrorDTO> fieldErrorDTO) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, fieldErrorDTO);
    }

    record FieldErrorDTO(String field, String message) {
    }
}
