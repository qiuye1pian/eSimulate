package org.core.pso.simulator.facade.load;

import org.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

public interface LoadValue {
    Energy calculateDifference(List<Energy> produceList);
}
