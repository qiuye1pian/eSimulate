package org.esimulate.core.model.result.indication;

import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;

public class RenewableEnergyShare implements Indication {

    BigDecimal renewableEnergyShare;

    public RenewableEnergyShare(BigDecimal renewableEnergyShare) {
        this.renewableEnergyShare = renewableEnergyShare;
    }

    @Override
    public BigDecimal getIndication() {
        return renewableEnergyShare;
    }

}
