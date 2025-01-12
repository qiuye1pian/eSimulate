package org.core.pso.simulator.facade.environment;

import org.core.pso.simulator.facade.base.TimeSeriesData;

public interface EnvironmentData extends TimeSeriesData {

    EnvironmentValue getEnvironmentValue(Integer timeIndex);

}
