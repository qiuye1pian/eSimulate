package org.esimulate.core.pso.dto;

import lombok.Getter;
import org.esimulate.core.pso.particle.Dimension;

import java.math.BigDecimal;

@Getter
public class BoundedDimension implements Dimension {

    private final BigDecimal lowerBound;

    private final BigDecimal upperBound;

    public BoundedDimension(BigDecimal lowerBound, BigDecimal upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

}
