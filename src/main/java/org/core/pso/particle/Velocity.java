package org.core.pso.particle;


import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;


public class Velocity {

    @Getter
    private final BigDecimal[] components; // 粒子在各维度的速度

    public Velocity(BigDecimal[] components) {
        this.components = components;
    }

    public int getDimensionCount() {
        return components.length;
    }

    public Velocity copy() {
        return new Velocity(Arrays.copyOf(components, components.length));
    }

    public void addAtDimension(int dimIndex, BigDecimal valueToAdd) {
        components[dimIndex] = components[dimIndex].add(valueToAdd);
    }

    public void setAtDimension(int dimIndex, BigDecimal newValue, int scale, RoundingMode roundingMode) {
        components[dimIndex] = newValue.setScale(scale, roundingMode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Velocity)) return false;
        Velocity that = (Velocity) o;
        if (this.components.length != that.components.length) return false;

        for (int i = 0; i < components.length; i++) {
            if (!this.components[i].equals(that.components[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(components);
    }
}