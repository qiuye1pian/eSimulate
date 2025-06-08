package org.esimulate.core.converter;

import org.esimulate.core.pojo.pso.OptimizeResult;

import javax.persistence.Converter;

@Converter
public class OptimizeResultConverter extends JsonAttributeConverter<OptimizeResult> {
    public OptimizeResultConverter() {
        super(OptimizeResult.class);
    }
}