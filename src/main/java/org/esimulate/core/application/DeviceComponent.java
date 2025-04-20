package org.esimulate.core.application;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.simulate.ModelLoadDto;
import org.esimulate.core.pso.particle.Dimension;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.service.device.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DeviceComponent {

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
    ThermalSaverService thermalSaverService;

    @Autowired
    GridService gridService;

    public @NotNull List<Device> getDeviceList(List<ModelLoadDto> modelDtoList) {
        return modelDtoList.stream()
                .parallel()
                .map(this::readModelData)
                .collect(Collectors.toList());
    }

    private Device readModelData(ModelLoadDto modelDto) {
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
        if (device instanceof Dimension && modelDto instanceof Dimension) {
            Dimension modelDimensionDto = (Dimension) modelDto;
            ((Dimension) device).setLowerBound(modelDimensionDto.getLowerBound());
            ((Dimension) device).setUpperBound(modelDimensionDto.getUpperBound());
        }
        return device;
    }

}
