package org.core.model.device;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class GasBoilerModel {

    // 燃气锅炉的燃烧效率 (η_GB)
    private final BigDecimal etaGB;

    private List<BigDecimal> gasBoilerOutputList;

    /**
     * 构造函数：初始化燃气锅炉参数
     *
     * @param etaGB 燃气锅炉的燃烧效率 (0~1)
     */
    public GasBoilerModel(String etaGB) {
        this.etaGB = new BigDecimal(etaGB);
    }

    /**
     * 计算燃气锅炉的供热功率 H_(GB,t)。
     *
     * @param P_heat     热负荷功率 (kW)
     * @param P_th_solar 光热电站的出力 (kW)
     * @return 燃气锅炉的供热功率 (kW)，若结果 < 0 则输出 0
     */
    public BigDecimal calculateHeatPower(BigDecimal P_heat, BigDecimal P_th_solar) {
        return P_heat.subtract(P_th_solar).max(BigDecimal.ZERO);
    }

    /**
     * 计算天然气消耗成本 F^GB。
     *
     * @param C_CH4     单位天然气价格 (元/kWh)
     * @param H_GB_list 燃气锅炉的供热功率列表 (kW)
     * @return 天然气消耗总成本
     */
    public BigDecimal calculateGasCost(BigDecimal C_CH4, List<BigDecimal> H_GB_list) {

        // 计算天然气消耗量
        return H_GB_list.stream()
                .map(H_GB -> C_CH4.multiply(H_GB.divide(etaGB, 10, RoundingMode.HALF_UP)))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 批量计算每小时的燃气锅炉供热功率 H_(GB,t)。
     *
     * @param P_heat_list     热负荷功率列表 (kW)
     * @param P_th_solar_list 光热电站出力列表 (kW)
     * @return 燃气锅炉的供热功率列表 (kW)
     */
    public List<BigDecimal> calculateHeatPowers(List<BigDecimal> P_heat_list, List<BigDecimal> P_th_solar_list) {
        if (P_heat_list.size() != P_th_solar_list.size()) {
            throw new IllegalArgumentException("热负荷功率列表和光热电站出力列表长度必须一致！");
        }

        List<BigDecimal> H_GB_list = new java.util.ArrayList<>();
        for (int i = 0; i < P_heat_list.size(); i++) {
            H_GB_list.add(calculateHeatPower(P_heat_list.get(i), P_th_solar_list.get(i)));
        }

        return H_GB_list;
    }

}
