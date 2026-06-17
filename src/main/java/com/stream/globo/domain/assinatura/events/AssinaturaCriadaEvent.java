package com.stream.globo.domain.assinatura.events;

import com.stream.globo.shared.DomainEvent;
import java.time.LocalDateTime;

/**
 * Evento disparado quando uma nova assinatura é criada.
 */
public class AssinaturaCriadaEvent implements DomainEvent {

    private final String aggregateId;
    private final String planoAggregateId;
    private final String usuarioAggregateId;
    private final LocalDateTime ocorridoEm;

    public AssinaturaCriadaEvent(String aggregateId, String planoAggregateId, String usuarioAggregateId) {
        this.aggregateId = aggregateId;
        this.planoAggregateId = planoAggregateId;
        this.usuarioAggregateId = usuarioAggregateId;
        this.ocorridoEm = LocalDateTime.now();
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public LocalDateTime getOcorridoEm() {
        return ocorridoEm;
    }

    public String getPlanoAggregateId() {
        return planoAggregateId;
    }

    public String getUsuarioAggregateId() {
        return usuarioAggregateId;
    }
}

