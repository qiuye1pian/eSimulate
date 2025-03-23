package org.esimulate.core.repository;

import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectricLoadSchemeRepository extends JpaRepository<ElectricLoadScheme, Long> {

    Page<ElectricLoadScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);

    Optional<ElectricLoadScheme> findBySchemeName(String schemeName);

}
