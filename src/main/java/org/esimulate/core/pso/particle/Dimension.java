package org.esimulate.core.pso.particle;

import java.math.BigDecimal;

public interface Dimension {

    void setLowerBound(BigDecimal lowerBound); // 获取维度的下界

    void setUpperBound(BigDecimal upperBound); // 获取维度的上界

    BigDecimal getLowerBound(); // 获取维度的下界

    BigDecimal getUpperBound(); // 获取维度的上界
}