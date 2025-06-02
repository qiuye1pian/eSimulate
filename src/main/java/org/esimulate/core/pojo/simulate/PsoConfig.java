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

    // 惯性权重
    BigDecimal inertiaWeight;

    // 自我学习因子
    BigDecimal c1;

    // 群体学习因子
    BigDecimal c2;

}
