package org.core.pso.simulator;

import java.math.BigDecimal;
import java.util.List;

public interface Producer {

    BigDecimal produce(List<EnvironmentValue> environmentValueList);
}
