package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolarPowerModelDto {

    /**
     * 模型名
     */
    private String modelName;

    // 光伏系统额定功率 (kW)
    private BigDecimal P_pvN;

    // 光伏组件温度系数 (1/℃)，通常为负值
    private BigDecimal t_e;

    // 参考温度 (℃)
    private BigDecimal T_ref;

    // 参考辐照度 (W/m²)
    private BigDecimal G_ref;


}
