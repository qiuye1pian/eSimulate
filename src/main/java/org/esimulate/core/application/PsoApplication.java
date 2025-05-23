package org.esimulate.core.application;


import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.simulate.ModelLoadDto;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.pso.PsoGlobal;
import org.esimulate.core.pso.particle.Particle2;
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
public class PsoApplication {

    @Autowired
    LoadDataComponent loadDataComponent;

    @Autowired
    EnvironmentDataComponent environmentDataComponent;

    @Autowired
    DeviceComponent deviceComponent;

    public OptimizeResult doPso(PsoConfig psoConfig) {
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
        List<Device> deviceList = deviceComponent.getDeviceList(psoConfig.getModelDimensionDtoList().stream().map(x -> (ModelLoadDto) x).collect(Collectors.toList()));
        long endModelData = System.currentTimeMillis();
        log.info("加载模型耗时： {} ms", (endModelData - startModelData));

        PsoGlobal psoGlobal = new PsoGlobal();

        List<Particle2> particleList = new ArrayList<>();
        for (int i = 0; i <= psoConfig.getParticleCount(); i++) {
            particleList.add(new Particle2(loadDataList, environmentDataList, deviceList));
        }

        psoGlobal.setParticleList(particleList);

        for(int i = 0; i <= psoConfig.getMaxIterations();i++){

        }

        return new OptimizeResult();
    }

}
