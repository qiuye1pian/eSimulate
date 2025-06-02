package org.esimulate.core.pso.particle;

import io.jsonwebtoken.lang.Collections;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Position3 implements Cloneable {

    /**
     * 粒子在各维度的坐标
     */
    private final List<Coordinate3> coordinate3List;

    public Position3(List<Dimension> dimensionsList) {
        coordinate3List = dimensionsList.stream().map(Coordinate3::new).collect(Collectors.toList());
    }

    /**
     * 获取坐标的维度数量
     */
    public int getDimensionCount() {
        return Collections.isEmpty(coordinate3List) ? 0 : coordinate3List.size();
    }

    /**
     * 设置坐标在某维度的值
     */
    public void setAtDimension(int dimIndex, BigDecimal newValue) {
        coordinate3List.get(dimIndex).setValue(newValue);
    }

    /**
     * 根据维度顺序获取维度值
     *
     * @param i 第i个维度
     * @return 第i个维度的值
     */
    public BigDecimal getCoordinateByIndex(int i) {
        return coordinate3List.get(i).getValue();
    }


    /**
     * 判断是否相等（严格比较 BigDecimal，包括精度）
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position3)) {
            return false;
        }
        Position3 that = (Position3) o;
        if (this.coordinate3List.size() != that.coordinate3List.size()) {
            return false;
        }

        for (int i = 0; i < coordinate3List.size(); i++) {
            // BigDecimal 的 equals 是严格比较，包括小数位
            if (!this.coordinate3List.get(i).equals(that.coordinate3List.get(i))) {
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
        return Arrays.hashCode(coordinate3List.toArray());
    }


    /**
     * 深拷贝 Clone 方法
     */
    @Override
    public Position3 clone() {
        try {
            // 创建浅拷贝
            Position3 cloned = (Position3) super.clone();
            // 深拷贝 coordinateList
            List<Coordinate3> clonedCoordinate3List = this.coordinate3List.stream()
                    .map(Coordinate3::clone) // 调用 Coordinate 的 clone 方法
                    .collect(Collectors.toList());
            // 设置拷贝后的坐标列表
            cloned.coordinate3List.clear();
            cloned.coordinate3List.addAll(clonedCoordinate3List);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    public List<BigDecimal> getCoordinateValueList() {
        return this.coordinate3List.stream().map(Coordinate3::getValue).collect(Collectors.toList());
    }
}
