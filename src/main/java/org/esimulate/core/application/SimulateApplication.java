package org.esimulate.core.application;

import org.esimulate.core.pojo.simulate.LoadDto;
import org.esimulate.core.pojo.simulate.SimulateConfigDto;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.esimulate.core.pso.simulator.result.SimulateResult;
import org.esimulate.core.service.load.ElectricLoadSchemeService;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SimulateApplication {

    @Autowired
    ElectricLoadSchemeService electricLoadSchemeService;

    @Autowired
    ThermalLoadSchemeService thermalLoadSchemeService;


    public SimulateResult doSimulate(SimulateConfigDto simulateConfigDto) {

        List<LoadData> loadDataList = simulateConfigDto.getLoadDtoList().stream()
                .map(this::readLoadData)
                .collect(Collectors.toList());



        return SimulateResult.success();
    }

    private LoadData readLoadData(LoadDto loadDto) {
        switch (loadDto.getLoadTypeEnum()) {
            case ElectricLoad:
                return electricLoadSchemeService.findById(loadDto.getId());
            case ThermalLoad:
                return thermalLoadSchemeService.findById(loadDto.getId());
        }
        throw new IllegalArgumentException("未知负荷类型: " + loadDto.getLoadTypeEnum() +
                "，ID=" + loadDto.getId());
    }
}
