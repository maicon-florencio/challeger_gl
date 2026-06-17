package com.stream.globo.application.assinatura;

import java.time.LocalDate;

public record AssinaturaResponse(String aggregateId,
                                 String usuarioNome,
                                 String tipoPlano,
                                 LocalDate dataInicio,
                                 LocalDate dataExpiracao,
                                 String status
                                 ) {
}
