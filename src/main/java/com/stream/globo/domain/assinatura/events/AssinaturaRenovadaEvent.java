package com.stream.globo.domain.assinatura.events;

import com.stream.globo.shared.DomainEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evento disparado quando uma assinatura é renovada.
 */
public class AssinaturaRenovadaEvent implements DomainEvent {

    private final String aggregateId;
    private final LocalDate novaDataFim;
    private final LocalDateTime ocorridoEm;

    public AssinaturaRenovadaEvent(String aggregateId, LocalDate novaDataFim) {
        this.aggregateId = aggregateId;
        this.novaDataFim = novaDataFim;
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

    public LocalDate getNovaDataFim() {
        return novaDataFim;
    }
}

