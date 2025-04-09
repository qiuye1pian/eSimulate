package org.esimulate.core.application;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.simulate.EnvironmentDto;
import org.esimulate.core.pojo.simulate.LoadDto;
import org.esimulate.core.pojo.simulate.ModelDto;
import org.esimulate.core.pojo.simulate.SimulateConfigDto;
import org.esimulate.core.pso.simulator.Simulator;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.esimulate.core.pso.simulator.result.SimulateResult;
import org.esimulate.core.service.device.*;
import org.esimulate.core.service.environment.*;
import org.esimulate.core.service.load.ElectricLoadSchemeService;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SimulateApplication {

    @Autowired
    ElectricLoadSchemeService electricLoadSchemeService;

    @Autowired
    ThermalLoadSchemeService thermalLoadSchemeService;

    @Autowired
    WindPowerService windPowerService;

    @Autowired
    SolarPowerService solarPowerService;

    @Autowired
    HydroPowerPlantModelService hydroPowerPlantModelService;

    @Autowired
    ThermalPowerService thermalPowerService;

    @Autowired
    BatteryService batteryService;

    @Autowired
    GasBoilerService gasBoilerService;

    @Autowired
    WindSpeedSchemeService windSpeedSchemeService;

    @Autowired
    WaterSpeedSchemeService waterSpeedSchemeService;

    @Autowired
    TemperatureSchemeService temperatureSchemeService;

    @Autowired
    SunlightIrradianceSchemeService sunlightIrradianceSchemeService;

    @Autowired
    GasSchemeService gasSchemeService;

    @Autowired
    ThermalSaverService thermalSaverService;

    @Autowired
    GridService gridService;

    public SimulateResult doSimulate(SimulateConfigDto simulateConfigDto) {
        log.info("开始仿真");
        long startTotal = System.currentTimeMillis();

        log.info("加载负荷数据");
        long startLoadData = System.currentTimeMillis();
        List<LoadData> loadDataList = simulateConfigDto.getLoadDtoList().stream()
                .parallel()
                .map(this::readLoadData)
                .collect(Collectors.toList());
        long endLoadData = System.currentTimeMillis();
        log.info("加载负荷数据耗时： {} ms", (endLoadData - startLoadData));

        log.info("加载环境数据");
        long startEnvData = System.currentTimeMillis();
        List<EnvironmentData> environmentDataList = simulateConfigDto.getEnvironmentDtoList().stream()
                .parallel()
                .map(this::readEnvironmentData)
                .collect(Collectors.toList());
        long endEnvData = System.currentTimeMillis();
        log.info("加载环境数据耗时： {} ms", (endEnvData - startEnvData));

        log.info("加载模型");
        long startModelData = System.currentTimeMillis();
        List<Device> deviceList = simulateConfigDto.getModelDtoList().stream()
                .parallel()
                .map(this::readModelData)
                .collect(Collectors.toList());
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

    private EnvironmentData readEnvironmentData(EnvironmentDto environmentDto) {
        switch (environmentDto.getEnvironmentTypeEnum()) {
            case WindSpeed:
                return windSpeedSchemeService.findWithValuesById(environmentDto.getId());

            case WaterSpeed:
                return waterSpeedSchemeService.findWithValuesById(environmentDto.getId());

            case Temperature:
                return temperatureSchemeService.findWithValuesById(environmentDto.getId());

            case Sunlight:
                return sunlightIrradianceSchemeService.findWithValuesById(environmentDto.getId());

            case Gas:
                return gasSchemeService.findWithValuesById(environmentDto.getId());

            default:
                log.error("未识别的环境类型: {}", environmentDto.getEnvironmentTypeEnum());
                throw new IllegalArgumentException("未知环境数据类型: " + environmentDto.getEnvironmentTypeEnum());
        }
    }

    private Device readModelData(ModelDto modelDto) {
        Device device;
        switch (modelDto.getModelTypeEnum()) {
            case WindPower:
                device = windPowerService.findById(modelDto.getId());
                break;

            case SolarPower:
                device = solarPowerService.findById(modelDto.getId());
                break;

            case HydroPower:
                device = hydroPowerPlantModelService.findById(modelDto.getId());
                break;

            case ThermalPower:
                device = thermalPowerService.findById(modelDto.getId());
                break;

            case Battery:
                device = batteryService.findById(modelDto.getId());
                break;

            case GasBoiler:
                device = gasBoilerService.findById(modelDto.getId());
                break;

            case ThermalSaver:
                device = thermalSaverService.findById(modelDto.getId());
                break;

            case Grid:
                device = gridService.findById(modelDto.getId());
                break;

            default:
                log.error("未识别的模型类型: {}", modelDto.getModelTypeEnum());
                throw new IllegalArgumentException("未知模型类型: " + modelDto.getModelTypeEnum());
        }
        device.setQuantity(modelDto.getQuantity());
        return device;
    }

    private LoadData readLoadData(LoadDto loadDto) {
        switch (loadDto.getLoadTypeEnum()) {
            case ElectricLoad:
                return electricLoadSchemeService.findWithValuesById(loadDto.getId());
            case ThermalLoad:
                return thermalLoadSchemeService.findWithValuesById(loadDto.getId());
            default:
                log.error("未识别的负荷类型: {}, ID={}", loadDto.getLoadTypeEnum(), loadDto.getId());
                throw new IllegalArgumentException("未知负荷类型: " + loadDto.getLoadTypeEnum() +
                        "，ID=" + loadDto.getId());
        }

    }
}
