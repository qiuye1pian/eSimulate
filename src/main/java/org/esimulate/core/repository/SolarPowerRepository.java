package org.esimulate.core.repository;

import org.esimulate.core.model.device.SolarPowerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolarPowerRepository extends JpaRepository<SolarPowerModel, Long> {

    Page<SolarPowerModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<SolarPowerModel> findByModelName(String modelName);

}
