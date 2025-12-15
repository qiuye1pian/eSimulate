package org.esimulate.core.pso.particle;


import lombok.Data;

import java.util.Arrays;


@Data
public class Velocity implements Cloneable {

    private Integer[] velocities; // 粒子在各维度的速度

    public Velocity(Integer[] velocities) {
        this.velocities = velocities;
    }

    public int getDimensionCount() {
        return velocities.length;
    }

    public Velocity copy() {
        return new Velocity(Arrays.copyOf(velocities, velocities.length));
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