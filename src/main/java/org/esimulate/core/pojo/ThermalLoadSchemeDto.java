package org.esimulate.core.pojo;

import lombok.Data;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;

@Data
public class ThermalLoadSchemeDto {

    public ThermalLoadSchemeDto(ThermalLoadScheme thermalLoadScheme) {
        this.id = thermalLoadScheme.getId();
        this.name = thermalLoadScheme.getSchemeName();
    }

    private Long id;
    private String name;
}
