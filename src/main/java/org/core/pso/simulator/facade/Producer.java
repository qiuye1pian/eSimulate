package org.core.pso.simulator.facade;

import org.core.pso.simulator.facade.result.energy.Energy;
import org.core.pso.simulator.facade.environment.EnvironmentValue;

import java.util.List;

public interface Producer {

    Energy produce(List<EnvironmentValue> environmentValueList);

}