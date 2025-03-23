package org.esimulate.core.pojo;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class WindPowerChartDtoTest {
    /**
     * 正常数据测试
     */
    @Test
    void testConstructorWithNormalData() {
        // 构造测试数据
        List<BigDecimal> xAxisData = Arrays.asList(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("3.0"));
        List<BigDecimal> seriesData = Arrays.asList(
                new BigDecimal("5.0"),
                new BigDecimal("10.0"),
                new BigDecimal("7.5"));
        // 当 seriesData 中最大值为 10，该值乘以 1.33 后为 13.3，设置 scale 0 且舍去小数点部分最后应该为 13
        WindPowerChartDto dto = new WindPowerChartDto(xAxisData, seriesData);
        // 检查 xAxis
        Assertions.assertNotNull(dto.getXAxis(), "xAxis 不应为 null");
        Assertions.assertEquals("category", dto.getXAxis().getType(), "xAxis type 应为 'category'");
        Assertions.assertFalse(dto.getXAxis().isBoundaryGap(), "boundaryGap 应为 false");
        // 比较 xAxis 数据
        Assertions.assertEquals(xAxisData, dto.getXAxis().getData(), "xAxis 数据不一致");
        // 检查 axisLabel 的 formatter
        Assertions.assertEquals("{value} m/s", dto.getXAxis().getAxisLabel().getFormatter(), "axisLabel formatter 不正确");
        // 检查 series，只有一个 Series 对象
        Assertions.assertNotNull(dto.getSeries(), "series 不应为 null");
        Assertions.assertEquals(1, dto.getSeries().size(), "series 内对象个数应为 1");
        WindPowerChartDto.Series series = dto.getSeries().get(0);
        Assertions.assertEquals("", series.getName(), "Series的name应为空字符串");
        Assertions.assertEquals("line", series.getType(), "Series的type应为 'line'");
        Assertions.assertTrue(series.getSmooth(), "Series 的 smooth 应为 true");
        Assertions.assertEquals(seriesData, series.getData(), "Series 数据不一致");
        // 检查 yAxis，最大值计算
        Assertions.assertNotNull(dto.getYAxis(), "yAxis 不应为 null");
        // 10 * 1.33 = 13.3，setScale(0, RoundingMode.DOWN) 后为 13
        Assertions.assertEquals(new BigDecimal("13"), dto.getYAxis().getMax(), "yAxis max 计算不正确");
    }

    /**
     * 测试 seriesData 为空时
     */
    @Test
    void testConstructorWithEmptySeriesData() {
        List<BigDecimal> xAxisData = Arrays.asList(
                new BigDecimal("0.5"),
                new BigDecimal("1.5"));
        List<BigDecimal> seriesData = Collections.emptyList();
        WindPowerChartDto dto = new WindPowerChartDto(xAxisData, seriesData);
        // 验证 xAxis
        Assertions.assertEquals(xAxisData, dto.getXAxis().getData(), "xAxis 数据不一致");

        // 验证 series 数据为空
        Assertions.assertNotNull(dto.getSeries(), "series 不应为 null");
        Assertions.assertEquals(1, dto.getSeries().size(), "series 内对象个数应为 1");
        Assertions.assertTrue(dto.getSeries().get(0).getData().isEmpty(), "Series 数据列表应为空");
        // 验证 yAxis，由于 seriesData为空，所以最大值默认为 0，然后乘以1.33之后仍然为 0
        Assertions.assertEquals(BigDecimal.ZERO, dto.getYAxis().getMax(), "yAxis max 应该为 0");
    }

    /**
     * 测试传入 null 数据时，会抛出异常（因为代码中未进行 null 判断）
     */
    @Test
    void testConstructorWithNullData() {
        // xAxisData 为 null
        List<BigDecimal> seriesData = Arrays.asList(new BigDecimal("1.0"), new BigDecimal("2.0"));
        Assertions.assertThrows(NullPointerException.class, () -> {
            new WindPowerChartDto(null, seriesData);
        }, "传入 null 的 xAxisData 应该抛出 NullPointerException");
        // seriesData 为 null
        List<BigDecimal> xAxisData = Arrays.asList(new BigDecimal("1.0"), new BigDecimal("2.0"));
        Assertions.assertThrows(NullPointerException.class, () -> {
            new WindPowerChartDto(xAxisData, null);
        }, "传入 null 的 seriesData 应该抛出 NullPointerException");
    }


    /**
     * 性能和扩展性测试：大量数据环境下构造对象的耗时
     */
    @Test
    void testPerformanceForLargeData() {
        // 构建大量数据
        int size = 100000;
        List<BigDecimal> xAxisData = Collections.nCopies(size, new BigDecimal("1.0"));
        // 构造一个递增序列作为 Series 数据
        List<BigDecimal> seriesData = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            seriesData.add(new BigDecimal(String.valueOf(i)));
        }
        long startTime = System.nanoTime();
        WindPowerChartDto dto = new WindPowerChartDto(xAxisData, seriesData);
        long endTime = System.nanoTime();
        long durationInMs = (endTime - startTime) / 1_000_000;

        // 限定构造方法执行时间在合理范围内（例如 500ms 内）
        Assertions.assertTrue(durationInMs < 500, "构造大量数据对象耗时过长: " + durationInMs + "ms");
        // 验证 yAxis max 应为 (size-1)*1.33 向下取整
        int expectedMaxInt = new BigDecimal(String.valueOf(size - 1))
                .multiply(new BigDecimal("1.33"))
                .setScale(0, RoundingMode.DOWN)
                .intValue();
        Assertions.assertEquals(new BigDecimal(expectedMaxInt), dto.getYAxis().getMax(), "yAxis max 计算不正确");
    }
}