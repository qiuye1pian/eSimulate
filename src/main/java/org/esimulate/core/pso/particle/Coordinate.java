package org.esimulate.core.pso.particle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 位置中某个维度的值
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate implements Dimension, Cloneable {

    private String modelName;

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
        this.modelName = x.getModelName();
        this.lowerBound = x.getLowerBound();
        this.upperBound = x.getUpperBound();
        this.value = this.lowerBound;
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
