package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pojo.model.GridModelDto;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.ElectricDevice;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pojo.simulate.result.StackedChartData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "grid_model")
@AllArgsConstructor
@NoArgsConstructor
public class GridModel extends Device implements Provider, ElectricDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    @Column(nullable = false)
    private BigDecimal gridPrice;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    @Transient
    // 电网购买记录 (kW)
    private List<Energy> gridOutPutList = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public GridModel(GridModelDto gridModelDto) {
        this.modelName = gridModelDto.getModelName();
        this.gridPrice = gridModelDto.getGridPrice();
        this.carbonEmissionFactor = gridModelDto.getCarbonEmissionFactor();
    }

    /**
     * 提供电力
     * @param afterStorageEnergyList 经过储能处理后的电能
     * @return 通过电网补齐之后的剩余电力
     */
    @Override
    public Energy provide(List<Energy> afterStorageEnergyList) {
        BigDecimal afterStorageElectricEnergy = afterStorageEnergyList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 如果电力有冗余，返回冗余
        if (afterStorageElectricEnergy.compareTo(BigDecimal.ZERO) >= 0) {
            this.gridOutPutList.add(new ElectricEnergy(BigDecimal.ZERO));
            return new ElectricEnergy(afterStorageElectricEnergy);
        }

        // 从电网买电
        this.gridOutPutList.add(new ElectricEnergy(afterStorageElectricEnergy.abs()));

        return new ElectricEnergy(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTotalEnergy() {
        return gridOutPutList.stream().map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return this.getTotalEnergy()
                .multiply(carbonEmissionFactor)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal getPurchaseCost() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.ONE;
    }

    @Override
    protected Integer getLifetimeYears() {
        return 1;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return this.getTotalEnergy()
                .multiply(gridPrice)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        List<BigDecimal> collect = this.gridOutPutList.stream().map(Energy::getValue).collect(Collectors.toList());
        StackedChartData stackedChartData = new StackedChartData(String.format("电网购电: %s", this.modelName), collect, 100);
        return Collections.singletonList(stackedChartData);
    }

    @Override
    public GridModel clone() {
        GridModel clone = (GridModel) super.clone();

        // 深拷贝 BigDecimal
        clone.gridPrice = new BigDecimal(this.gridPrice.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // 字符串字段直接复制（不可变类型）
        clone.modelName = this.modelName;

        // id 字段，如保留
        clone.id = this.id;

        return clone;
    }
}
