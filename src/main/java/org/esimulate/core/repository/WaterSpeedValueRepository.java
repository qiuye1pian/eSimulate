package org.esimulate.core.repository;

import org.esimulate.core.model.environment.water.WaterSpeedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterSpeedValueRepository extends JpaRepository<WaterSpeedValue, Long> {
}
