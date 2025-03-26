package org.esimulate.core.pojo.environment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.environment.gas.GasScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GasSchemeDto {

    private Long id;
    private String name;

    public GasSchemeDto(GasScheme scheme) {
        this.id = scheme.getId();
        this.name = scheme.getSchemeName();
    }

}
