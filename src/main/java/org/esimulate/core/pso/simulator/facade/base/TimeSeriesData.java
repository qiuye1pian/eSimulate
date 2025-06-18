package org.esimulate.core.pso.simulator.facade.base;

public interface TimeSeriesData {

    int getDataLength();

    TimeSeriesData cutOffMoreThan(int i);

}
