package org.esimulate.core.controller.pso;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.application.PsoApplication;
import org.esimulate.core.model.task.OptimizeTask;
import org.esimulate.core.pojo.OptimizeTaskState;
import org.esimulate.core.pojo.pso.OptimizeFeedback;
import org.esimulate.core.pojo.pso.OptimizeResultDto;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/optimize/pso")
public class PSOController {

    @Autowired
    PsoApplication psoApplication;

    @PostMapping("/do")
    public OptimizeFeedback doOptimize(@RequestBody PsoConfig psoConfig) {

        OptimizeTask optimizeTask = psoApplication.createOptimizeTask(psoConfig);
        Long taskId = optimizeTask.getId();

        //发起doPso的线程
        psoApplication.doPso(optimizeTask, psoConfig);

        //返回OptimizeFeedback
        return new OptimizeFeedback(taskId);
    }

    @PostMapping("/getTaskState")
    public Optional<OptimizeTaskState> getTaskState(@RequestBody OptimizeFeedback optimizeFeedback) {
        //根据OptimizeFeedback里的id查找task
        return psoApplication.getOptimizeTask(optimizeFeedback.getTaskId()).map(OptimizeTaskState::new);
    }

    @PostMapping("/getResult")
    public Optional<OptimizeResultDto> getResult(@RequestBody OptimizeFeedback optimizeFeedback) {
        //根据OptimizeFeedback里的id查找task
        //返回Task
        return psoApplication.getOptimizeResult(optimizeFeedback.getTaskId());
    }

    @PostMapping("/cancelTask")
    public void cancelTask(@RequestBody OptimizeFeedback optimizeFeedback) {
        log.info("正在尝试取消任务, taskId:{}", optimizeFeedback.getTaskId());
        psoApplication.cancelTask(optimizeFeedback.getTaskId());
    }

}
