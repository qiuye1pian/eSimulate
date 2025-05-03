package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

public interface Adjustable extends CarbonEmitter {

    List<Energy> adjustable(List<Energy> afterStorageEnergyList);

    BigDecimal getAdjustTotalEnergy();

}
