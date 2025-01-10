package org.core.pso.simulator;

import org.core.model.device.GasBoilerModel;
import org.core.model.device.ThermalPowerModel;
import org.core.model.environment.sunlight.IrradianceData;
import org.core.model.load.heat.ThermalLoadData;
import org.core.pso.simulator.result.HeatBalanceResult;
import org.core.pso.simulator.result.SimulateResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    /**
     * 模拟光热电站和燃气锅炉的运行
     *
     * @param thermalModel   光热电站模型
     * @param gasBoilerModel 燃气锅炉模型
     * @param heatLoadData   热负荷需求数据（单位: kW）
     * @param irradianceData 光照数据（DNI, 单位: W/m²）
     * @return 模拟结果对象 SimulateResult
     */
    public SimulateResult simulate(ThermalPowerModel thermalModel, GasBoilerModel gasBoilerModel,
                                   ThermalLoadData heatLoadData, IrradianceData irradianceData) {

        // 1. 计算光热电站出力 (MW 转换为 kW)
        List<BigDecimal> thermalPowerList = thermalModel.calculateThermalPowerList(irradianceData);

        // 2. 计算燃气锅炉出力
        gasBoilerModel.calculateHeatPowers(heatLoadData.getThermalLoadData(), thermalPowerList);

        // 储能暂时先放一放

        // 3.

        // 4. 生成热平衡结果
        HeatBalanceResult heatBalanceResult = new HeatBalanceResult(thermalModel, gasBoilerModel);

        // 5. 返回模拟结果
        return new SimulateResult(heatBalanceResult);
    }

}

