package org.esimulate.core.repository;

import org.esimulate.core.model.device.ThermalPowerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThermalPowerRepository extends JpaRepository<ThermalPowerModel, Long> {

    Page<ThermalPowerModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<ThermalPowerModel> findByModelName(String modelName);

}
