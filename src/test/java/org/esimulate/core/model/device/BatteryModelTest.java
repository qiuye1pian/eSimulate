package org.esimulate.core.model.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BatteryModelTest {

    private BatteryModel battery;

    @BeforeEach
    public void setup() {
        battery = new BatteryModel();
        battery.setC_t(new BigDecimal("10000"));
        battery.setSOC_min(new BigDecimal("0.1"));
        battery.setSOC_max(new BigDecimal("0.9"));
        battery.setMu(new BigDecimal("0.01"));
        battery.setMaxChargePower(new BigDecimal("3000"));
        battery.setMaxDischargePower(new BigDecimal("2500"));
        battery.setEtaHch(new BigDecimal("0.95"));
        battery.setEtaHdis(new BigDecimal("0.9"));
        battery.setE_ESS_t(new BigDecimal("5000"));
    }

    @Test
    public void testChargingWithinLimits_1() {
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("2000"));
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("6900.00"), battery.getE_ESS_t());
    }

    @Test
    public void testChargingWithinLimits_2() {
        BigDecimal result = battery.testUpdateElectricEnergy(BigDecimal.ZERO);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("5000"), battery.getE_ESS_t());
    }

    @Test
    public void testChargingBeyondSOCMax() {
        battery.setE_ESS_t(new BigDecimal("8800")); // Close to SOC_max
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("2000"));
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0); // Some power left unused
        assertEquals(new BigDecimal("8990.0"), battery.getE_ESS_t().setScale(1, RoundingMode.HALF_UP));
    }

    @Test
    public void testDischargingWithinLimits_1() {
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("-2000"));
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("3200.0"), battery.getE_ESS_t());
    }

    @Test
    public void testDischargingWithinLimits_2() {
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("-2000"));
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
        assertTrue(battery.getE_ESS_t().compareTo(new BigDecimal("5000")) < 0);
    }

    @Test
    public void testDischargingBelowSOCMin() {
        battery.setE_ESS_t(new BigDecimal("1200")); // Close to SOC_min
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("-2000"));
        assertTrue(result.compareTo(BigDecimal.ZERO) < 0); // Some gap not filled
        assertEquals(new BigDecimal("1020.0"), battery.getE_ESS_t().setScale(1, RoundingMode.HALF_UP));
    }
}
