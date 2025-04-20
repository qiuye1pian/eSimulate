package org.esimulate.core.pso.simulator.facade.load;

import org.esimulate.core.pso.simulator.facade.base.TimeSeriesData;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadData extends TimeSeriesData {

    LoadValue getLoadValue(Integer timeIndex);

    List<LocalDateTime> getDatetimeList();
}
