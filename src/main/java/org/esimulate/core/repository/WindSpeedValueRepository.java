package org.esimulate.core.repository;

import org.esimulate.core.model.environment.wind.WindSpeedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WindSpeedValueRepository extends JpaRepository<WindSpeedValue, Long> {


}
