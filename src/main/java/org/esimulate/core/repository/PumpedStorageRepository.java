package org.esimulate.core.repository;

import org.esimulate.core.model.device.PumpedStorageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PumpedStorageRepository extends JpaRepository<PumpedStorageModel, Long> {

    Page<PumpedStorageModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<PumpedStorageModel> findByModelName(String modelName);

}
