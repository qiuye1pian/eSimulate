package org.core.model.result;

import org.esimulate.core.model.result.MomentResult;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class MomentResultTest {

    @Test
    public void testIsUnqualified() {
        List<Energy> validList = Arrays.asList(
                new ElectricEnergy(new BigDecimal("10")),
                new ElectricEnergy(new BigDecimal("5"))
        );

        List<Energy> invalidList = Arrays.asList(
                new ElectricEnergy(new BigDecimal("-10")),
                new ElectricEnergy(new BigDecimal("5"))
        );

        MomentResult validResult = new MomentResult(validList);
        MomentResult invalidResult = new MomentResult(invalidList);


    }
}