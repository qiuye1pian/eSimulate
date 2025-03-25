package org.esimulate.core.pojo;

import com.alibaba.fastjson2.JSONObject;
import org.esimulate.core.pojo.load.LoadValueChartDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class LoadValueChartDtoTest {

    @Test
    void testToJson() {
        // 构造测试数据
        List<String> xAxisData = Arrays.asList(
                "01-01 00:00:00",
                "01-01 01:00:00",
                "01-01 02:00:00");

        // 将对象转换为 JSON 字
        List<BigDecimal> seriesData = Arrays.asList(
                new BigDecimal("5.0"),
                new BigDecimal("10.0"),
                new BigDecimal("7.5"));

        LoadValueChartDto dto = new LoadValueChartDto(xAxisData, seriesData);
        // 将对象转换为 JSON 字符串
        String jsonString = JSONObject.toJSONString(dto);
        // 断言 JSON 字符串是否符合预期
        Assertions.assertEquals("{\"XAxis\":{\"axisLabel\":{\"formatter\":\"{value}\"},\"boundaryGap\":false,\"data\":[\"01-01 00:00:00\",\"01-01 01:00:00\",\"01-01 02:00:00\"],\"type\":\"category\"},\"YAxis\":{\"max\":13},\"series\":[{\"data\":[5.0,10.0,7.5],\"name\":\"\",\"smooth\":true,\"type\":\"line\"}]}", jsonString);
    }
}