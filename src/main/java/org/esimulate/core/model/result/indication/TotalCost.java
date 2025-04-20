package org.esimulate.core.model.result.indication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalCost implements Indication {

    BigDecimal totalCostValue;

    @Override
    public String getIndicationName() {
        return "TotalCost";
    }

    @Override
    public String getDescription() {
        return "年度总成本";
    }

    @Override
    public BigDecimal getIndication() {
        return totalCostValue;
    }
}
