package org.core.model.environment.temperature;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.core.pso.simulator.facade.environment.EnvironmentValue;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "temperature_values")
public class TemperatureValue implements EnvironmentValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private TemperatureScheme temperatureScheme;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "temperature_speed", nullable = false)
    private BigDecimal temperature;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public BigDecimal getValue() {
        return temperature;
    }
}
