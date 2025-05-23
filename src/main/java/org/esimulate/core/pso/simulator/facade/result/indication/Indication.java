package org.esimulate.core.pso.simulator.facade.result.indication;

import java.math.BigDecimal;

public interface Indication {

    String getIndicationName();

    String getDescription();

    BigDecimal getIndication();

}
