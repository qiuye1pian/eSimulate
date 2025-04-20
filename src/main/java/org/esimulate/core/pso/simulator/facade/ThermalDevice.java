package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pojo.simulate.result.StackedChartData;

import java.util.List;

public interface ThermalDevice {
    public abstract List<StackedChartData> getStackedChartDataList();
}
