package org.esimulate.core.pso.particle;


import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;


@Getter
public class Velocity3 implements Cloneable {

    private BigDecimal[] velocities; // 粒子在各维度的速度

    public Velocity3(BigDecimal[] velocities) {
        this.velocities = velocities;

    }

    public int getDimensionCount() {
        return velocities.length;
    }

    public Velocity3 copy() {
        return new Velocity3(Arrays.copyOf(velocities, velocities.length));
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
        if (!(o instanceof Velocity3)) {
            return false;
        }
        Velocity3 that = (Velocity3) o;
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
    public Velocity3 clone() {
        try {
            Velocity3 cloned = (Velocity3) super.clone();
            // 深拷贝 velocities 数组
            cloned.velocities = Arrays.copyOf(this.velocities, this.velocities.length);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}