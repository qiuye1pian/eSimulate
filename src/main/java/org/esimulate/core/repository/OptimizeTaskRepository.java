package org.esimulate.core.repository;

import org.esimulate.core.model.task.OptimizeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptimizeTaskRepository extends JpaRepository<OptimizeTask, Long> {

}
