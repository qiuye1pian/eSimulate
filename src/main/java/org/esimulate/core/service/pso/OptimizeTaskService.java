package org.esimulate.core.service.pso;


import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.enums.TaskStateEnum;
import org.esimulate.core.model.task.OptimizeTask;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.repository.OptimizeTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class OptimizeTaskService {

    @Autowired
    OptimizeTaskRepository optimizeTaskRepository;

    @Transactional
    public OptimizeTask createOptimizeTask(PsoConfig psoConfig) {
        OptimizeTask optimizeTask = new OptimizeTask(psoConfig);
        return optimizeTaskRepository.save(optimizeTask);
    }

    @Transactional
    public OptimizeTask save(OptimizeTask optimizeTask) {
        return optimizeTaskRepository.save(optimizeTask);
    }

    @Transactional(readOnly = true)
    public Optional<OptimizeTask> findOptimizeTaskById(Long taskId) {
        return optimizeTaskRepository.findById(taskId);
    }

    @Transactional
    public void cancelTask(Long taskId) {
        Optional<OptimizeTask> byId = optimizeTaskRepository.findById(taskId);
        if (!byId.isPresent()) {
            log.error("没有找到任务,taskId:{}", taskId);
            return;
        }
        OptimizeTask optimizeTask = byId.get();
        optimizeTask.setTaskState(TaskStateEnum.CANCELLED);
        optimizeTaskRepository.save(optimizeTask);
    }
}
