package org.esimulate.core.converter;

import org.esimulate.core.pso.particle.Position;

import javax.persistence.Converter;

@Converter
public class PositionConverter extends JsonAttributeConverter<Position> {
    public PositionConverter() {
        super(Position.class);
    }
}