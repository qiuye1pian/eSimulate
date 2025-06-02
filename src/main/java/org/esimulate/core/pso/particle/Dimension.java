package org.esimulate.core.pso.particle;

public interface Dimension {

    void setLowerBound(Integer lowerBound); // 获取维度的下界

    void setUpperBound(Integer upperBound); // 获取维度的上界

    Integer getLowerBound(); // 获取维度的下界

    Integer getUpperBound(); // 获取维度的上界
}