package org.esimulate.core.repository;

import org.esimulate.core.model.environment.temperature.TemperatureValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureValueRepository extends JpaRepository<TemperatureValue, Long> {

}
