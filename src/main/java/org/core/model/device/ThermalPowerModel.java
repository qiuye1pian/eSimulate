package org.core.model.device;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 光热电站模型类
 * 用于计算光热电站吸收的热功率。
 */
@Data
public class ThermalPowerModel {

    // 常量：用于将 W 转换为 MW
    private static final BigDecimal MW_CONVERSION_FACTOR = new BigDecimal("1000000");

    // 光热转换效率 (η_SF)
    private final BigDecimal etaSF;

    // CSP 电站镜场面积 (S_SF, 单位: m²)
    private final BigDecimal SSF;

    /**
     * 构造函数：初始化光热电站参数
     *
     * @param etaSF 光热转换效率 (0~1)
     * @param SSF   CSP 电站镜场面积 (m²)
     */
    public ThermalPowerModel(String etaSF, String SSF) {
        this.etaSF = new BigDecimal(etaSF);
        this.SSF = new BigDecimal(SSF);
    }

    /**
     * 计算 t 时段集热装置吸收的热功率 (MW)。
     * <p>
     * 公式：P_th_solar(t) = η_SF * S_SF * D_t / 1e6
     *
     * @param D_t 太阳光在时段 t 的平均直接辐射量 (DNI, 单位: W/m²)
     * @return t 时段光热电站吸收的热功率 (单位: MW)
     */
    public BigDecimal calculateThermalPower(BigDecimal D_t) {
        // 将公式中的结果转换为 MW，因此需要除以 1e6
        return etaSF.multiply(SSF).multiply(D_t).divide(MW_CONVERSION_FACTOR, 10, RoundingMode.HALF_UP);
    }

    /**
     * 批量计算多个时段的光热电站吸收的热功率 (MW)。
     *
     * @param D_t_List 太阳光在各时段的平均直接辐射量列表 (单位: W/m²)
     * @return 每个时段对应的光热电站吸收的热功率列表 (单位: MW)
     */
    public List<BigDecimal> calculateThermalPower(List<BigDecimal> D_t_List) {
        return D_t_List.stream().map(this::calculateThermalPower).collect(Collectors.toList());
    }

}

