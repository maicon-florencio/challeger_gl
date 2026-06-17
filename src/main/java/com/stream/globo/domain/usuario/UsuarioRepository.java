package com.stream.globo.domain.usuario;

import com.stream.globo.domain.usuario.entity.Usuario;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByAggregateId(@NonNull String aggregateId);
    boolean existsByEmail(@NonNull String email);
    boolean existsByCpf(@NonNull String cpf);
}
