package org.esimulate.core.model.device;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CogenerationModelTest {

    @Test
    public void testRampDown_SufficientAfterRampDown_01() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("60"));
        model.setRampDownRate(new BigDecimal("10"));

        BigDecimal thermalGap = new BigDecimal("-50");

        model.rampDownForTest(thermalGap);
        assertEquals(new BigDecimal("50"), model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_SufficientAfterRampDown_02() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("60"));
        model.setRampDownRate(new BigDecimal("20"));

        BigDecimal thermalGap = new BigDecimal("-50");

        model.rampDownForTest(thermalGap);
        assertEquals(new BigDecimal("50.00"), model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_SufficientAfterRampDown_03() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("70"));
        model.setRampDownRate(new BigDecimal("10"));
        model.setQuantity(BigDecimal.valueOf(2));

        BigDecimal thermalGap = new BigDecimal("-100");

        model.rampDownForTest(thermalGap);
        assertEquals(new BigDecimal("60"), model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_SufficientAfterRampDown_04() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("60"));
        model.setRampDownRate(new BigDecimal("10"));
        model.setQuantity(BigDecimal.valueOf(2));

        BigDecimal thermalGap = new BigDecimal("-100");

        model.rampDownForTest(thermalGap);
        assertEquals(new BigDecimal("50"), model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_SufficientAfterRampDown_05() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("70"));
        model.setRampDownRate(new BigDecimal("30"));
        model.setQuantity(BigDecimal.valueOf(2));

        BigDecimal thermalGap = new BigDecimal("-100");

        model.rampDownForTest(thermalGap);
        assertEquals(new BigDecimal("50.00"), model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_ToZeroThenInsufficient_01() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("10"));
        model.setRampDownRate(new BigDecimal("10"));

        BigDecimal thermalGap = BigDecimal.ZERO;

        model.rampDownForTest(thermalGap);
        assertEquals(BigDecimal.ZERO, model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_ToZeroThenInsufficient_02() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("10"));
        model.setRampDownRate(new BigDecimal("20"));

        BigDecimal thermalGap = BigDecimal.ZERO;

        model.rampDownForTest(thermalGap);
        assertEquals(BigDecimal.ZERO, model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_ToZeroThenInsufficient_03() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("20"));
        model.setRampDownRate(new BigDecimal("10"));

        BigDecimal thermalGap = BigDecimal.ZERO;

        model.rampDownForTest(thermalGap);
        assertEquals(BigDecimal.TEN, model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_ToZeroThenInsufficient_04() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("20"));
        model.setRampDownRate(new BigDecimal("10"));

        model.setQuantity(BigDecimal.valueOf(2));

        BigDecimal thermalGap = BigDecimal.ZERO;

        model.rampDownForTest(thermalGap);
        assertEquals(BigDecimal.TEN, model.getCurrentAdjustableThermalPower());
    }

    @Test
    public void testRampDown_NoAdjust() {
        CogenerationModel model = new CogenerationModel();
        model.setCurrentAdjustableThermalPower(new BigDecimal("50"));
        model.setRampDownRate(new BigDecimal("10"));

        BigDecimal thermalGap = new BigDecimal("-50");

        model.rampDownForTest(thermalGap);
        assertEquals(new BigDecimal("50.00"), model.getCurrentAdjustableThermalPower());
    }


}