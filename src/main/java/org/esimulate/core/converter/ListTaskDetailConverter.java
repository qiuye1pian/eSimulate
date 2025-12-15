package org.esimulate.core.converter;

import com.alibaba.fastjson2.JSON;
import org.esimulate.core.model.task.TaskDetail;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class ListTaskDetailConverter implements AttributeConverter<List<TaskDetail>, String> {
    @Override
    public String convertToDatabaseColumn(List<TaskDetail> attribute) {
        return attribute == null ? null : JSON.toJSONString(attribute);
    }
    @Override
    public List<TaskDetail> convertToEntityAttribute(String dbData) {
        return dbData == null ? null : JSON.parseArray(dbData, TaskDetail.class);
    }
}