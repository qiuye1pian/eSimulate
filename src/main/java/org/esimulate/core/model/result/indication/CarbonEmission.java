package org.esimulate.core.model.result.indication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonEmission implements Indication {

    BigDecimal totalCarbonEmission;

    @Override
    public String getIndicationName() {
        return "CarbonEmission";
    }

    @Override
    public String getDescription() {
        return "碳排放总量";
    }

    @Override
    public BigDecimal getIndication() {
        return totalCarbonEmission;
    }
}
