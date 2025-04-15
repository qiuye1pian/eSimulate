package org.esimulate.core.application;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.simulate.EnvironmentDto;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.service.environment.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EnvironmentDataComponent {

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

    public @NotNull List<EnvironmentData> getEnvironmentData(List<EnvironmentDto> environmentDtoList) {
        return environmentDtoList.stream()
                .parallel()
                .map(this::readEnvironmentData)
                .collect(Collectors.toList());
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

}
