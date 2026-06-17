package com.stream.globo.application.usuario;

import com.stream.globo.shared.StatusEnum;

import java.time.LocalDateTime;

public record UsuarioResponse(String aggregateId, String nome, String cpf,  String email, LocalDateTime criacaoDt, StatusEnum status) {
}
