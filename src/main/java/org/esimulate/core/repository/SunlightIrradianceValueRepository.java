package org.esimulate.core.repository;

import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SunlightIrradianceValueRepository extends JpaRepository<SunlightIrradianceValue, Long> {

}
