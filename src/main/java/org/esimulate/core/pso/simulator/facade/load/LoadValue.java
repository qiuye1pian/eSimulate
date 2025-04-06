package org.esimulate.core.pso.simulator.facade.load;

import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;

public interface LoadValue {

    Energy calculateDifference(List<Energy> produceList);

    LoadValue add(LoadValue loadValue);

    BigDecimal getLoadValue();
}
