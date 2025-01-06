package org.core.pso.particle;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Getter
public class Position implements Cloneable {

    private BigDecimal[] coordinates; // 粒子在各维度的坐标

    public Position(BigDecimal[] coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * 获取坐标的维度数量
     */
    public int getDimensionCount() {
        return coordinates.length;
    }

    /**
     * 拷贝一个新的 Position
     */
    public Position copy() {
        return new Position(Arrays.copyOf(coordinates, coordinates.length));
    }

    /**
     * 在指定坐标维度上加一个值（可用于更新坐标）
     */
    public void addAtDimension(int dimIndex, BigDecimal valueToAdd) {
        coordinates[dimIndex] = coordinates[dimIndex].add(valueToAdd);
    }

    /**
     * 设置坐标在某维度的值并保留指定小数位
     */
    public void setAtDimension(int dimIndex, BigDecimal newValue, int scale, RoundingMode roundingMode) {
        coordinates[dimIndex] = newValue.setScale(scale, roundingMode);
    }

    public BigDecimal getCoordinateByIndex(int i) {
        return coordinates[i];
    }

    /**
     * 判断是否相等（严格比较 BigDecimal，包括精度）
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position that = (Position) o;
        if (this.coordinates.length != that.coordinates.length) return false;

        for (int i = 0; i < coordinates.length; i++) {
            // BigDecimal 的 equals 是严格比较，包括小数位
            if (!this.coordinates[i].equals(that.coordinates[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 如果重写了 equals，最好也重写 hashCode
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates);
    }


    /**
     * 深拷贝 Clone 方法
     */
    @Override
    public Position clone() {
        try {
            Position cloned = (Position) super.clone();
            // 深拷贝 coordinates 数组
            cloned.coordinates = Arrays.copyOf(this.coordinates, this.coordinates.length);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
