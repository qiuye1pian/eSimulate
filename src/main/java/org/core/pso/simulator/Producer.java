package org.core.pso.simulator;

import java.util.List;

public interface Producer {

    void produce(List<TimeSeriesValue> timeSeriesValueList);
}
