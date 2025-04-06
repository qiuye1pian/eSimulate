package org.esimulate.core.pso.simulator.facade.result.energy;

import java.math.BigDecimal;

public interface Energy {

    BigDecimal getValue();

    String getEnergyType();

    Energy add(Energy energy);
}
