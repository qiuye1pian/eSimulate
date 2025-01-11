package org.core.pso.simulator;

public interface TimeSeriesData {

    int getDataLength();

    TimeSeriesValue getTimeSeriesValue(Integer timeIndex);
}
