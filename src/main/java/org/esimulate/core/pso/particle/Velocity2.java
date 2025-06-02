package org.esimulate.core.pso.particle;


import lombok.Getter;

import java.util.Arrays;


@Getter
public class Velocity2 implements Cloneable {

    private Integer[] velocities; // 粒子在各维度的速度

    public Velocity2(Integer[] velocities) {
        this.velocities = velocities;
    }

    public int getDimensionCount() {
        return velocities.length;
    }

    public Velocity2 copy() {
        return new Velocity2(Arrays.copyOf(velocities, velocities.length));
    }

    public void setAtDimension(int dimIndex, Integer newValue) {
        velocities[dimIndex] = newValue;
    }

    public Integer getVelocityAt(int index) {
        return this.velocities[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Velocity2)) {
            return false;
        }
        Velocity2 that = (Velocity2) o;
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
    public Velocity2 clone() {
        try {
            Velocity2 cloned = (Velocity2) super.clone();
            // 深拷贝 velocities 数组
            cloned.velocities = Arrays.copyOf(this.velocities, this.velocities.length);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}