package org.esimulate.core.repository;

import lombok.NonNull;
import org.esimulate.core.model.environment.wind.WindSpeedScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WindSpeedSchemeRepository extends JpaRepository<WindSpeedScheme, Long> {

    Page<WindSpeedScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);


    Optional<WindSpeedScheme> findBySchemeName(@NonNull String schemeName);

    @EntityGraph(attributePaths = "windSpeedValues")
    Optional<WindSpeedScheme> findWithValuesById(Long id);
}
