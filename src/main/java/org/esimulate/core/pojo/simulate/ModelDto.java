package org.esimulate.core.pojo.simulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.enums.ModelTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {

    ModelTypeEnum modelTypeEnum;

    Long id;

    Integer quantity;

}
