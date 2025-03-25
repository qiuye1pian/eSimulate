package org.esimulate.core.model.environment.sunlight;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SunlightIrradianceSchemeTest {

    @Test
    public void test_to_Json() {
        SunlightIrradianceScheme scheme = new SunlightIrradianceScheme();
        scheme.setId(1L);
        scheme.setSchemeName("scheme");
        assertEquals("{\"dataLength\":0,\"id\":1,\"schemeName\":\"scheme\",\"sunlightIrradianceValues\":[]}", JSONObject.toJSONString((scheme)));
    }
}