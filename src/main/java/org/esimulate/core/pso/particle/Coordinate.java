package org.esimulate.core.pso.particle;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 位置中某个维度的值
 */
@Setter
@Getter
public class Coordinate implements Dimension, Cloneable {

    /**
     * 维度最小值
     */
    private BigDecimal lowerBound;
    /**
     * 维度最大值
     */
    private BigDecimal upperBound;
    /**
     * 当前值
     */
    private BigDecimal value;

    public Coordinate(Dimension x) {
        this.lowerBound = x.getLowerBound();
        this.upperBound = x.getUpperBound();
        this.value = BigDecimal.ZERO;
    }

    /**
     * 克隆
     * @return 新对象
     */
    @Override
    public Coordinate clone() {
        try {
            Coordinate clone = (Coordinate) super.clone();
            // 深拷贝 value，因为 BigDecimal 是可变对象
            clone.value = new BigDecimal(this.value.toString());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
