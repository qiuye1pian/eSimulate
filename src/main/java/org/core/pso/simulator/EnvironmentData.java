package org.core.pso.simulator;

import java.util.List;

public interface EnvironmentData extends TimeSeriesData {

    EnvironmentValue getEnvironmentValueList(Integer timeIndex);

}
