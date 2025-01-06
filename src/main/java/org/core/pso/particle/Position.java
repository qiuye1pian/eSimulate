package org.core.pso.particle;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Position implements Cloneable {

    /**
     * 粒子在各维度的坐标
     */

    private final List<Coordinate> coordinateList;

    public Position(List<Dimension> dimensionsList) {
        coordinateList = dimensionsList.stream().map(Coordinate::new).collect(Collectors.toList());
    }

    /**
     * 获取坐标的维度数量
     */
    public int getDimensionCount() {
        return coordinateList.size();
    }

    /**
     * 设置坐标在某维度的值
     */
    public void setAtDimension(int dimIndex, BigDecimal newValue) {
        coordinateList.get(dimIndex).setValue(newValue);
    }

    /**
     * 根据维度顺序获取维度值
     *
     * @param i 第i个维度
     * @return 第i个维度的值
     */
    public BigDecimal getCoordinateByIndex(int i) {
        return coordinateList.get(i).getValue();
    }



    /**
     * 判断是否相等（严格比较 BigDecimal，包括精度）
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position that = (Position) o;
        if (this.coordinateList.size() != that.coordinateList.size()) return false;

        for (int i = 0; i < coordinateList.size(); i++) {
            // BigDecimal 的 equals 是严格比较，包括小数位
            if (!this.coordinateList.get(i).equals(that.coordinateList.get(i))) {
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
        return Arrays.hashCode(coordinateList.toArray());
    }


    /**
     * 深拷贝 Clone 方法
     */
    @Override
    public Position clone() {
        try {
            // 创建浅拷贝
            Position cloned = (Position) super.clone();
            // 深拷贝 coordinateList
            List<Coordinate> clonedCoordinateList = this.coordinateList.stream()
                    .map(Coordinate::clone) // 调用 Coordinate 的 clone 方法
                    .collect(Collectors.toList());
            // 设置拷贝后的坐标列表
            cloned.coordinateList.clear();
            cloned.coordinateList.addAll(clonedCoordinateList);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    public List<BigDecimal> getCoordinateValueList() {
        return this.coordinateList.stream().map(Coordinate::getValue).collect(Collectors.toList());
    }
}
