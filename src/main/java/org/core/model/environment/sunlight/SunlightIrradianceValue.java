package org.core.model.environment.sunlight;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sunlight_irradiance_values")
public class SunlightIrradianceValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private SunlightIrradianceScheme sunlightIrradianceScheme;

    @Column(name = "datetime", nullable = false)
    private Timestamp datetime;

    @Column(name = "irradiance", nullable = false)
    private Double irradiance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
