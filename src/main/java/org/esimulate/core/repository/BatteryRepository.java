package org.esimulate.core.repository;

import org.esimulate.core.model.device.BatteryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatteryRepository extends JpaRepository<BatteryModel, Long> {

    Page<BatteryModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<BatteryModel> findByModelName(String modelName);

}
