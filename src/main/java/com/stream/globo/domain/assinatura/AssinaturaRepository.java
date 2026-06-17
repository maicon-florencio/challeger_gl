package com.stream.globo.domain.assinatura;

import com.stream.globo.domain.assinatura.entity.Assinatura;
import com.stream.globo.shared.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssinaturaRepository extends JpaRepository<Assinatura, UUID> {

    Optional<Assinatura> findByUsuarioAggregateIdAndStatus(String usuarioId, StatusEnum status);
}
