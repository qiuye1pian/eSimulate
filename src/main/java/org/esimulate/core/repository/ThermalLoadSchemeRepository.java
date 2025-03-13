package org.esimulate.core.repository;

import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThermalLoadSchemeRepository extends JpaRepository<ThermalLoadScheme, Long> {

    Page<ThermalLoadScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);



}