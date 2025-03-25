package org.esimulate.core.pojo.load;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThermalLoadSchemeDto {

    private Long id;
    private String name;

    public ThermalLoadSchemeDto(ThermalLoadScheme thermalLoadScheme) {
        this.id = thermalLoadScheme.getId();
        this.name = thermalLoadScheme.getSchemeName();
    }

}
