package org.esimulate.core.repository;

import org.esimulate.core.model.device.WindPowerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WindPowerRepository extends JpaRepository<WindPowerModel, Long> {

    Page<WindPowerModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<WindPowerModel> findByModelName(String modelName);

}
