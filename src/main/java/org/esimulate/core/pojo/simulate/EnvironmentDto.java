package org.esimulate.core.pojo.simulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.enums.EnvironmentTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentDto {

    EnvironmentTypeEnum environmentTypeEnum;

    Long id;

}
