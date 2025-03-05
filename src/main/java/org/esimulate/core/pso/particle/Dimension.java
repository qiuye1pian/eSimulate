package org.esimulate.core.pso.particle;

import java.math.BigDecimal;

public interface Dimension {
    BigDecimal getLowerBound(); // 获取维度的下界
    BigDecimal getUpperBound(); // 获取维度的上界
}