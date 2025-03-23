package org.esimulate.core.repository;

import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThermalLoadValueRepository extends JpaRepository<ThermalLoadValue, Long> {


}
