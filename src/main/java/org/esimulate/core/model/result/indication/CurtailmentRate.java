package org.esimulate.core.model.result.indication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurtailmentRate implements Indication {

    BigDecimal curtailmentRate;

    @Override
    public String getIndicationName() {
        return "CurtailmentRate";
    }

    @Override
    public String getDescription() {
        return "弃风弃光率";
    }

    @Override
    public BigDecimal getIndication() {
        return curtailmentRate;
    }
}
