package org.core.pso.dto;


import lombok.Data;
import org.core.pso.particle.Dimension;
import org.core.pso.particle.EnvironmentLoad;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PSOParameters {

    private List<Dimension> dimensionList; //维度列表
    private List<EnvironmentLoad> environmentLoadList;
    private int particleCount; // 粒子数
    private int maxIterations; // 最大迭代次数
    private BigDecimal inertiaWeight; // 惯性权重
    private BigDecimal c1; // 自我学习因子
    private BigDecimal c2; // 群体学习因子

    public PSOParameters(List<Dimension> dimensionList,
                         List<EnvironmentLoad> environmentLoadList,
                         int particleCount,
                         int maxIterations,
                         double inertiaWeight,
                         double c1, double c2) {
        this.dimensionList = dimensionList;
        this.environmentLoadList = environmentLoadList;
        this.particleCount = particleCount;
        this.maxIterations = maxIterations;
        this.inertiaWeight = BigDecimal.valueOf(inertiaWeight);
        this.c1 = BigDecimal.valueOf(c1);
        this.c2 = BigDecimal.valueOf(c2);
    }

    public int getDimensionCount() {
        return dimensionList.size();
    }


}