package org.core.model.environment.sunlight;

import java.math.BigDecimal;
import java.util.List;


public interface IrradianceData {
    /**
     * 获取光照强度数据列表
     *
     * @return 光照强度数据列表 (单位: W/m²)
     */
    List<BigDecimal> getIrradianceData();
}