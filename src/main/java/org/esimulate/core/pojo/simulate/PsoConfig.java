package org.esimulate.core.pojo.simulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsoConfig {

    List<LoadDto> loadDtoList;

    List<ModelDimensionDto> modelDimensionDtoList;

    List<EnvironmentDto> environmentDtoList;

    // 粒子个数
    Integer particleCount;

    // 最大迭代次数
    Integer maxIterations;

    // 惯性权重 初值
    BigDecimal inertiaWeightStart;

    // 惯性权重 终值
    BigDecimal inertiaWeightEnd;

    // 自我学习因子 初值
    BigDecimal c1Start;

    // 自我学习因子 终值
    BigDecimal c1End;

    // 群体学习因子 初值
    BigDecimal c2Start;

    // 群体学习因子 终值
    BigDecimal c2End;

    // 最大弃风弃光率
    BigDecimal maxCurtailmentRate = BigDecimal.valueOf(80);

    // 最小可再生能源渗透率
    BigDecimal minRenewableEnergyShare = BigDecimal.valueOf(80);

}
