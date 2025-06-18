package org.esimulate.core.application;


import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.enums.TaskStateEnum;
import org.esimulate.core.model.task.OptimizeTask;
import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.pso.OptimizeResultDto;
import org.esimulate.core.pojo.pso.SimulateSnapshot;
import org.esimulate.core.pojo.simulate.ModelLoadDto;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.pso.particle.Particle;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.esimulate.core.service.pso.OptimizeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PsoApplication {

    @Autowired
    LoadDataComponent loadDataComponent;

    @Autowired
    EnvironmentDataComponent environmentDataComponent;

    @Autowired
    DeviceComponent deviceComponent;

    @Autowired
    OptimizeTaskService optimizeTaskService;

    @Async("psoAsyncExecutor")
    public void doPso(OptimizeTask optimizeTask, PsoConfig psoConfig) {
        log.info("开始寻优");
        long startTotal = System.currentTimeMillis();

        log.info("加载负荷数据");
        long startLoadData = System.currentTimeMillis();

        List<LoadData> loadDataList = loadDataComponent.getLoadData(psoConfig.getLoadDtoList());
        long endLoadData = System.currentTimeMillis();
        log.info("加载负荷数据耗时： {} ms", (endLoadData - startLoadData));

        log.info("加载环境数据");
        long startEnvData = System.currentTimeMillis();
        List<EnvironmentData> environmentDataList = environmentDataComponent.getEnvironmentData(psoConfig.getEnvironmentDtoList());
        long endEnvData = System.currentTimeMillis();
        log.info("加载环境数据耗时： {} ms", (endEnvData - startEnvData));

        log.info("加载模型");
        long startModelData = System.currentTimeMillis();
        List<Device> deviceList = deviceComponent.getDeviceList(psoConfig.getModelDimensionDtoList().stream().map(x -> (ModelLoadDto) x).collect(Collectors.toList()));
        long endModelData = System.currentTimeMillis();
        log.info("加载模型耗时： {} ms", (endModelData - startModelData));

        log.info("开始PSO");
        long startPso = System.currentTimeMillis();

        OptimizeResult optimizeResult = new OptimizeResult();
        List<Particle> particleList = new ArrayList<>();

        //临时加的剪切长度的
        List<LoadData> shortLoadDataList = loadDataList.stream()
                .map(x -> x.cutOffMoreThan(24))
                .map(x -> (LoadData) x)
                .collect(Collectors.toList());
        List<EnvironmentData> shortEnvironmentDataList = environmentDataList.stream()
                .map(environmentData -> environmentData.cutOffMoreThan(24))
                .map(x -> (EnvironmentData) x)
                .collect(Collectors.toList());

        for (int i = 0; i < psoConfig.getParticleCount(); i++) {
            particleList.add(new Particle(i, psoConfig, shortLoadDataList, shortEnvironmentDataList, deviceList));
        }

        AtomicInteger atomicInteger = new AtomicInteger();

        particleList.stream()
                .map(Particle::getCurrentPosition)
                .findAny()
                .ifPresent(optimizeResult::setGlobalBestPosition);

        optimizeTask.setTaskState(TaskStateEnum.IN_PROGRESS);
        optimizeTaskService.save(optimizeTask);

        for (int i = 0; i < psoConfig.getMaxIterations(); i++) {
            List<SimulateSnapshot> simulateSnapshotList = particleList.stream()
                    .parallel()
                    .peek(particle -> particle.move(optimizeResult.getGlobalBestPosition()))
                    .map(Particle::runSimulate)
                    .peek(particle -> optimizeTask.setCurrentIteration(atomicInteger.incrementAndGet()))
                    .collect(Collectors.toList());
            optimizeResult.addSimulateSnapshotList(simulateSnapshotList);
            optimizeTaskService.save(optimizeTask);

            TaskStateEnum taskStateEnum = optimizeTaskService.findOptimizeTaskById(optimizeTask.getId())
                    .map(OptimizeTask::getTaskState)
                    .orElse(optimizeTask.getTaskState());
            log.debug("检查任务持久化状态, taskId={}, state={}", optimizeTask.getId(), taskStateEnum);
            // 如果任务已在外部请求取消，则停止执行
            if (taskStateEnum == TaskStateEnum.CANCELLED) {
                log.debug("任务取消, taskId={}", optimizeTask.getId());
                break;
            }
        }

        long endPso = System.currentTimeMillis();
        log.info("PSO耗时：{} ms", (endPso - startPso));

        log.info("寻优结束");
        long endTotal = System.currentTimeMillis();
        log.info("总耗时： {} ms", (endTotal - startTotal));

        optimizeTask.setOptimizeResult(optimizeResult);
        optimizeTask.setTaskState(TaskStateEnum.COMPLETED);
        optimizeTaskService.save(optimizeTask);
    }

    public Optional<OptimizeTask> getOptimizeTask(Long taskId) {
        return optimizeTaskService.findOptimizeTaskById(taskId);
    }

    public Optional<OptimizeResultDto> getOptimizeResult(Long taskId) {
        return optimizeTaskService.findOptimizeTaskById(taskId)
                .map(OptimizeResultDto::new);
    }

    public OptimizeTask createOptimizeTask(PsoConfig psoConfig) {
        return optimizeTaskService.createOptimizeTask(psoConfig);
    }

    public void cancelTask(Long taskId) {
        optimizeTaskService.cancelTask(taskId);
    }
}
