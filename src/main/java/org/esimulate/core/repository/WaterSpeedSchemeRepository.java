package org.esimulate.core.repository;

import lombok.NonNull;
import org.esimulate.core.model.environment.water.WaterSpeedScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaterSpeedSchemeRepository extends JpaRepository<WaterSpeedScheme, Long> {

    Page<WaterSpeedScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);


    Optional<WaterSpeedScheme> findBySchemeName(@NonNull String schemeName);

    @EntityGraph(attributePaths = "waterSpeedValues")
    Optional<WaterSpeedScheme> findWithValuesById(Long id);

}
