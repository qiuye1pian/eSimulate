package org.esimulate.core.repository;

import org.esimulate.core.model.device.ThermalPowerUnitModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThermalPowerUnitRepository extends JpaRepository<ThermalPowerUnitModel, Long> {

    Page<ThermalPowerUnitModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<ThermalPowerUnitModel> findByModelName(String modelName);

}
