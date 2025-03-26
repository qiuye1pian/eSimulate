package org.esimulate.core.repository;

import org.esimulate.core.model.environment.gas.GasValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GasValueRepository extends JpaRepository<GasValue, Long> {

}
