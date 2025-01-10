package org.core.pso.simulator;

import org.core.model.device.ThermalPowerModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    /**
     * 模拟光热电站和燃气锅炉的运行
     *
     * @param irradianceData 光照数据（DNI, 单位: W/m²），任意长度
     * @param thermalModel   光热电站模型
     * @param heatLoadData   热负荷需求数据（单位: kW），与光照数据长度一致
     * @return 每小时燃气锅炉的供热功率列表（单位: kW）
     */
    public List<BigDecimal> simulate(List<BigDecimal> irradianceData, ThermalPowerModel thermalModel, List<BigDecimal> heatLoadData) {
        if (irradianceData.size() != heatLoadData.size()) {
            throw new IllegalArgumentException("光照数据和热负荷数据长度必须一致！");
        }

        List<BigDecimal> boilerHeatOutput = new ArrayList<>();
        for (int i = 0; i < irradianceData.size(); i++) {
            // 计算光热电站吸收的热功率 (MW 转换为 kW)
            BigDecimal thermalPowerMW = thermalModel.calculateThermalPower(irradianceData.get(i));
            BigDecimal thermalPowerKW = thermalPowerMW.multiply(new BigDecimal("1000"));

            // 计算燃气锅炉的供热功率
            BigDecimal boilerOutput = heatLoadData.get(i).subtract(thermalPowerKW).max(BigDecimal.ZERO);
            boilerHeatOutput.add(boilerOutput);
        }
        return boilerHeatOutput;
    }

    /**
     * 计算光热电站的吸收热功率列表（MW）
     *
     * @param irradianceData 光照数据（DNI, 单位: W/m²），任意长度
     * @param thermalModel   光热电站模型
     * @return 每小时光热电站吸收的热功率列表（单位: MW）
     */
    public List<BigDecimal> calculateThermalPowers(List<BigDecimal> irradianceData, ThermalPowerModel thermalModel) {
        List<BigDecimal> thermalPowers = new ArrayList<>();
        for (BigDecimal D_t : irradianceData) {
            thermalPowers.add(thermalModel.calculateThermalPower(D_t));
        }
        return thermalPowers;
    }



}
