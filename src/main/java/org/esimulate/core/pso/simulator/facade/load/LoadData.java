package org.esimulate.core.pso.simulator.facade.load;

import org.esimulate.core.pso.simulator.facade.base.TimeSeriesData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface LoadData extends TimeSeriesData {

    LoadValue getLoadValue(Integer timeIndex);

    List<LocalDateTime> getDatetimeList();

    String getLoadName();

    List<BigDecimal> getLoadValueList();
}
