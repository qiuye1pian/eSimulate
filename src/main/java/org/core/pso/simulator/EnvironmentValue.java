package org.core.pso.simulator;

import java.math.BigDecimal;

public interface EnvironmentValue extends TimeSeriesValue {
    BigDecimal getValue();
}
