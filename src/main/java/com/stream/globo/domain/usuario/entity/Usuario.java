package com.stream.globo.domain.usuario.entity;

import com.stream.globo.domain.usuario.UsuarioId;
import com.stream.globo.shared.DomainEvent;
import com.stream.globo.shared.StatusEnum;
import com.stream.globo.shared.exception.BussinessException;
import com.stream.globo.domain.usuario.events.UsuarioCriadoEvent;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.*;

/**
 * RootAggregate de Usuario - Implementação DDD
 * Responsável por gerenciar o ciclo de vida do usuário e suas mudanças de estado.
 * Encapsula a lógica de negócio e publica eventos de domínio.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", unique = true, nullable = false, updatable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "criacao_dt", nullable = false, updatable = false)
    private LocalDateTime criacaoDt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Usuario() {
    }

    // ==================== Métodos de Acesso ====================

    public UsuarioId getUsuarioId() {
        return UsuarioId.from(this.aggregateId);
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

    public LocalDateTime getCriacaoDt() {
        return criacaoDt;
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

    // ==================== Factory Method ====================

    /**
     * Cria um novo usuário baseado nos dados fornecidos.
     * Dispara evento UsuarioCriado.
     *
     * @param nome nome do usuário
     * @param email email único do usuário
     * @param cpf CPF único do usuário
     * @return nova instância de Usuario
     */
    public static Usuario criar(@NonNull final String nome,
                                @NonNull final String email,
                                @NonNull final String cpf) {
        validarDados(nome, email, cpf);

        final Usuario usuario = new Usuario();
        usuario.aggregateId = UUID.randomUUID().toString();
        usuario.nome = nome;
        usuario.email = email;
        usuario.cpf = cpf;
        usuario.criacaoDt = LocalDateTime.now();
        usuario.status = StatusEnum.ATIVO;

        usuario.domainEvents.add(new UsuarioCriadoEvent(
            usuario.aggregateId,
            usuario.nome,
            usuario.email,
            usuario.cpf
        ));

        return usuario;
    }

    // ==================== Validações de Invariantes ====================

    private static void validarDados(@NonNull String nome, @NonNull String email, @NonNull String cpf) {
        validarNome(nome);
        validarEmail(email);
        validarCpf(cpf);
    }

    private static void validarNome(@NonNull String nome) {
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome do usuário não pode estar vazio");
        }
        if (nome.length() < 3) {
            throw new BussinessException("Nome do usuário deve ter no mínimo 3 caracteres");
        }
    }

    private static void validarEmail(@NonNull String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email não pode estar vazio");
        }
        if (!email.contains("@")) {
            throw new BussinessException("Email inválido");
        }
    }

    private static void validarCpf(@NonNull String cpf) {
        if (cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode estar vazio");
        }

        if (!cpf.matches("\\d+")) {
            throw new BussinessException("CPF deve conter apenas dígitos");
        }

        if (cpf.length() != 11) {
            throw new BussinessException("CPF deve conter 11 dígitos");
        }

    }

    // ==================== equals e hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(aggregateId, usuario.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "aggregateId='" + aggregateId + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", status=" + status +
                '}';
    }
}
