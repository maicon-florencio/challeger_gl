package com.stream.globo.domain.plano;

import com.stream.globo.domain.plano.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

    Optional<Plano> findByAggregateId(String aggregateId);
}
