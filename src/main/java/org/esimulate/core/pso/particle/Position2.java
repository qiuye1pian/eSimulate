package org.esimulate.core.pso.particle;

import io.jsonwebtoken.lang.Collections;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Position2 implements Cloneable {

    /**
     * 粒子在各维度的坐标
     */
    private List<Coordinate2> coordinateList;

    public Position2(List<Dimension> dimensionsList) {
        coordinateList = dimensionsList.stream().map(Coordinate2::new).collect(Collectors.toList());
    }

    /**
     * 获取坐标的维度数量
     */
    public int getDimensionCount() {
        return Collections.isEmpty(coordinateList) ? 0 : coordinateList.size();
    }

    /**
     * 设置坐标在某维度的值
     */
    public void setAtDimension(int dimIndex, Integer newValue) {
        coordinateList.get(dimIndex).setValue(newValue);
    }

    public Integer getValueAt(int index) {
        return coordinateList.get(index).getValue();
    }

    /**
     * 判断是否相等（严格比较 BigDecimal，包括精度）
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position2)) {
            return false;
        }
        Position2 that = (Position2) o;
        if (this.coordinateList.size() != that.coordinateList.size()) {
            return false;
        }

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
    public Position2 clone() {
        try {
            // 创建浅拷贝
            Position2 cloned = (Position2) super.clone();
            // 深拷贝 coordinateList
            List<Coordinate2> clonedCoordinateList = this.coordinateList.stream()
                    .map(Coordinate2::clone) // 调用 Coordinate 的 clone 方法
                    .collect(Collectors.toList());
            // 设置拷贝后的坐标列表
            cloned.coordinateList.clear();
            cloned.coordinateList.addAll(clonedCoordinateList);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    public List<Integer> getCoordinateValueList() {
        return this.coordinateList.stream().map(Coordinate2::getValue).collect(Collectors.toList());
    }
}
