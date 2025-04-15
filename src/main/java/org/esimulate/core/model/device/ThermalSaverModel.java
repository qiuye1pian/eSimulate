package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.result.energy.ThermalEnergy;
import org.esimulate.core.pojo.model.ThermalSaverModelDto;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pso.simulator.result.StackedChartData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Entity
@Table(name = "thermal_saver_model")
@AllArgsConstructor
@NoArgsConstructor
public class ThermalSaverModel extends Device implements Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    // 总热储能容量（例如单位：kWh）
    @Column(nullable = false)
    private BigDecimal totalStorageCapacity;

    // 当前热储能量
    @Column(nullable = false)
    private BigDecimal currentStorage;

    // 储热效率（例如：0.9表示90%的储热效率）
    @Column(nullable = false)
    private BigDecimal chargingEfficiency;

    // 放热效率（例如：0.85表示85%的放热效率）
    @Column(nullable = false)
    private BigDecimal dischargingEfficiency;

    // 热损失率（例如：0.05表示每个时段损失5%的储热能量）
    @Column(nullable = false)
    private BigDecimal thermalLossRate;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 维护成本
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 每个时刻热存储的剩余热量
    @Transient
    private List<ThermalEnergy> E_ESS_LIST = new ArrayList<>();

    @Transient
    private List<BigDecimal> chargingList = new ArrayList<>();

    @Transient
    private List<BigDecimal> disChargingList = new ArrayList<>();

    public ThermalSaverModel(ThermalSaverModelDto thermalSaverModelDto) {
        this.id = thermalSaverModelDto.getId();
        this.modelName = thermalSaverModelDto.getModelName();
        this.totalStorageCapacity = thermalSaverModelDto.getTotalStorageCapacity();
        this.currentStorage = thermalSaverModelDto.getCurrentStorage();
        this.chargingEfficiency = thermalSaverModelDto.getChargingEfficiency();
        this.dischargingEfficiency = thermalSaverModelDto.getDischargingEfficiency();
        this.thermalLossRate = thermalSaverModelDto.getThermalLossRate();
        this.carbonEmissionFactor = thermalSaverModelDto.getCarbonEmissionFactor();
        this.cost = thermalSaverModelDto.getCost();
        this.purchaseCost = thermalSaverModelDto.getPurchaseCost();
    }

    @Override
    public Energy storage(List<Energy> differenceList) {
        // 计算热能差值（正值表示有多余热能需储存，负值表示需从储能中释放）
        BigDecimal thermalEnergyDifference = differenceList.stream()
                .filter(x -> x instanceof ThermalEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 先将参数按个数扩大
        totalStorageCapacity = totalStorageCapacity.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        currentStorage = currentStorage.multiply(quantity).setScale(2, RoundingMode.HALF_UP);

        // 应用热损失
        currentStorage = currentStorage.multiply(BigDecimal.ONE.subtract(thermalLossRate)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal actualChange;
        BigDecimal effective;

        if (thermalEnergyDifference.compareTo(BigDecimal.ZERO) > 0) {
            // 储热：将热能存入储能中，考虑充热效率
            BigDecimal potentialChange = thermalEnergyDifference.multiply(chargingEfficiency).setScale(2, RoundingMode.HALF_UP);
            BigDecimal maxAddable = totalStorageCapacity.subtract(currentStorage);
            actualChange = potentialChange.min(maxAddable);
            currentStorage = currentStorage.add(actualChange);
            // 实际输入热能 = 实际储能 / 效率
            effective = actualChange.divide(chargingEfficiency, RoundingMode.HALF_UP);

        } else if (thermalEnergyDifference.compareTo(BigDecimal.ZERO) < 0) {
            // 放热：从储能中释放热能，考虑放热效率
            BigDecimal potentialChange = thermalEnergyDifference.divide(dischargingEfficiency,2, RoundingMode.HALF_UP);
            BigDecimal maxReleasable = currentStorage.min(potentialChange.abs());
            actualChange = maxReleasable.negate();
            currentStorage = currentStorage.add(actualChange);
            // 实际输出热能 = 实际放热 × 效率
            effective = actualChange.multiply(dischargingEfficiency).setScale(2, RoundingMode.HALF_UP);

        } else {
            actualChange = BigDecimal.ZERO;
            effective = actualChange;
        }

        chargingList.add(actualChange.compareTo(BigDecimal.ZERO) > 0 ? actualChange : BigDecimal.ZERO);
        disChargingList.add(actualChange.compareTo(BigDecimal.ZERO) < 0 ? actualChange.abs() : BigDecimal.ZERO);

        // 再次边界控制防止数值精度误差
        if (currentStorage.compareTo(BigDecimal.ZERO) < 0) {
            currentStorage = BigDecimal.ZERO;
        } else if (currentStorage.compareTo(totalStorageCapacity) > 0) {
            currentStorage = totalStorageCapacity;
        }

        // 记录当前储能
        E_ESS_LIST.add(new ThermalEnergy(currentStorage));

        // 先将参数按个数扩大
        totalStorageCapacity = totalStorageCapacity.divide(quantity, 2, RoundingMode.HALF_UP);
        currentStorage = currentStorage.divide(quantity, 2, RoundingMode.HALF_UP);

        // 返回剩余未处理的热能差值
        thermalEnergyDifference = thermalEnergyDifference.subtract(effective);
        return new ThermalEnergy(thermalEnergyDifference);
    }

    @Override
    public BigDecimal calculateCarbonEmissions() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getDiscountRate() {
        return BigDecimal.valueOf(0.07);
    }

    @Override
    protected Integer getLifetimeYears() {
        return 25;
    }

    @Override
    protected BigDecimal getCostOfOperation() {
        BigDecimal chargingTotal = this.chargingList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal disChargingTotal = this.disChargingList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        return chargingTotal.add(disChargingTotal)
                .multiply(this.cost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal getCostOfGrid() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal getCostOfControl() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<StackedChartData> getStackedChartDataList() {
        StackedChartData chargingList = new StackedChartData(String.format("%s 储热", this.modelName), this.chargingList, 600);
        StackedChartData disChargingList = new StackedChartData(String.format("%s 放热", this.modelName), this.disChargingList, 600);
        return Arrays.asList(chargingList, disChargingList);
    }

    @Override
    public ThermalSaverModel clone() {
        ThermalSaverModel clone = (ThermalSaverModel) super.clone();

        // 深拷贝 BigDecimal 字段
        clone.totalStorageCapacity = new BigDecimal(this.totalStorageCapacity.toString());
        clone.currentStorage = new BigDecimal(this.currentStorage.toString());
        clone.chargingEfficiency = new BigDecimal(this.chargingEfficiency.toString());
        clone.dischargingEfficiency = new BigDecimal(this.dischargingEfficiency.toString());
        clone.thermalLossRate = new BigDecimal(this.thermalLossRate.toString());
        clone.carbonEmissionFactor = new BigDecimal(this.carbonEmissionFactor.toString());
        clone.cost = new BigDecimal(this.cost.toString());
        clone.purchaseCost = new BigDecimal(this.purchaseCost.toString());

        // 深拷贝 Timestamp
        clone.updatedAt = new Timestamp(this.updatedAt.getTime());

        // 字符串字段直接赋值
        clone.modelName = this.modelName;

        // id 字段赋值（可选）
        clone.id = this.id;

        // 忽略 @Transient 字段：chargingList, disChargingList, E_ESS_LIST

        return clone;
    }

}