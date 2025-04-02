package org.esimulate.core.model.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testChargingWithinLimits() {
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("2000"));
        assertTrue(result.compareTo(BigDecimal.ZERO) < 0);
        assertTrue(battery.getE_ESS_t().compareTo(new BigDecimal("5000")) > 0);
    }

    @Test
    public void testChargingBeyondSOCMax() {
        battery.setE_ESS_t(new BigDecimal("8800")); // Close to SOC_max
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("2000"));
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0); // Some power left unused
        assertEquals(new BigDecimal("9000.0"), battery.getE_ESS_t().setScale(1));
    }

    @Test
    public void testDischargingWithinLimits() {
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("-2000"));
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(battery.getE_ESS_t().compareTo(new BigDecimal("5000")) < 0);
    }

    @Test
    public void testDischargingBelowSOCMin() {
        battery.setE_ESS_t(new BigDecimal("1200")); // Close to SOC_min
        BigDecimal result = battery.testUpdateElectricEnergy(new BigDecimal("-2000"));
        assertTrue(result.compareTo(BigDecimal.ZERO) < 0); // Some gap not filled
        assertEquals(new BigDecimal("1000.0"), battery.getE_ESS_t().setScale(1));
    }
}
