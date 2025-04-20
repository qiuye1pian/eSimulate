package org.esimulate.core.repository;

import org.esimulate.core.model.device.HydroPowerPlantModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HydroPowerPlantRepository extends JpaRepository<HydroPowerPlantModel, Long> {

    Page<HydroPowerPlantModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<HydroPowerPlantModel> findByModelName(String modelName);

}
