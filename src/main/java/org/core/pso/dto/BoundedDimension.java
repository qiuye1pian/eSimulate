package org.core.pso.dto;

import lombok.Getter;
import org.core.pso.particle.Dimension;

import java.math.BigDecimal;

public class BoundedDimension implements Dimension {

    @Getter
    private final BigDecimal lowerBound;
    @Getter
    private final BigDecimal upperBound;

    public BoundedDimension(double lowerBound, double upperBound) {
        this.lowerBound = BigDecimal.valueOf(lowerBound);
        this.upperBound = BigDecimal.valueOf(upperBound);
    }

}
