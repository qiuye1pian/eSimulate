package org.core.model.load.heat;

import org.core.pso.simulator.LoadData;

import java.math.BigDecimal;
import java.util.List;

public interface ThermalLoadData extends LoadData {

    List<BigDecimal> getThermalLoadData();

}
