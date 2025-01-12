package org.core.pso.simulator.facade.load;

import org.core.pso.simulator.facade.base.TimeSeriesData;

public interface LoadData extends TimeSeriesData {

    LoadValue getLoadValue(Integer timeIndex);

}
