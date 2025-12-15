package org.esimulate.core.pojo.simulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;
import org.esimulate.core.pso.particle.Dimension;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDimensionDto implements Dimension, ModelLoadDto {

    String modelName;

    ModelTypeEnum modelTypeEnum;

    Long id;

    Integer lowerBound = 1;

    Integer upperBound = 2000;

    @Override
    public Integer getQuantity() {
        return lowerBound;
    }
}
