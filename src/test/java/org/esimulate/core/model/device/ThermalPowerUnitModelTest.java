package org.esimulate.core.model.device;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThermalPowerUnitModelTest {

    @Test
    public void testRampUpForTest_case1_underLimit() {
        // 当前10kW, 最大爬坡20kW，能量缺口25kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setCurrentAdjustablePower(new BigDecimal("10"));
        model.setRampUpRate(new BigDecimal("20"));
        // 爬坡
        BigDecimal result = model.rampUpForTest(new BigDecimal("-25"));

        // 爬到满足要求即可
        assertEquals(new BigDecimal("25.00"), result);
    }

    @Test
    public void testRampUpForTest_case2_overLimit() {
        // 当前10kW, 最大爬坡20kW，能量缺口50kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setCurrentAdjustablePower(new BigDecimal("10"));
        model.setRampUpRate(new BigDecimal("20"));

        BigDecimal result = model.rampUpForTest(new BigDecimal("-50")); // 差额 < rampUpRate

        // 爬到30
        assertEquals(new BigDecimal("30"), result);
    }


    @Test
    public void testRampDownForTest_case1() {
        // 当前50kW, 最大下坡20kW，能量缺口25kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setCurrentAdjustablePower(new BigDecimal("50"));
        model.setRampDownRate(new BigDecimal("20"));
        // 向下爬坡
        BigDecimal result = model.rampDownForTest(new BigDecimal("-25"));

        // 爬到30
        assertEquals(new BigDecimal("30"), result);
    }

    @Test
    public void testRampDownForTest_case2() {
        // 当前50kW, 最大下坡20kW，能量缺口45kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setCurrentAdjustablePower(new BigDecimal("50"));
        model.setRampDownRate(new BigDecimal("20"));
        // 爬坡
        BigDecimal result = model.rampDownForTest(new BigDecimal("-45"));

        // 爬到满足要求即可
        assertEquals(new BigDecimal("45.00"), result);
    }

    @Test
    public void testRampDownForTest_case3() {
        // 当前20kW, 最大下坡20kW，能量缺口0kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setCurrentAdjustablePower(new BigDecimal("20"));
        model.setRampDownRate(new BigDecimal("20"));
        // 爬坡
        BigDecimal result = model.rampDownForTest(new BigDecimal("0"));

        // 爬到满足要求即可
        assertEquals(new BigDecimal("0"), result);
    }

    @Test
    public void testRampDownForTest_case4() {
        // 当前20kW, 最大下坡20kW，能量缺口0kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setCurrentAdjustablePower(new BigDecimal("20"));
        model.setRampDownRate(new BigDecimal("10"));
        // 爬坡
        BigDecimal result = model.rampDownForTest(new BigDecimal("0"));

        // 爬到满足要求即可
        assertEquals(new BigDecimal("10"), result);
    }

    @Test
    public void testRampDownForTest_case5() {
        // 当前50kW, 最大下坡20kW，能量缺口45kW
        ThermalPowerUnitModel model = new ThermalPowerUnitModel();
        model.setQuantity(BigDecimal.valueOf(2));
        model.setCurrentAdjustablePower(new BigDecimal("50"));
        model.setRampDownRate(new BigDecimal("20"));
        // 爬坡
        BigDecimal result = model.rampDownForTest(new BigDecimal("-45"));

        // 爬到满足要求即可
        assertEquals(new BigDecimal("45.00"), result);
    }

}