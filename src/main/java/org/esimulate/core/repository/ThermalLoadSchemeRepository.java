package org.esimulate.core.repository;

import lombok.NonNull;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThermalLoadSchemeRepository extends JpaRepository<ThermalLoadScheme, Long> {

    Page<ThermalLoadScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);


    Optional<ThermalLoadScheme> findBySchemeName(@NonNull String schemeName);

    @EntityGraph(attributePaths = "thermalLoadValues")
    Optional<ThermalLoadScheme> findWithValuesById(Long id);
}