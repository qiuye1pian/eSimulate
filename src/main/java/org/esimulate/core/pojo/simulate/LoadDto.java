package org.esimulate.core.pojo.simulate;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.enums.LoadTypeEnum;

@Data
@NoArgsConstructor
public class LoadDto {

    LoadTypeEnum loadTypeEnum;

    Long id;

}
