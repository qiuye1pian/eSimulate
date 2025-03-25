package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SunlightIrradianceSchemeDto {

    private Long id;
    private String name;

    public SunlightIrradianceSchemeDto(SunlightIrradianceScheme scheme) {
        this.id = scheme.getId();
        this.name = scheme.getSchemeName();
    }

}
