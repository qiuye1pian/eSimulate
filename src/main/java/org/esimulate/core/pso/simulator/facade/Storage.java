package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

public interface Storage extends CarbonEmitter {

    List<Energy> storage(List<Energy> differenceList);

}
