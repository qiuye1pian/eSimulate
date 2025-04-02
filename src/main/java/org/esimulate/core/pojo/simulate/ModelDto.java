package org.esimulate.core.pojo.simulate;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;

@Data
@NoArgsConstructor
public class ModelDto {

    ModelTypeEnum modelTypeEnum;

    Long id;

}
