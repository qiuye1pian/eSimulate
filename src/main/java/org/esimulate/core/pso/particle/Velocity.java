package org.esimulate.core.pso.particle;


import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;


@Getter
public class Velocity implements Cloneable {

    private BigDecimal[] velocities; // 粒子在各维度的速度

    public Velocity(BigDecimal[] velocities) {
        this.velocities = velocities;
    }

    public int getDimensionCount() {
        return velocities.length;
    }

    public Velocity copy() {
        return new Velocity(Arrays.copyOf(velocities, velocities.length));
    }

    public void addAtDimension(int dimIndex, BigDecimal valueToAdd) {
        velocities[dimIndex] = velocities[dimIndex].add(valueToAdd);
    }

    public void setAtDimension(int dimIndex, BigDecimal newValue) {
        velocities[dimIndex] = newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Velocity)) {
            return false;
        }
        Velocity that = (Velocity) o;
        if (this.velocities.length != that.velocities.length) {
            return false;
        }

        for (int i = 0; i < velocities.length; i++) {
            if (!this.velocities[i].equals(that.velocities[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(velocities);
    }

    /**
     * 深拷贝 Clone 方法
     */
    @Override
    public Velocity clone() {
        try {
            Velocity cloned = (Velocity) super.clone();
            // 深拷贝 velocities 数组
            cloned.velocities = Arrays.copyOf(this.velocities, this.velocities.length);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}