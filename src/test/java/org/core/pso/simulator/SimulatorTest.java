package org.core.pso.simulator;

import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class SimulatorTest {

    @Test
    public void test_01() {

        List<ElectricLoadValue> electricLoadValues = Arrays.asList(
                new ElectricLoadValue(LocalDateTime.of(2023, 1, 1, 10, 0,0), new BigDecimal("500")),
                new ElectricLoadValue(LocalDateTime.of(2023, 1, 1, 9, 0,0), new BigDecimal("600")),
                new ElectricLoadValue(LocalDateTime.of(2023, 1, 1, 11, 0,0), new BigDecimal("400"))
        );

        List<SunlightIrradianceValue> sunlightIrradianceValues = Arrays.asList(
                new SunlightIrradianceValue(LocalDateTime.of(2023, 1, 1, 10, 0,0), new BigDecimal("500")),
                new SunlightIrradianceValue(LocalDateTime.of(2023, 1, 1, 9, 0,0), new BigDecimal("600")),
                new SunlightIrradianceValue(LocalDateTime.of(2023, 1, 1, 11, 0,0), new BigDecimal("400"))
        );
    }
}