package org.esimulate.core.converter;

import org.esimulate.core.pojo.simulate.PsoConfig;

import javax.persistence.Converter;

@Converter
public class PsoConfigConverter extends JsonAttributeConverter<PsoConfig> {
    public PsoConfigConverter() {
        super(PsoConfig.class);
    }
}