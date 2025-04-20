package org.esimulate.core.pojo.simulate;

import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;

import java.math.BigDecimal;

public interface ModelLoadDto {

    ModelTypeEnum getModelTypeEnum();

    Long getId();

    BigDecimal getQuantity();
}
