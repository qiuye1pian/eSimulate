package org.core.model.device;

import lombok.Getter;
import org.core.model.environment.sunlight.IrradianceData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ThermalPowerModel {

    // 光热转换效率 (η_SF)
    private final BigDecimal etaSF;
    // CSP 电站镜场面积 (S_SF, 单位: m²)
    private final BigDecimal SSF;
    // 模型数量
    private final int modelCount;
    // 每小时光热电站出力列表 (单位: kW)
    private List<BigDecimal> thermalPowerList;

    // 常量：用于将 W 转换为 kW
    private static final BigDecimal KW_CONVERSION_FACTOR = new BigDecimal("1000");

    /**
     * 构造函数：初始化光热电站参数
     *
     * @param etaSF      光热转换效率 (0~1)
     * @param SSF        CSP 电站镜场面积 (m²)
     * @param modelCount 模型数量
     */
    public ThermalPowerModel(String etaSF, String SSF, int modelCount) {
        this.etaSF = new BigDecimal(etaSF);
        this.SSF = new BigDecimal(SSF);
        this.modelCount = modelCount;
        this.thermalPowerList = new ArrayList<>();
    }

    /**
     * 批量计算光热电站的吸收热功率列表 (kW)，并保存到类内成员。
     *
     * @param irradianceData 实现了 IrradianceData 接口的光照数据源
     * @return 光热电站出力列表 (单位: kW)
     */
    public List<BigDecimal> calculateThermalPowerList(IrradianceData irradianceData) {
        // 清空之前的计算结果
        thermalPowerList = irradianceData.getIrradianceData().stream()
                .map(this::calculateThermalPower) // 调用单点计算方法
                .collect(Collectors.toList());
        return new ArrayList<>(thermalPowerList); // 返回计算结果
    }

    /**
     * 获取光热电站的出力列表 (kW)
     *
     * @return thermalPowerList
     */
    public List<BigDecimal> getThermalPowerList() {
        return new ArrayList<>(thermalPowerList);
    }

    /**
     * 计算单个时段的光热电站吸收热功率 (kW)。
     * <p>
     * 公式：P_th_solar(t) = η_SF * S_SF * D_t / 1000 * modelCount
     *
     * @param D_t 太阳光在时段 t 的平均直接辐射量 (DNI, 单位: W/m²)
     * @return t 时段的光热电站吸收热功率 (单位: kW)
     */
    private BigDecimal calculateThermalPower(BigDecimal D_t) {
        return etaSF.multiply(SSF).multiply(D_t)
                .divide(KW_CONVERSION_FACTOR, 10, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(modelCount));
    }
}
