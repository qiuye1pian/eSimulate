package org.esimulate.core.application;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.simulate.ModelLoadDto;
import org.esimulate.core.pojo.simulate.SimulateConfigDto;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.simulator.Simulator;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SimulateApplication {

    @Autowired
    LoadDataComponent loadDataComponent;

    @Autowired
    EnvironmentDataComponent environmentDataComponent;

    @Autowired
    DeviceComponent deviceComponent;

    public SimulateResult doSimulate(SimulateConfigDto simulateConfigDto) {
        log.info("开始仿真");
        long startTotal = System.currentTimeMillis();

        log.info("加载负荷数据");
        long startLoadData = System.currentTimeMillis();
        List<LoadData> loadDataList = loadDataComponent.getLoadData(simulateConfigDto.getLoadDtoList());
        long endLoadData = System.currentTimeMillis();
        log.info("加载负荷数据耗时： {} ms", (endLoadData - startLoadData));

        log.info("加载环境数据");
        long startEnvData = System.currentTimeMillis();
        List<EnvironmentData> environmentDataList = environmentDataComponent.getEnvironmentData(simulateConfigDto.getEnvironmentDtoList());
        long endEnvData = System.currentTimeMillis();
        log.info("加载环境数据耗时： {} ms", (endEnvData - startEnvData));

        log.info("加载模型");
        long startModelData = System.currentTimeMillis();
        List<Device> deviceList = deviceComponent.getDeviceList(simulateConfigDto.getModelDtoList().stream().map(x -> (ModelLoadDto) x).collect(Collectors.toList()));
        long endModelData = System.currentTimeMillis();
        log.info("加载模型耗时： {} ms", (endModelData - startModelData));

        log.info("进入仿真计算");
        long startSimulation = System.currentTimeMillis();
        SimulateResult simulate = Simulator.simulate(loadDataList, environmentDataList, deviceList, new ArrayList<>());
        long endSimulation = System.currentTimeMillis();
        log.info("仿真计算耗时： {} ms", (endSimulation - startSimulation));

        log.info("仿真结束");
        long endTotal = System.currentTimeMillis();
        log.info("总耗时： {} ms", (endTotal - startTotal));

        return simulate;
    }


}
