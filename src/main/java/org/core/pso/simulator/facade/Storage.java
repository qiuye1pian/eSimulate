package org.core.pso.simulator.facade;

import org.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

public interface Storage extends CarbonEmitter {

    Energy storage(List<Energy> differenceList);

}
