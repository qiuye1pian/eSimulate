package org.esimulate.core.application;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.device.Device;
import org.esimulate.core.pojo.simulate.EnvironmentDto;
import org.esimulate.core.pojo.simulate.LoadDto;
import org.esimulate.core.pojo.simulate.ModelDto;
import org.esimulate.core.pojo.simulate.SimulateConfigDto;
import org.esimulate.core.pso.simulator.Simulator;
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

    public SimulateResult doSimulate(SimulateConfigDto simulateConfigDto) {

        List<LoadData> loadDataList = simulateConfigDto.getLoadDtoList().stream()
                .parallel()
                .map(this::readLoadData)
                .collect(Collectors.toList());

        List<EnvironmentData> environmentDataList = simulateConfigDto.getEnvironmentDtoList().stream()
                .parallel()
                .map(this::readEnvironmentData)
                .collect(Collectors.toList());

        List<Device> deviceList = simulateConfigDto.getModelDtoList().stream()
                .parallel()
                .map(this::readModelData)
                .collect(Collectors.toList());

        List<Producer> producerList = deviceList.stream()
                .filter(x -> x instanceof Producer)
                .map(x -> (Producer) x)
                .collect(Collectors.toList());

        List<Storage> storageList = deviceList.stream()
                .filter(x -> x instanceof Storage)
                .map(x -> (Storage) x)
                .collect(Collectors.toList());

        List<Provider> providerList = deviceList.stream()
                .filter(x -> x instanceof Provider)
                .map(x -> (Provider) x)
                .collect(Collectors.toList());

        return Simulator.simulate(loadDataList, environmentDataList, producerList, storageList, providerList, new ArrayList<>());
    }

    private EnvironmentData readEnvironmentData(EnvironmentDto environmentDto) {
        switch (environmentDto.getEnvironmentTypeEnum()) {
            case WindSpeed:
                return windSpeedSchemeService.findById(environmentDto.getId());

            case WaterSpeed:
                return waterSpeedSchemeService.findById(environmentDto.getId());

            case Temperature:
                return temperatureSchemeService.findById(environmentDto.getId());

            case Sunlight:
                return sunlightIrradianceSchemeService.findById(environmentDto.getId());

            case Gas:
                return gasSchemeService.findById(environmentDto.getId());

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
                return electricLoadSchemeService.findById(loadDto.getId());
            case ThermalLoad:
                return thermalLoadSchemeService.findById(loadDto.getId());
            default:
                log.error("未识别的负荷类型: {}, ID={}", loadDto.getLoadTypeEnum(), loadDto.getId());
                throw new IllegalArgumentException("未知负荷类型: " + loadDto.getLoadTypeEnum() +
                        "，ID=" + loadDto.getId());
        }

    }
}
