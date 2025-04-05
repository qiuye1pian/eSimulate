package org.esimulate.core.repository;

import org.esimulate.core.model.device.GridModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GridRepository extends JpaRepository<GridModel, Long> {

    Page<GridModel> findByModelNameContaining(String modelName, Pageable pageable);

    Optional<GridModel> findByModelName(String modelName);

}
