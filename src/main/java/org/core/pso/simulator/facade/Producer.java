package org.core.pso.simulator.facade;

import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

public interface Producer extends CarbonEmitter {

    Energy produce(List<EnvironmentValue> environmentValueList);

    BigDecimal getTotalEnergy();

}