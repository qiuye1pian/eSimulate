package org.esimulate.core.model.environment.sunlight;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.jetbrains.annotations.TestOnly;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sunlight_irradiance_values")
public class SunlightIrradianceValue implements EnvironmentValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JSONField(serialize = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private SunlightIrradianceScheme sunlightIrradianceScheme;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "irradiance", nullable = false)
    private BigDecimal irradiance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public SunlightIrradianceValue(BigDecimal irradiance) {
        this.irradiance = irradiance;
    }

    @TestOnly
    public SunlightIrradianceValue(LocalDateTime dateTime, BigDecimal irradiance) {
        this.datetime = dateTime;
        this.irradiance = irradiance;
    }

    @Override
    public BigDecimal getValue() {
        return irradiance;
    }
}
