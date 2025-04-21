package org.esimulate.core.pso.simulator.facade;

import org.esimulate.core.pojo.simulate.result.StackedChartData;

import java.util.List;

public interface ElectricDevice {
    List<StackedChartData> getStackedChartDataList();
}
