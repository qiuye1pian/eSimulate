package org.esimulate.core.pso;

import lombok.Data;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.particle.Position;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PsoGlobal {

    private Position globalBestPosition;

    private BigDecimal globalBestValue;

    public void updateGlobal(List<SimulateResult> optimizeResult) {
        optimizeResult.
    }
}
