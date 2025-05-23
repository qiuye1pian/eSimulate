package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

public interface Adjustable extends CarbonEmitter {

    void adjustable(List<Energy> afterStorageEnergyList);

    List<Energy> getAdjustTotalEnergy();

}
