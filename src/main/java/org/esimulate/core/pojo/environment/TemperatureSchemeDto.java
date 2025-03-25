package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.temperature.TemperatureScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureSchemeDto {

    private Long id;
    private String name;

    public TemperatureSchemeDto(TemperatureScheme scheme) {
        this.id = scheme.getId();
        this.name = scheme.getSchemeName();
    }
}
