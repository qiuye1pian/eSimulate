package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceScheme;
import org.esimulate.core.model.environment.water.WaterSpeedScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSpeedSchemeDto {

    private Long id;
    private String name;

    public WaterSpeedSchemeDto(WaterSpeedScheme scheme) {
        this.id = scheme.getId();
        this.name = scheme.getSchemeName();
    }


}
