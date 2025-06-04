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

/**
 * 直接生成指定天数的逐小时光照数据（CSV 格式），
 * 白天 (6:00–18:00) 按正弦曲线模拟，中午 12–14 点为峰值；
 * 1–4 月 & 10–12 月峰值乘以 0.85，其它月份乘以 1.0；
 * 夜晚（18:00–次日 6:00）光照强度为 0；
 * 在基准曲线上再叠加带惯性的随机波动。
 * <p>
 * 使用示例（从命令行传参）：
 * javac SunlightCsvGenerator.java
 * java SunlightCsvGenerator 365 output_sunlight.csv
 * <p>
 * 如果不希望通过命令行传参，也可直接修改 main() 中的 days 和 outputPath。
 */
public class SunlightCsvGenerator {

    public static void main(String[] args) {

        int days = 365;

        String outputPath = "/Users/chenhonghe/Desktop/华北电力大学/县域多能互补一体化平台/脚本/上传模板/sunlight_values.csv";

        // 光照参数
        Result result = getSunlight();

        // 随机波动生成器
        Random random = new Random();

        // 时间起点：2025-01-01 00:00:00（如果需要用其它年份/日期，可自行修改）
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // “带惯性”的上一次波动值，初始为 0
        BigDecimal previousFluctuation = BigDecimal.ZERO;

        // 最大波动幅度 = jump
        BigDecimal maxFluctuation = new BigDecimal(result.jump);

        // 将 min/max 转为 BigDecimal 方便比较
        BigDecimal minAllowed = new BigDecimal(result.min);
        BigDecimal maxAllowed = new BigDecimal(result.max);



        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // 写入 CSV 表头
            writer.write("时间," + result.valueTitle + "\n");

            // 一年的小时数 = days * 24
            int totalHours = days * 24;
            for (int i = 0; i < totalHours; i++) {
                LocalDateTime currentTime = startTime.plusHours(i);
                int hourOfDay = currentTime.getHour();       // 0–23
                int monthOfYear = currentTime.getMonthValue(); // 1–12

                // 1) 计算当下“基准无波动”光照值（夜晚为 0，白天按正弦分布；12:00–14:00 附近最大）
                BigDecimal baseValue;

                double monthFactor = monthOfYear <= 4 || monthOfYear >= 10 ? 0.85 : 1.0;

                double sunrise = Math.round(result.sunrise - 0.2d * (6 - Math.abs(monthOfYear - 6.5)));
                double sunset = Math.round(result.sunset + 0.3d * (6 - Math.abs(monthOfYear - 6.5)));

                double dayLength = sunset - sunrise;

                if (hourOfDay < sunrise || hourOfDay > sunset) {
                    // 夜晚：光照 0
                    baseValue = BigDecimal.ZERO;
                } else {
                    // 白天：用 sin 曲线，从 6 时开始 0 → 12 时达到峰值 → 18 时回到 0
                    // 时间参数 t = (小时数 - 6) / 12.0，t ∈ [0,1] ； sin(π * t) ∈ [0,1]
                    double t = (hourOfDay - sunrise) / dayLength;
                    double factor = Math.sin(Math.PI * t);
                    // 确定当月系数：1–4 月 与 10–12 月乘以 0.85，其他月份 1.0
                    // 峰值 = maxAllowed * monthFactor
                    BigDecimal peakThisMonth = maxAllowed.multiply(BigDecimal.valueOf(monthFactor - (random.nextDouble() / 10)));
                    // “基准值” = sin(...) * 峰值
                    baseValue = peakThisMonth.multiply(BigDecimal.valueOf(factor));
                }

                // 2) 在基准值上叠加“带惯性”的随机波动，夜晚时保持为 0
                BigDecimal fluctuation;
                if (baseValue.compareTo(BigDecimal.ZERO) == 0) {
                    // 夜晚：没有波动
                    fluctuation = BigDecimal.ZERO;
                } else {
                    // 白天：计算随机波动
                    BigDecimal randomComponent = maxFluctuation
                            .multiply(BigDecimal.valueOf(random.nextDouble() * 2.0 - 1.0));
                    // 本次波动 = previousFluctuation * 0.65 + randomComponent * 0.35
                    fluctuation = previousFluctuation
                            .multiply(BigDecimal.valueOf(0.65))
                            .add(randomComponent.multiply(BigDecimal.valueOf(0.35)));
                }

                //    最终值 = 基准 + 本次波动
                BigDecimal currentValue = baseValue.add(fluctuation);

                // 3) 边界约束：不低于 minAllowed，不高于当月峰值 peakThisMonth
                if (currentValue.compareTo(minAllowed) < 0) {
                    currentValue = minAllowed;
                }
                // 当月峰值 = maxAllowed * 系数
                BigDecimal peakThisMonth = maxAllowed.multiply(BigDecimal.valueOf(monthFactor - (random.nextDouble() / 10)));
                if (currentValue.compareTo(peakThisMonth) > 0) {
                    currentValue = peakThisMonth;
                }

                // 写入 CSV：格式 "%s,%.2f\n"
                writer.write(String.format(
                        "%s,%.2f\n",
                        currentTime.format(formatter),
                        currentValue.setScale(2, RoundingMode.HALF_UP)
                ));

                // 更新惯性波动与上一时刻值
                previousFluctuation = fluctuation;
            }

            System.out.println("已生成 " + days + " 天的光照数据 → " + outputPath);
        } catch (IOException e) {
            System.err.println("写入文件失败：" + e.getMessage());
        }
    }

    /**
     * 返回初始化参数：
     * init      : 初始光照值（通常从 0 开始）
     * jump      : 随机波动的最大振幅（这里取 400，意味着波动项最大可在 ±400 之间）
     * valueTitle: CSV 中第二列的表头名称
     * min       : 允许的最小光照（夜晚可为 0）
     * max       : 允许的最大光照（理论峰值）
     */
    private static @NotNull Result getSunlight() {
        String init = "0.00";
        String jump = "400.00";
        String valueTitle = "光照";
        String min = "0";
        String max = "2100.00";
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