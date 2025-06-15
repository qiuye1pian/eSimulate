package org.esimulate.dev;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CsvGenerator {

    private static @NotNull Result getSunlight() {
        String init = "0.00";
        String jump = "400.00";
        String valueTitle = "光照";
        String min = "0";
        String max = "2100.00";
        return new Result(init, jump, valueTitle, min, max);
    }

    private static @NotNull Result getTemperature() {
        String init = "21.0";
        String jump = "5.00";
        String valueTitle = "温度";
        String min = "-20";
        String max = "41.35";
        return new Result(init, jump, valueTitle, min, max);
    }

    private static @NotNull Result getWindSpeed() {
        String init = "7.1";
        String jump = "5.50";
        String valueTitle = "风速";
        String min = "0";
        String max = "32.00";
        return new Result(init, jump, valueTitle, min, max);
    }

    private static @NotNull Result getWaterSpeed() {
        String init = "13.3";
        String jump = "5.80";
        String valueTitle = "水流";
        String min = "0.5";
        String max = "46.00";
        return new Result(init, jump, valueTitle, min, max);
    }


    private static @NotNull Result getLoad() {
        String init = "980931.1";
        String jump = "77611.03";
        String valueTitle = "电负荷";
        String min = "658931.1";
        String max = "1746000.0";
        return new Result(init, jump, valueTitle, min, max);
    }

    /**
     * 曲线生成器
     *
     * @param args 没有参数
     */
    public static void main(String[] args) {

        Result result = getLoad();

        int days = 1;

        // 设置初始时间
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        // 设置日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 设置随机数生成器
        Random random = new Random();
        // 设置初始值
        BigDecimal previousValue = new BigDecimal(result.init);
        // 设置波动范围
        BigDecimal maxFluctuation = new BigDecimal(result.jump);
        // 上一次的波动值（用于引入惯性）
        BigDecimal previousFluctuation = BigDecimal.ZERO;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/chenhonghe/Desktop/华北电力大学/县域多能互补一体化平台/脚本/上传模板/load_values.csv"))) {
            // 写入 CSV 表头
            writer.write(String.format("%s,%s\n", "时间", result.valueTitle));

            // 写入每一行数据
            for (int i = 0; i < days * 24; i++) { // 一年的小时数
                // 当前时间
                LocalDateTime currentTime = startTime.plusHours(i);
                // 生成随机波动分量
                BigDecimal randomComponent = maxFluctuation.multiply(BigDecimal.valueOf(random.nextDouble() * 2 - 1));
                // 当前波动 = 上一次波动 * 0.8 + 本次随机分量 * 0.2（可根据需求调整权重）
                BigDecimal fluctuation = previousFluctuation.multiply(BigDecimal.valueOf(0.8))
                        .add(randomComponent.multiply(BigDecimal.valueOf(0.2)));
                // 计算当前值
                BigDecimal currentValue = previousValue.add(fluctuation);
                // 确保值在指定范围内
                if (currentValue.compareTo(new BigDecimal(result.min)) < 0) {
                    currentValue = new BigDecimal(result.min);
                }
                if (currentValue.compareTo(new BigDecimal(result.max)) > 0) {
                    currentValue = new BigDecimal(result.max).add(fluctuation.multiply(BigDecimal.valueOf(random.nextDouble() * 2 - 1)));
                }
                // 写入当前行
                writer.write(String.format("%s,%.2f\n", currentTime.format(formatter), currentValue.setScale(2, RoundingMode.HALF_UP)));
                // 更新上一波动值
                previousFluctuation = fluctuation;
                // 更新前一个荷值
                previousValue = currentValue;
            }
        } catch (IOException e) {
            System.out.printf("发生错误" + e.getMessage());
        }
    }


    private static class Result {
        public final String init;
        public final String jump;
        public final String valueTitle;
        public final String min;
        public final String max;

        public Result(String init, String jump, String valueTitle, String min, String max) {
            this.init = init;
            this.jump = jump;
            this.valueTitle = valueTitle;
            this.min = min;
            this.max = max;
        }
    }
}