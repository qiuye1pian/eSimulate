package org.esimulate.core.pojo.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TimeValue {

    LocalDateTime getTime();

    BigDecimal getValue();

}