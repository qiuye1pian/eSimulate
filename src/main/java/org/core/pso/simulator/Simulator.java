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
        // 2. 计算差额负荷 (热负荷 - 光热出力)
        List<BigDecimal> deficitList = calculateDeficit(heatLoadData.getThermalLoadData(), thermalPowerList);

        // 3. 计算燃气锅炉出力
        List<BigDecimal> gasBoilerOutputList = new ArrayList<>();
        for (BigDecimal deficit : deficitList) {
            gasBoilerOutputList.add(gasBoilerModel.calculateHeatPower(deficit, BigDecimal.ZERO));
        }

        // 4. 生成热平衡结果
        HeatBalanceResult heatBalanceResult = new HeatBalanceResult(
                thermalModel, gasBoilerModel, thermalPowerList, gasBoilerOutputList
        );

        // 5. 返回模拟结果
        return new SimulateResult(heatBalanceResult);
    }

    /**
     * 计算差额负荷 (热负荷 - 光热出力)
     *
     * @param heatLoadData    热负荷需求数据 (kW)
     * @param thermalPowerList 光热电站出力 (kW)
     * @return 差额负荷列表 (kW)
     */
    private List<BigDecimal> calculateDeficit(List<BigDecimal> heatLoadData, List<BigDecimal> thermalPowerList) {
        if (heatLoadData.size() != thermalPowerList.size()) {
            throw new IllegalArgumentException("热负荷数据和光热出力数据长度必须一致！");
        }

        List<BigDecimal> deficitList = new ArrayList<>();
        for (int i = 0; i < heatLoadData.size(); i++) {
            BigDecimal deficit = heatLoadData.get(i).subtract(thermalPowerList.get(i)).max(BigDecimal.ZERO);
            deficitList.add(deficit);
        }
        return deficitList;
    }
}

