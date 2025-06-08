package org.esimulate.core.converter;

import com.alibaba.fastjson2.JSONObject;
import org.esimulate.core.pojo.simulate.PsoConfig;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PsoConfigConverter implements AttributeConverter<PsoConfig, String> {

    @Override
    public String convertToDatabaseColumn(PsoConfig attribute) {
        try {
            return JSONObject.toJSONString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize PsoConfig", e);
        }
    }

    @Override
    public PsoConfig convertToEntityAttribute(String dbData) {
        try {
            return JSONObject.parseObject(dbData, PsoConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize PsoConfig", e);
        }
    }
}