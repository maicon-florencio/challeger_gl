package com.stream.globo.shared.exception.global;

import com.stream.globo.shared.exception.BussinessException;
import com.stream.globo.shared.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Set<ErrorResponseDTO.FieldErrorDTO> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponseDTO.FieldErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toSet());


        ErrorResponseDTO errorResponse = ErrorResponseDTO.criar(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação nos campos enviados.",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.criar(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Collections.EMPTY_SET
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.criar(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Collections.EMPTY_SET
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBussinessException(BussinessException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.criar(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ex.getMessage(),
                Collections.EMPTY_SET
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

}
