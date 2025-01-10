package org.core.pso.simulator;

import org.core.model.device.ThermalPowerModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 初始化光热电站模型
        ThermalPowerModel model = new ThermalPowerModel("0.75", "50000");

        // 模拟一年的小时数据（8760小时）
        List<BigDecimal> irradianceData = generateIrradianceData(8760);
        List<BigDecimal> heatLoadData = generateHeatLoadData(8760);

        // 初始化模拟器
        Simulator simulator = new Simulator();

        // 计算燃气锅炉的供热功率
        List<BigDecimal> boilerHeatOutput = simulator.simulate(irradianceData, model, heatLoadData);

        // 计算光热电站的吸收热功率
        List<BigDecimal> thermalPowers = simulator.calculateThermalPowers(irradianceData, model);

        // 输出部分结果
        System.out.println("燃气锅炉的供热功率 (前10小时):");
        for (int i = 0; i < 10; i++) {
            System.out.println("小时 " + (i + 1) + ": " + boilerHeatOutput.get(i) + " kW");
        }

        System.out.println("\n光热电站的吸收热功率 (前10小时):");
        for (int i = 0; i < 10; i++) {
            System.out.println("小时 " + (i + 1) + ": " + thermalPowers.get(i) + " MW");
        }
    }

    // 生成模拟的光照数据 (DNI)
    private static List<BigDecimal> generateIrradianceData(int hours) {
        List<BigDecimal> data = new ArrayList<>();
        for (int i = 0; i < hours; i++) {
            data.add(new BigDecimal(Math.random() * 1000)); // 随机生成 0~1000 W/m²
        }
        return data;
    }

    // 生成模拟的热负荷数据
    private static List<BigDecimal> generateHeatLoadData(int hours) {
        List<BigDecimal> data = new ArrayList<>();
        for (int i = 0; i < hours; i++) {
            data.add(new BigDecimal(500 + Math.random() * 1000)); // 随机生成 500~1500 kW
        }
        return data;
    }
}
