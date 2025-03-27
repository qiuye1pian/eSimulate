package org.esimulate.core.pojo.chart.bar3d;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolarPower3DChartDtoTest {
    @Test
    public void test_convert_point() {
        SolarPower3DChartDto solarPower3DChartDto = new SolarPower3DChartDto(BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO);
        assertEquals("[1, 10, 0]", solarPower3DChartDto.toPoint());
    }
}