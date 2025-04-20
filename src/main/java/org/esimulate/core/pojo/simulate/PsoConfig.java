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

    Integer particleCount;

    Integer maxIterations;

    BigDecimal inertiaWeight;

    BigDecimal c1;

    BigDecimal c2;

}
