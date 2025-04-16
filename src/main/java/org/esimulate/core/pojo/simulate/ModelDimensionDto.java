package org.esimulate.core.pojo.simulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;
import org.esimulate.core.pso.particle.Dimension;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDimensionDto implements Dimension, ModelLoadDto {

    ModelTypeEnum modelTypeEnum;

    Long id;

    BigDecimal lowerBound = BigDecimal.ONE;

    BigDecimal upperBound = BigDecimal.valueOf(1000);

    @Override
    public BigDecimal getQuantity() {
        return lowerBound;
    }
}
