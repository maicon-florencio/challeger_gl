package com.stream.globo.domain.assinatura.events;

import com.stream.globo.shared.DomainEvent;
import java.time.LocalDateTime;

/**
 * Evento disparado quando uma assinatura é cancelada.
 */
public class AssinaturaCanceladaEvent implements DomainEvent {
    
    private final String aggregateId;
    private final LocalDateTime ocorridoEm;
    private final String motivo;
    
    public AssinaturaCanceladaEvent(String aggregateId, String motivo) {
        this.aggregateId = aggregateId;
        this.motivo = motivo;
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

    
    public String getMotivo() {
        return motivo;
    }
}

