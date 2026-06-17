package com.stream.globo.domain.usuario.events;

import com.stream.globo.shared.DomainEvent;
import java.time.LocalDateTime;

/**
 * Evento disparado quando um novo usuário é criado.
 */
public class UsuarioCriadoEvent implements DomainEvent {
    
    private final String aggregateId;
    private final String nome;
    private final String email;
    private final String cpf;
    private final LocalDateTime ocorridoEm;
    
    public UsuarioCriadoEvent(String aggregateId, String nome, String email, String cpf) {
        this.aggregateId = aggregateId;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
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

    public String getNome() {
        return nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getCpf() {
        return cpf;
    }
}

