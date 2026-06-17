package com.stream.globo.domain.assinatura;

import com.stream.globo.domain.assinatura.entity.Assinatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssinaturaRepository extends JpaRepository<Assinatura, UUID> {
}
