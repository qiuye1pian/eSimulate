package org.core.pso.simulator.result;

import org.core.model.device.GasBoilerModel;
import org.core.model.device.ThermalPowerModel;

import java.math.BigDecimal;
import java.util.List;

public class HeatBalanceResult {

    private final ThermalPowerModel thermalPowerModel;

    private final GasBoilerModel gasBoilerModel;

    public HeatBalanceResult(ThermalPowerModel thermalPowerModel, GasBoilerModel gasBoilerModel) {
        this.thermalPowerModel = thermalPowerModel;
        this.gasBoilerModel = gasBoilerModel;
    }


    public List<BigDecimal> gasBoilerOutputList(){
        return gasBoilerModel.gasBoilerOutputList();
    }
}