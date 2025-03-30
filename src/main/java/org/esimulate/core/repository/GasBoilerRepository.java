package org.esimulate.core.repository;

import org.esimulate.core.model.device.GasBoilerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GasBoilerRepository extends JpaRepository<GasBoilerModel, Long> {

    Page<GasBoilerModel> findByModelNameContaining(String modelName, Pageable pageable);

}
