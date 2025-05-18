package org.esimulate.core.model.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.indication.calculator.NonRenewableEnergyDevice;
import org.esimulate.core.pso.particle.Dimension;
import org.esimulate.core.pso.simulator.facade.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "hydro_power_plant_model")
@AllArgsConstructor
@NoArgsConstructor
public class CogenerationModel extends Device implements Producer, Adjustable,
        Dimension, ElectricDevice, ThermalDevice, NonRenewableEnergyDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String modelName;

    //最大发电功率 Pmax (kW)
    @Column(nullable = false)
    private BigDecimal Pmax;

    //最小发电功率 Pmin (kW)
    @Column(nullable = false)
    private BigDecimal Pmin;

    //最大供热功率 Qmax (kW)
    @Column(nullable = false)
    private BigDecimal Qmax;

    // 向上爬坡速率（单位：kW）
    @Column(nullable = false)
    private BigDecimal rampUpRate;

    // 向下爬坡速率（单位：kW）
    @Column(nullable = false)
    private BigDecimal rampDownRate;

    //发电效率
    @Column(nullable = false)
    private BigDecimal etaElectric;

    //散热损失率
    @Column(nullable = false)
    private BigDecimal etaLoss;

    //溴冷机的制热系数
    @Column(nullable = false)
    private BigDecimal COP;

    //烟气回收率
    @Column(nullable = false)
    private BigDecimal flueGasRecoveryRate;

    //天然气低热值 默认值取 9.7 kW·h/m3
    @Column(nullable = false)
    private BigDecimal gasLHV;

    //运行成本系数 a ["CNY"⋅("MW"⋅"h" )^(-1)]
    @Column(nullable = false)
    private BigDecimal a;

    //运行成本系数 b ["CNY"⋅("MW"⋅"h" )^(-1)]
    @Column(nullable = false)
    private BigDecimal b;

    //运行成本系数 c ("CNY"⋅"h" ^(-1))
    @Column(nullable = false)
    private BigDecimal c;

    // 碳排放因子
    @Column(nullable = false)
    private BigDecimal carbonEmissionFactor;

    // 天然气单价 默认值取2.5元/m3
    @Column(nullable = false)
    private BigDecimal cost;

    // 建设成本
    @Column(nullable = false)
    private BigDecimal purchaseCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;



}
