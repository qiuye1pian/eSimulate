package org.esimulate.core.model.environment.water;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "water_speed_values")
public class WaterSpeedValue implements EnvironmentValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JSONField(serialize = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private WaterSpeedScheme waterSpeedScheme;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "water_speed", nullable = false)
    private BigDecimal waterSpeed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public BigDecimal getValue() {
        return waterSpeed;
    }
}
