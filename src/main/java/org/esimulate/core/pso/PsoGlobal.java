package org.esimulate.core.pso;

import lombok.Data;
import org.esimulate.core.pso.particle.Position3;

import java.math.BigDecimal;

@Data
public class PsoGlobal {

    private Position3 globalBestPosition3;

    private BigDecimal globalBestValue;

}
