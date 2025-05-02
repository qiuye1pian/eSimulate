package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

public interface Producer extends CarbonEmitter {

    List<Energy> produce(List<EnvironmentValue> environmentValueList);

    BigDecimal getTotalEnergy();

}