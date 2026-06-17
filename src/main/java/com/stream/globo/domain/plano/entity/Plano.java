package com.stream.globo.domain.plano.entity;

import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.domain.plano.events.PlanoCriadoEvent;
import com.stream.globo.shared.DomainEvent;
import com.stream.globo.shared.StatusEnum;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * RootAggregate de Plano - Implementação DDD
 * Responsável por gerenciar o ciclo de vida do plano de assinatura.
 * Encapsula a lógica de negócio e publica eventos de domínio.
 */
@Entity
@Table(name = "plano")
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", unique = true, nullable = false, updatable = false)
    private String aggregateId;

    @Column(name = "plano_type", unique = true, nullable = false, updatable = false)
    private String planoType;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "criacao_dt", nullable = false, updatable = false)
    private LocalDateTime criacaoDt;

    @Column(nullable = false)
    private Integer acessosSimultaneos;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Plano() {
    }

    // ==================== Métodos de Acesso ====================

    public PlanoId getPlanoId() {
        return PlanoId.from(this.aggregateId);
    }

    public String getPlanoType() {
        return planoType;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getCriacaoDt() {
        return criacaoDt;
    }

    public Integer getAcessosSimultaneos() {
        return acessosSimultaneos;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    public void limparDomainEvents() {
        this.domainEvents.clear();
    }

    // ==================== Validações de Invariantes ====================

    private static void validarDados(@NonNull String planoType, @NonNull BigDecimal valor, @NonNull Integer acessosSimultaneos) {
        validarPlanoType(planoType);
        validarValor(valor);
        validarAcessosSimultaneos(acessosSimultaneos);
    }

    private static void validarPlanoType(@NonNull String planoType) {
        if (planoType.isBlank()) {
            throw new IllegalArgumentException("Tipo de plano não pode estar vazio");
        }
        if (planoType.length() < 2) {
            throw new IllegalArgumentException("Tipo de plano deve ter no mínimo 2 caracteres");
        }
    }

    private static void validarValor(@NonNull BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do plano deve ser maior que zero");
        }
    }

    private static void validarAcessosSimultaneos(@NonNull Integer acessos) {
        if (acessos <= 0) {
            throw new IllegalArgumentException("Número de acessos simultâneos deve ser maior que zero");
        }
        if (acessos > 100) {
            throw new IllegalArgumentException("Número de acessos simultâneos não pode exceder 100");
        }
    }

    // ==================== Factory Method ====================

    /**
     * Cria um novo plano baseado nos dados fornecidos.
     * Dispara evento PlanoCriado.
     *
     * @param planoType tipo/nome do plano (BASIC, PREMIUM, ENTERPRISE)
     * @param valor valor mensal do plano
     * @param acessosSimultaneos número de acessos simultâneos permitidos
     * @return nova instância de Plano
     */
    public static Plano criar(@NonNull final String planoType,
                              @NonNull final BigDecimal valor,
                              @NonNull final Integer acessosSimultaneos) {
        validarDados(planoType, valor, acessosSimultaneos);

        final Plano plano = new Plano();
        plano.aggregateId = UUID.randomUUID().toString();
        plano.planoType = planoType;
        plano.valor = valor;
        plano.acessosSimultaneos = acessosSimultaneos;
        plano.criacaoDt = LocalDateTime.now();
        plano.status = StatusEnum.ATIVO;

        plano.domainEvents.add(new PlanoCriadoEvent(
            plano.aggregateId,
            plano.planoType,
            plano.valor,
            plano.acessosSimultaneos
        ));

        return plano;
    }

    // ==================== equals e hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plano plano = (Plano) o;
        return Objects.equals(aggregateId, plano.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId);
    }

    @Override
    public String toString() {
        return "Plano{" +
                "aggregateId='" + aggregateId + '\'' +
                ", planoType='" + planoType + '\'' +
                ", valor=" + valor +
                ", acessosSimultaneos=" + acessosSimultaneos +
                ", status=" + status +
                '}';
    }
}
