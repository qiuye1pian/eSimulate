package org.esimulate.core.repository;

import lombok.NonNull;
import org.esimulate.core.model.environment.temperature.TemperatureScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemperatureSchemeRepository extends JpaRepository<TemperatureScheme, Long> {

    Page<TemperatureScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);

    Optional<TemperatureScheme> findBySchemeName(@NonNull String schemeName);

}
