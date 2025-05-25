package org.esimulate.core.repository;

import org.esimulate.core.model.device.CogenerationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CogenerationRepository extends JpaRepository<CogenerationModel, Long> {

    Page<CogenerationModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<CogenerationModel> findByModelName(String modelName);

}
