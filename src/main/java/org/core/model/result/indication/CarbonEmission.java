package org.core.model.result.indication;

import org.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;

public class CarbonEmission implements Indication {

    BigDecimal totalCarbonEmission;

    public CarbonEmission(BigDecimal totalCarbonEmission) {
        this.totalCarbonEmission = totalCarbonEmission;
    }


    @Override
    public BigDecimal getIndication() {
        return null;
    }
}
