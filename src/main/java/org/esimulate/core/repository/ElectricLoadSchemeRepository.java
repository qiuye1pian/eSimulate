package org.esimulate.core.repository;

import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricLoadSchemeRepository extends JpaRepository<ElectricLoadScheme, Long> {

}
