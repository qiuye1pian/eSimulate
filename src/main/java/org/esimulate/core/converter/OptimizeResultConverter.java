package org.esimulate.core.converter;

import com.alibaba.fastjson2.JSONObject;
import org.esimulate.core.pojo.pso.OptimizeResult;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OptimizeResultConverter implements AttributeConverter<OptimizeResult, String> {
    @Override
    public String convertToDatabaseColumn(OptimizeResult attribute) {
        try {
            return JSONObject.toJSONString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize OptimizeResult", e);
        }
    }

    @Override
    public OptimizeResult convertToEntityAttribute(String dbData) {
        try {
            return JSONObject.parseObject(dbData, OptimizeResult.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize OptimizeResult", e);
        }
    }
}
