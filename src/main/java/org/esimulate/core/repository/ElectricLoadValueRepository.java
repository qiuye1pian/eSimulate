package org.esimulate.core.repository;

import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricLoadValueRepository extends JpaRepository<ElectricLoadValue, Long> {

}
