package org.esimulate.core.application;


import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.simulate.ModelDto;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PsoApplication {

    @Autowired
    LoadDataComponent loadDataComponent;

    @Autowired
    EnvironmentDataComponent environmentDataComponent;

    @Autowired
    DeviceComponent deviceComponent;

    public OptimizeResult doPso(PsoConfig psoConfig){
        log.info("开始仿真");
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
        List<ModelDto> modelDtoList = psoConfig.getModelDtoList();
        List<Device> deviceList = deviceComponent.getDeviceList(modelDtoList);
        long endModelData = System.currentTimeMillis();
        log.info("加载模型耗时： {} ms", (endModelData - startModelData));

        return new OptimizeResult();
    }

}
