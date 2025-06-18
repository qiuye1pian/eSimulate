package org.esimulate.core.application;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pojo.simulate.LoadDto;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.esimulate.core.service.load.ElectricLoadSchemeService;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoadDataComponent {

    @Autowired
    ElectricLoadSchemeService electricLoadSchemeService;

    @Autowired
    ThermalLoadSchemeService thermalLoadSchemeService;

    public @NotNull List<LoadData> getLoadData(List<LoadDto> loadDtoList) {
        return loadDtoList.stream()
                .parallel()
                .map(this::readLoadData)
                .collect(Collectors.toList());
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
