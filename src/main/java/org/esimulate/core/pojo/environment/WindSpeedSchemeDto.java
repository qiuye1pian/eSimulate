package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.wind.WindSpeedScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WindSpeedSchemeDto {

    private Long id;

    private String name;

    public WindSpeedSchemeDto(WindSpeedScheme scheme) {
        this.id = scheme.getId();
        this.name = scheme.getSchemeName();
    }

}
