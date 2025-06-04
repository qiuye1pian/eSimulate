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


public class TemperatureCsvGenerator {

    public static void main(String[] args) {

        int days = 365;

        // 参数
        Result result = getTemperature();

        String outputPath = String.format("/Users/chenhonghe/Desktop/华北电力大学/县域多能互补一体化平台/脚本/上传模板/%s.csv", result.valueTitle);

        // 随机波动生成器
        Random random = new Random();

        // 时间起点：2025-01-01 00:00:00（如果需要用其它年份/日期，可自行修改）
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // 写入 CSV 表头
            writer.write("时间," + result.valueTitle + "\n");

            // 一年的小时数 = days * 24
            int totalHours = days * 24;
            for (int i = 0; i < totalHours; i++) {
                LocalDateTime currentTime = startTime.plusHours(i);
                int hourOfDay = currentTime.getHour();         // 0–23
                int monthOfYear = currentTime.getMonthValue(); // 1–12

                // —— 1) 根据月份决定「当日最低温/最高温」区间 —— //
                // 先定义每个“月对应的最低/最高值”：
                BigDecimal dailyMin, dailyMax;

                switch (monthOfYear) {
                    case 1:
                        dailyMin = new BigDecimal("-10");
                        dailyMax = new BigDecimal("10");
                        break;
                    case 2:
                        dailyMin = new BigDecimal("-8");
                        dailyMax = new BigDecimal("12");
                        break;
                    case 3:
                        dailyMin = new BigDecimal("-5");
                        dailyMax = new BigDecimal("15");
                        break;
                    case 4:
                        dailyMin = new BigDecimal("5");
                        dailyMax = new BigDecimal("25");
                        break;
                    case 5:
                        dailyMin = new BigDecimal("10");
                        dailyMax = new BigDecimal("30");
                        break;
                    case 6:
                        dailyMin = new BigDecimal("15");
                        dailyMax = new BigDecimal("35");
                        break;
                    case 7:
                        dailyMin = new BigDecimal("20");
                        dailyMax = new BigDecimal("38");
                        break;
                    case 8:
                        dailyMin = new BigDecimal("22");
                        dailyMax = new BigDecimal("40");
                        break;
                    case 9:
                        dailyMin = new BigDecimal("18");
                        dailyMax = new BigDecimal("35");
                        break;
                    case 10:
                        dailyMin = new BigDecimal("12");
                        dailyMax = new BigDecimal("32");
                        break;
                    case 11:
                        dailyMin = new BigDecimal("-5");
                        dailyMax = new BigDecimal("15");
                        break;
                    case 12:
                        dailyMin = new BigDecimal("-8");
                        dailyMax = new BigDecimal("12");
                        break;
                    default:
                        // 万一出现 不是 1–12 以外的值
                        dailyMin = new BigDecimal("0");
                        dailyMax = new BigDecimal("20");
                }

                // —— 2) 计算昼夜温度曲线：余弦函数，14 点取最高 (normalized=+1)，2 点取最低 (normalized=-1) —— //
                // 余弦函数参数：angle = π * (hourOfDay - 14) / 12
                double angle = Math.PI * (hourOfDay - 14) / 12.0;
                // normalized ∈ [-1, +1]
                double normalized = Math.cos(angle);

                // 先把 dailyMin/dailyMax 换成 double 做运算，最后再转 BigDecimal
                double minD = dailyMin.doubleValue();
                double maxD = dailyMax.doubleValue();

                // 日均温 = (min + max) / 2
                double dailyAvg = (minD + maxD) / 2.0;
                // 半振幅 = (max - min) / 2
                double amplitude = (maxD - minD) / 2.0;

                // 当小时为 2 时，normalized = cos((2-14)/12*π) = cos(-π) = -1 → base = dailyAvg + amplitude * (-1) = dailyMin
                // 当小时为 14 时, normalized = cos(0) = +1 → base = dailyAvg + amplitude * (+1) = dailyMax
                double baseTempDouble = dailyAvg + amplitude * normalized;
                BigDecimal baseTemp = BigDecimal.valueOf(baseTempDouble)
                        .setScale(2, RoundingMode.HALF_UP);

                // —— 3) 随机微扰（可选） —— //
                // 如果需要“带惯性”的波动，可以保留原来的 previousFluctuation 逻辑；也可以使用无惯性、纯随机：
                BigDecimal fluctuation;
                // 这里示例：在±1.0°C 之间均匀抖动
                double randomDelta = (random.nextDouble() * 2.0 - 1.0) * 2.0; // [-1, +1) × 2°C
                fluctuation = BigDecimal.valueOf(randomDelta)
                        .setScale(2, RoundingMode.HALF_UP);

                // 最终温度 = 基准 + 随机扰动
                BigDecimal currentValue = baseTemp.add(fluctuation);

                // —— 4) 边界约束：不得低于 dailyMin，不得高于 dailyMax —— //
                if (currentValue.compareTo(dailyMin) < 0) {
                    currentValue = dailyMin;
                }
                if (currentValue.compareTo(dailyMax) > 0) {
                    currentValue = dailyMax;
                }

                // —— 5) 写入 CSV —— //
                writer.write(String.format(
                        "%s,%.2f\n",
                        currentTime.format(formatter),
                        currentValue.setScale(2, RoundingMode.HALF_UP)
                ));
            }
        } catch (IOException e) {
            System.err.println("写入文件失败：" + e.getMessage());
        }
    }

    private static @NotNull Result getTemperature() {
        String init = "21.0";
        String jump = "5.00";
        String valueTitle = "温度";
        String min = "-20";
        String max = "41.35";
        int sunrise = 6;
        int sunset = 18;
        return new Result(init, jump, valueTitle, min, max, sunrise, sunset);
    }

    private static class Result {
        public final String init;
        public final String jump;
        public final String valueTitle;
        public final String min;
        public final String max;
        public int sunrise;
        public int sunset;

        public Result(String init, String jump, String valueTitle, String min, String max, int sunrise, int sunset) {
            this.init = init;
            this.jump = jump;
            this.valueTitle = valueTitle;
            this.min = min;
            this.max = max;
            this.sunrise = sunrise;
            this.sunset = sunset;
        }
    }
}