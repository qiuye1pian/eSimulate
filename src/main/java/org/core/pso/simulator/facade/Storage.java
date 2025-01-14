package org.core.pso.simulator.facade;

import org.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

public interface Storage {

    Energy storage(List<Energy> produceList);

}
