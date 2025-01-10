package org.core.model.environment.wind;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "wind_speed_values")
public class WindSpeedValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private WindSpeedScheme windSpeedScheme;

    @Column(name = "datetime", nullable = false)
    private Timestamp datetime;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
