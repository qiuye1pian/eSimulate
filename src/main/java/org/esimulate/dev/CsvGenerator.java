package org.esimulate.dev;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CsvGenerator {

    /**
     * 曲线生成器
     *
     * @param args 没有参数
     */
    public static void main(String[] args) {

        String init = "21.00";
        String jump = "1.00";
        String valueTitle = "温度";
        String min = "-30.00";
        String max = "45.99";

        // 设置初始时间
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        // 设置日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 设置随机数生成器
        Random random = new Random();
        // 设置初始值
        BigDecimal previousValue = new BigDecimal(init);
        // 设置波动范围
        BigDecimal maxFluctuation = new BigDecimal(jump);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/chenhonghe/Desktop/华北电力大学/县域多能互补一体化平台/脚本/上传模板/load_values.csv"))) {
            // 写入 CSV 表头
            writer.write(String.format("%s,%s\n", "时间", valueTitle));

            // 写入每一行数据
            for (int i = 0; i < 365 * 24; i++) { // 一年的小时数
                // 当前时间
                LocalDateTime currentTime = startTime.plusHours(i);
                // 生成负荷值波动
                BigDecimal fluctuation = maxFluctuation.multiply(BigDecimal.valueOf(random.nextDouble() * 2 - 1));
                // 计算当前负荷值
                BigDecimal currentValue = previousValue.add(fluctuation);
                // 确保负荷值在指定范围内
                if (currentValue.compareTo(new BigDecimal(min)) < 0) {
                    currentValue = new BigDecimal(min);
                }

                if (currentValue.compareTo(new BigDecimal(max)) > 0) {
                    currentValue = new BigDecimal(max);
                }
                // 写入当前行
                writer.write(String.format("%s,%.2f\n", currentTime.format(formatter), currentValue.setScale(2, RoundingMode.HALF_UP)));
                // 更新前一个负荷值
                previousValue = currentValue;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}