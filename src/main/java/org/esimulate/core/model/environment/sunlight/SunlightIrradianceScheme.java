package org.esimulate.core.model.environment.sunlight;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sunlight_irradiance_scheme")
public class SunlightIrradianceScheme implements IrradianceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "sunlightIrradianceScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SunlightIrradianceValue> sunlightIrradianceValues;

    public SunlightIrradianceScheme(List<SunlightIrradianceValue> values) {
        this.sunlightIrradianceValues = values;
    }

    @Override
    public int getDataLength() {
        return sunlightIrradianceValues.size();
    }

    @Override
    public EnvironmentValue getEnvironmentValue(Integer timeIndex) {
        return sunlightIrradianceValues.get(timeIndex);
    }
}
