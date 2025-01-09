package org.core.model.load.heat;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "thermal_load_values")
public class ThermalLoadValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private ThermalLoadScheme thermalLoadScheme;

    @Column(name = "datetime", nullable = false)
    private Timestamp datetime;

    @Column(name = "load_value", nullable = false)
    private Double loadValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
