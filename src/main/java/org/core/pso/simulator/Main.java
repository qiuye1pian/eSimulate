package org.core.pso.simulator;

import org.core.model.device.ThermalPowerModel;
import org.core.model.environment.sunlight.SunlightIrradianceScheme;
import org.core.model.environment.sunlight.SunlightIrradianceValue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // 构造光照数据
        List<SunlightIrradianceValue> values = Arrays.asList(
                new SunlightIrradianceValue(LocalDateTime.of(2023, 1, 1, 10, 0,0), new BigDecimal("500")),
                new SunlightIrradianceValue(LocalDateTime.of(2023, 1, 1, 9, 0,0), new BigDecimal("600")),
                new SunlightIrradianceValue(LocalDateTime.of(2023, 1, 1, 11, 0,0), new BigDecimal("400"))
        );

        // 初始化光照方案
        SunlightIrradianceScheme scheme = new SunlightIrradianceScheme(values);

        // 初始化光热电站模型
        ThermalPowerModel thermalModel = new ThermalPowerModel("0.75", "50000", 2);

        // 计算热功率


        // 输出结果

    }
}