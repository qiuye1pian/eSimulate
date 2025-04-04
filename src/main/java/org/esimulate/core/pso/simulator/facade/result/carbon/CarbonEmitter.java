package org.esimulate.core.pso.simulator.facade.result.carbon;

import org.esimulate.core.pso.simulator.facade.Device;

import java.math.BigDecimal;

public interface CarbonEmitter extends Device {

    BigDecimal calculateCarbonEmissions();

}
