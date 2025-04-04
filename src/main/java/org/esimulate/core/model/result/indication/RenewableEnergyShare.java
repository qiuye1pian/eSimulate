package org.esimulate.core.model.result.indication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenewableEnergyShare implements Indication {

    BigDecimal renewableEnergyShare;

    @Override
    public String getIndicationName() {
        return "RenewableEnergyShare";
    }

    @Override
    public String getDescription() {
        return "可再生能源占比";
    }

    @Override
    public BigDecimal getIndication() {
        return renewableEnergyShare;
    }

}
