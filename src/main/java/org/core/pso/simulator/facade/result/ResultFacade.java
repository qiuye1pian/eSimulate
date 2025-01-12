package org.core.pso.simulator.facade.result;

import org.core.pso.simulator.facade.result.carbon.Carbon;
import org.core.pso.simulator.facade.result.energy.Energy;

public interface ResultFacade {
    Energy getEnergy();
    Carbon getCarbon();
}
