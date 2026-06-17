package com.stream.globo.shared;

import java.time.LocalDateTime;

/**
 * Interface base para todos os eventos de domínio.
 * Seguindo padrões de DDD, eventos capturam mudanças importantes no agregado.
 */
public interface DomainEvent {

    String getAggregateId();

    LocalDateTime getOcorridoEm();

}

