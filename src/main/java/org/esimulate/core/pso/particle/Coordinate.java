package org.esimulate.core.pso.particle;

import lombok.Data;

import java.util.Random;

/**
 * 位置中某个维度的值
 */
@Data
public class Coordinate implements Dimension, Cloneable {

    /**
     * 维度最小值
     */
    private Integer lowerBound;
    /**
     * 维度最大值
     */
    private Integer upperBound;

    /**
     * 当前值
     */
    private Integer value;

    public Coordinate(Dimension x) {
        Random random = new Random();
        this.lowerBound = x.getLowerBound();
        this.upperBound = x.getUpperBound();
        this.value = lowerBound + random.nextInt(upperBound) / 2;
    }

    /**
     * 克隆
     *
     * @return 新对象
     */
    @Override
    public Coordinate clone() {
        try {
            Coordinate clone = (Coordinate) super.clone();
            // 深拷贝 value，因为 BigDecimal 是可变对象
            clone.value = new Integer(this.value.toString());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
