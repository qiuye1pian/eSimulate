package org.esimulate.core.pso.simulator.facade;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public abstract class Device {

    protected BigDecimal quantity;

    protected abstract BigDecimal getPurchaseCost();

    /**
     * 折现率（如：0.07 表示 7%）
     *
     * @return 折现率
     */
    @Transient
    protected abstract BigDecimal getDiscountRate();

    /**
     * 使用年限（单位：年）
     *
     * @return 使用年限
     */
    @Transient
    protected abstract Integer getLifetimeYears();

    @Transient
    protected abstract BigDecimal getCostOfOperation();

    @Transient
    protected abstract BigDecimal getCostOfGrid();

    @Transient
    protected abstract BigDecimal getCostOfControl();

    @Transient
    public BigDecimal getTotalCost() {
        // C_annual = C_cap + C_grid + C_op + C_con
        // 计算年化投资成本 C_cap
        BigDecimal C_cap = getAnnualInvestmentCost();

        // 计算 公共电网交互费用 C_grid
        BigDecimal c_grid = getCostOfGrid();

        // 计算 年度运行维护费用 C_op
        BigDecimal c_op = getCostOfOperation();

        // 计算 可控机组启停及运行成本 C_con
        BigDecimal c_con = getCostOfControl();

        return C_cap.add(c_grid).add(c_op).add(c_con);

    }


    private @NotNull BigDecimal getAnnualInvestmentCost() {
        BigDecimal r = getDiscountRate();
        int y = getLifetimeYears();
        BigDecimal onePlusR = BigDecimal.ONE.add(r);
        BigDecimal onePlusRPowerY = onePlusR.pow(y);
        BigDecimal annuityFactor = r.multiply(onePlusRPowerY)
                .divide(onePlusRPowerY.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        return getPurchaseCost()
                .multiply(quantity)
                .multiply(annuityFactor)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
