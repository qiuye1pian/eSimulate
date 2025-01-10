package org.core.model.device;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GasBoilerModel {

    // 燃气锅炉的燃烧效率 (η_GB)
    private final BigDecimal etaGB;

    // 燃气锅炉出力 (kW)
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
            throw new IllegalArgumentException("热负荷数据和光热出力数据长度必须一致！");
        }

        List<BigDecimal> deficitList = new ArrayList<>();
        for (int i = 0; i < P_heat_list.size(); i++) {
            // 计算差额负荷 (热负荷 - 光热出力) 多余
            deficitList.add(P_heat_list.get(i).subtract(P_th_solar_list.get(i)).max(BigDecimal.ZERO));
        }
        gasBoilerOutputList = deficitList;
        return new ArrayList<>(deficitList);
    }

    /**
     * 获取燃气锅炉出力 (kW)
     *
     * @return thermalPowerList
     */
    public List<BigDecimal> gasBoilerOutputList() {
        return new ArrayList<>(gasBoilerOutputList);
    }


}
