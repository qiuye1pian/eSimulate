package org.esimulate.core.repository;

import org.esimulate.core.model.device.ThermalSaverModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThermalSaverRepository extends JpaRepository<ThermalSaverModel, Long> {

    Page<ThermalSaverModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<ThermalSaverModel> findByModelName(String modelName);

}
