package org.esimulate.core.model.environment.sunlight;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.base.TimeSeriesData;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "sunlightIrradianceScheme", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SunlightIrradianceValue> sunlightIrradianceValues = new ArrayList<>();

    public SunlightIrradianceScheme(String schemeName) {
        this.schemeName = schemeName;
    }

    @Override
    public int getDataLength() {
        return sunlightIrradianceValues.size();
    }

    @Override
    public EnvironmentValue getEnvironmentValue(Integer timeIndex) {
        return sunlightIrradianceValues.get(timeIndex);
    }

    @Override
    public TimeSeriesData cutOffMoreThan(int i) {
        if (i <= 0) {
            // 保留 0 条
            this.sunlightIrradianceValues.clear();
        } else {
            // 使用 Stream.limit 保留前 i 条，其余清除
            List<SunlightIrradianceValue> truncated = this.sunlightIrradianceValues.stream()
                    .limit(i)
                    .collect(Collectors.toList());
            this.sunlightIrradianceValues.clear();
            this.sunlightIrradianceValues.addAll(truncated);
        }
        return this;
    }
}
