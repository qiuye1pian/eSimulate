package org.esimulate.core.pso.simulator.facade;

import java.math.BigDecimal;

public interface Device {

    BigDecimal getQuantity();

    void setQuantity(BigDecimal quantity);

    BigDecimal getPurchaseCost();

//    BigDecimal getTotalCost();
}
