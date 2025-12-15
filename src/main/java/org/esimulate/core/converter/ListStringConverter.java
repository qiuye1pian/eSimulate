package org.esimulate.core.converter;

import com.alibaba.fastjson2.JSON;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class ListStringConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute == null ? null : JSON.toJSONString(attribute);
    }
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData == null ? null : JSON.parseArray(dbData, String.class);
    }
}