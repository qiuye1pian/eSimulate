package org.core.pso.simulator.facade;

import org.core.pso.simulator.facade.environment.EnvironmentValue;

import java.math.BigDecimal;
import java.util.List;

public interface Producer {

    BigDecimal produce(List<EnvironmentValue> environmentValueList);

}