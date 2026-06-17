package com.stream.globo.domain.assinatura.entity;

import com.stream.globo.domain.assinatura.AssinaturaId;
import com.stream.globo.domain.assinatura.events.AssinaturaCanceladaEvent;
import com.stream.globo.domain.assinatura.events.AssinaturaCriadaEvent;
import com.stream.globo.domain.assinatura.events.AssinaturaRenovadaEvent;
import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.shared.DomainEvent;
import com.stream.globo.shared.StatusEnum;
import com.stream.globo.domain.usuario.UsuarioId;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * RootAggregate de Assinatura - Implementação DDD
 * Responsável por gerenciar o ciclo de vida da assinatura e suas mudanças de estado.
 * Encapsula a lógica de negócio e publica eventos de domínio.
 */
@Entity
@Table(name = "assinatura")
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", unique = true, nullable = false, updatable = false)
    private String aggregateId;

    @Column(name = "plano_aggregate_id", nullable = false)
    private String planoAggregateId;

    @Column(name = "usuario_aggregate_id", nullable = false)
    private String usuarioAggregateId;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(name = "criacao_dt", nullable = false, updatable = false)
    private LocalDateTime criacaoDt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Assinatura() {
    }

    // ==================== Métodos de Acesso ====================

    public AssinaturaId getAssinaturaId() {
        return AssinaturaId.from(this.aggregateId);
    }

    public String getPlanoAggregateId() {
        return this.planoAggregateId;
    }

    public String getUsuarioAggregateId() {
        return this.usuarioAggregateId;
    }

    public LocalDate getDataInicio() {
        return this.dataInicio;
    }

    public LocalDate getDataFim() {
        return this.dataFim;
    }

    public LocalDateTime getCriacaoDt() {
        return this.criacaoDt;
    }

    public StatusEnum getStatus() {
        return this.status;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    public void limparDomainEvents() {
        this.domainEvents.clear();
    }

    // ==================== Lógica de Negócio ====================

    /**
     * Renova a assinatura por mais um período (mês).
     * Implementa regra de negócio: apenas assinaturas ATIVAS podem ser renovadas.
     *
     * @throws IllegalStateException se a assinatura não está ATIVA
     */
    public void renovar() {
        validarAssinaturaAtiva("Assinatura deve estar ATIVA para ser renovada");
        
        LocalDate novaDataFim = this.dataFim.plusMonths(1);
        this.dataFim = novaDataFim;
        
        this.domainEvents.add(new AssinaturaRenovadaEvent(this.aggregateId, novaDataFim));
    }

    /**
     * Cancela a assinatura.
     * Implementa regra de negócio: apenas assinaturas ATIVAS podem ser canceladas.
     *
     * @param motivo razão do cancelamento
     * @throws IllegalStateException se a assinatura não está ATIVA
     */
    public void cancelar(@NonNull final String motivo) {
        validarAssinaturaAtiva("Assinatura deve estar ATIVA para ser cancelada");
        validarMotivoNaoVazio(motivo);
        
        this.status = StatusEnum.INATIVO;
        
        this.domainEvents.add(new AssinaturaCanceladaEvent(this.aggregateId, motivo));
    }

    /**
     * Verifica se a assinatura está vencida.
     *
     * @return true se a data fim é anterior a hoje
     */
    public boolean estaVencida() {
        return LocalDate.now().isAfter(this.dataFim);
    }

    /**
     * Verifica se a assinatura está próxima do vencimento (15 dias ou menos).
     *
     * @return true se faltam 15 dias ou menos para vencer
     */
    public boolean estaProximaDoVencimento() {
        LocalDate dataAlerta = LocalDate.now().plusDays(15);
        return !LocalDate.now().isAfter(this.dataFim) && this.dataFim.isBefore(dataAlerta) || this.dataFim.isEqual(dataAlerta);
    }

    // ==================== Validações de Invariantes ====================

    private void validarAssinaturaAtiva(String mensagem) {
        if (this.status != StatusEnum.ATIVO) {
            throw new IllegalStateException(mensagem + ". Status atual: " + this.status);
        }
    }

    private void validarMotivoNaoVazio(@NonNull String motivo) {
        if (motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo do cancelamento não pode estar vazio");
        }
    }

    // ==================== Factory Method ====================

    /**
     * Cria uma nova assinatura baseada nas entidades de domínio informadas.
     * Dispara evento AssinaturaCriada.
     *
     * @param planoId ID do plano
     * @param usuarioId ID do usuário
     * @return nova instância de Assinatura
     */
    public static Assinatura criar(@NonNull final PlanoId planoId, @NonNull final UsuarioId usuarioId) {
        final Assinatura assinatura = new Assinatura();
        assinatura.aggregateId = UUID.randomUUID().toString();
        assinatura.planoAggregateId = planoId.getValue();
        assinatura.usuarioAggregateId = usuarioId.getValue();
        
        LocalDate hoje = LocalDate.now();
        assinatura.dataInicio = hoje;
        assinatura.dataFim = hoje.plusMonths(1);
        assinatura.criacaoDt = LocalDateTime.now();
        assinatura.status = StatusEnum.ATIVO;
        
        assinatura.domainEvents.add(new AssinaturaCriadaEvent(
            assinatura.aggregateId,
            assinatura.planoAggregateId,
            assinatura.usuarioAggregateId
        ));
        
        return assinatura;
    }

    // ==================== equals e hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Assinatura that = (Assinatura) o;
        return Objects.equals(aggregateId, that.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId);
    }

    @Override
    public String toString() {
        return "Assinatura{" +
                "aggregateId='" + aggregateId + '\'' +
                ", planoAggregateId='" + planoAggregateId + '\'' +
                ", usuarioAggregateId='" + usuarioAggregateId + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", status=" + status +
                '}';
    }
}
