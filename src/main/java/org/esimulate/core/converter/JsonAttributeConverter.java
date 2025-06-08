package org.esimulate.core.converter;

import com.alibaba.fastjson2.JSONObject;

import javax.persistence.AttributeConverter;

/**
 * 泛化的 JSON AttributeConverter。
 * 子类只需在构造器中传入具体的 T 类型 Class。
 */
public abstract class JsonAttributeConverter<T> implements AttributeConverter<T, String> {

    private final Class<T> clazz;

    protected JsonAttributeConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) return null;
        try {
            return JSONObject.toJSONString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize " + clazz.getSimpleName(), e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return JSONObject.parseObject(dbData, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize " + clazz.getSimpleName(), e);
        }
    }
}