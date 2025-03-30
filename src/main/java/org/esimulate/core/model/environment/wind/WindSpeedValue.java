package org.esimulate.core.model.environment.wind;

import com.alibaba.fastjson2.annotation.JSONField;
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
@Table(name = "wind_speed_values")
public class WindSpeedValue implements EnvironmentValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JSONField(serialize = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private WindSpeedScheme windSpeedScheme;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "wind_speed", nullable = false)
    private BigDecimal windSpeed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public WindSpeedValue(BigDecimal windSpeed) {
        this.windSpeed = windSpeed;
    }

    @Override
    public BigDecimal getValue() {
        return windSpeed;
    }
}
