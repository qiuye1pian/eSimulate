package org.esimulate.core.model.device;

import java.math.BigDecimal;

public interface Device {

    BigDecimal getQuantity();
    void setQuantity(BigDecimal quantity);
}
