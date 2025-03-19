package org.esimulate.core.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.load.electric.ElectricLoadScheme;

@Data
@NoArgsConstructor
public class ElectricLoadSchemeDto {

    private Long id;
    private String name;

    public ElectricLoadSchemeDto(ElectricLoadScheme scheme) {
        this.id = scheme.getId();
        this.name = scheme.getSchemeName();
    }

}
