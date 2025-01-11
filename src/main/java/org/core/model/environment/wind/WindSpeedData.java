package org.core.model.environment.wind;

import org.core.pso.simulator.Environments;

import java.math.BigDecimal;
import java.util.List;

public interface WindSpeedData extends Environments {
    List<BigDecimal> getWindSpeedData();
}
