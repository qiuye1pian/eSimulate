package org.esimulate.core.controller.pso;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.application.PsoApplication;
import org.esimulate.core.component.TaskRegistry;
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
import java.util.concurrent.*;

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
        CompletableFuture<OptimizeTask> optimizeResultFuture = psoApplication.doPso(optimizeTask, psoConfig);

        TaskRegistry.getInstance().register(taskId, optimizeResultFuture);

        optimizeResultFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                // 这里既捕获业务异常，也捕获取消时抛出的 CancellationException
                if (throwable instanceof CancellationException) {
                    // 任务被取消的逻辑
                    log.info("任务已被取消，taskId={}", taskId);
                } else {
                    // 执行出错的逻辑
                    log.error("子线程执行异常", throwable);
                }
            } else {
                // 正常完成的逻辑
                log.info("子线程执行成功，taskId={},结果={}", taskId, result.getTaskState());
            }
            TaskRegistry.getInstance().remove(taskId);
        });

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
        Future<OptimizeTask> optimizeTaskFuture = TaskRegistry.getInstance().get(optimizeFeedback.getTaskId());
        if (optimizeTaskFuture != null && optimizeTaskFuture.isDone()) {
            try {
                OptimizeTask optimizeTask = optimizeTaskFuture.get(5, TimeUnit.SECONDS);
                return Optional.of(new OptimizeResultDto(optimizeTask));
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取OptimizeTask异常, taskId:{}, map size:{}",
                        optimizeFeedback.getTaskId(), TaskRegistry.getInstance().getFutureSize(), e);
            } catch (TimeoutException e) {
                log.error("获取OptimizeTask超时, taskId:{}, map size:{}",
                        optimizeFeedback.getTaskId(), TaskRegistry.getInstance().getFutureSize(), e);
            }
        }

        return psoApplication.getOptimizeResult(optimizeFeedback.getTaskId());
    }

    @PostMapping("/cancelTask")
    public void cancelTask(@RequestBody OptimizeFeedback optimizeFeedback) {
        log.info("正在尝试取消任务, taskId:{}", optimizeFeedback.getTaskId());
        Future<OptimizeTask> optimizeTaskFuture = TaskRegistry.getInstance().get(optimizeFeedback.getTaskId());
        if (optimizeTaskFuture != null && !optimizeTaskFuture.isDone()) {
            boolean cancelled = optimizeTaskFuture.cancel(true);
            if (cancelled) {
                log.info("取消成功");
                psoApplication.cancelTask(optimizeFeedback.getTaskId());
            } else {
                log.warn("任务不存在或已完成，无法取消，taskId={}", optimizeFeedback.getTaskId());
            }
        }
    }

}
