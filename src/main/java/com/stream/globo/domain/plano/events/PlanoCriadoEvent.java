package com.stream.globo.domain.plano.events;

import com.stream.globo.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento disparado quando um novo plano é criado.
 */
public class PlanoCriadoEvent implements DomainEvent {
    
    private final String aggregateId;
    private final String planoType;
    private final BigDecimal valor;
    private final Integer acessosSimultaneos;
    private final LocalDateTime ocorridoEm;
    
    public PlanoCriadoEvent(String aggregateId, String planoType, BigDecimal valor, Integer acessosSimultaneos) {
        this.aggregateId = aggregateId;
        this.planoType = planoType;
        this.valor = valor;
        this.acessosSimultaneos = acessosSimultaneos;
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

    public String getPlanoType() {
        return planoType;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public Integer getAcessosSimultaneos() {
        return acessosSimultaneos;
    }
}

