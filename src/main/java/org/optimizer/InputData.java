package org.optimizer;

import lombok.Data;

import java.util.Random;

@Data
public class InputData {
    private double[] windPowerHourly;
    private double[] pvPowerHourly;
    private double[] loadCurve;
    private double[] priceData;

    public InputData() {
        // 初始化数据
        this.windPowerHourly = generateWindPowerData(24);
        this.pvPowerHourly = generatePVPowerData(24);
        this.loadCurve = generateLoadCurve(24);
        this.priceData = generatePriceData(24);
    }

    // 生成风电出力数据
    private double[] generateWindPowerData(int hours) {
        double[] data = new double[hours];
        Random random = new Random();
        for (int i = 0; i < hours; i++) {
            data[i] = 500 + random.nextDouble() * 500; // 随机生成500到1000之间的风电数据
        }
        return data;
    }

    // 生成光伏出力数据
    private double[] generatePVPowerData(int hours) {
        double[] data = new double[hours];
        Random random = new Random();
        for (int i = 0; i < hours; i++) {
            data[i] = 200 + random.nextDouble() * 300; // 随机生成200到500之间的光伏数据
        }
        return data;
    }

    // 生成负载曲线
    private double[] generateLoadCurve(int hours) {
        double[] data = new double[hours];
        Random random = new Random();
        for (int i = 0; i < hours; i++) {
            data[i] = 1000 + random.nextDouble() * 500; // 随机生成1000到1500之间的负载数据
        }
        return data;
    }

    // 生成电价数据
    private double[] generatePriceData(int hours) {
        double[] data = new double[hours];
        Random random = new Random();
        for (int i = 0; i < hours; i++) {
            data[i] = 0.5 + random.nextDouble() * 0.5; // 随机生成0.5到1.0之间的电价数据
        }
        return data;
    }
}
