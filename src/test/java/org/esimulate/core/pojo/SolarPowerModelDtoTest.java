package org.esimulate.core.pojo;

import com.alibaba.fastjson2.JSONObject;
import org.esimulate.core.pojo.model.SolarPowerModelDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SolarPowerModelDtoTest {


    @Test
    public void test_toJson() {
        SolarPowerModelDto model = new SolarPowerModelDto();
        model.setId(1L);
        model.setModelName("Test Model");
        model.setPpvN(BigDecimal.valueOf(99.0));
        model.setTe(BigDecimal.valueOf(100.0));
        model.setTref(BigDecimal.valueOf(101.0));
        model.setGref(BigDecimal.valueOf(102.0));
        model.setCarbonEmissionFactor(BigDecimal.valueOf(103.0));
        model.setCost(BigDecimal.valueOf(104.0));
        model.setPurchaseCost(BigDecimal.valueOf(105.0));

        assertEquals("{\"carbonEmissionFactor\":103.0,\"cost\":104.0,\"gref\":102.0,\"id\":1,\"modelName\":\"Test Model\",\"ppvN\":99.0,\"purchaseCost\":105.0,\"te\":100.0,\"tref\":101.0}", JSONObject.toJSONString(model));
    }
}
