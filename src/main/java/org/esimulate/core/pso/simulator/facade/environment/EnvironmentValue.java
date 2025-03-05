package org.esimulate.core.pso.simulator.facade.environment;

import org.esimulate.core.pso.simulator.facade.base.TimeSeriesValue;

import java.math.BigDecimal;

public interface EnvironmentValue extends TimeSeriesValue {

    BigDecimal getValue();

}
