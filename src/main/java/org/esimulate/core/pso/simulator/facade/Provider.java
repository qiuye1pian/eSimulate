package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

public interface Provider extends CarbonEmitter {

    Energy provide(List<Energy> afterStorageEnergyList);

    BigDecimal getTotalEnergy();

}
