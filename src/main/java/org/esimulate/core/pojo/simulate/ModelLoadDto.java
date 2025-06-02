package org.esimulate.core.pojo.simulate;

import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;

public interface ModelLoadDto {

    ModelTypeEnum getModelTypeEnum();

    Long getId();

    Integer getQuantity();
}
