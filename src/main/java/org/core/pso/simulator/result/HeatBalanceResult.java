package org.core.pso.simulator.result;

import org.core.model.device.GasBoilerModel;
import org.core.model.device.ThermalPowerModel;

import java.math.BigDecimal;
import java.util.List;

public class HeatBalanceResult {
    private final ThermalPowerModel thermalPowerModel;
    private final GasBoilerModel gasBoilerModel;
    private final List<BigDecimal> gasBoilerOutputList; // 燃气锅炉出力 (kW)

    public HeatBalanceResult(ThermalPowerModel thermalPowerModel, GasBoilerModel gasBoilerModel,
                            List<BigDecimal> gasBoilerOutputList) {
        this.thermalPowerModel = thermalPowerModel;
        this.gasBoilerModel = gasBoilerModel;

        this.gasBoilerOutputList = gasBoilerOutputList;
    }

    public List<BigDecimal> getThermalPowerList() {
       return thermalPowerModel.getThermalPowerList();
    }
}