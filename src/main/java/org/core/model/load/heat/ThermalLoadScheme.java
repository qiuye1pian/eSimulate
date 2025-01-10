package org.core.model.load.heat;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.core.model.environment.sunlight.SunlightIrradianceValue;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "thermal_load_scheme")
public class ThermalLoadScheme implements ThermalLoadData{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "thermalLoadScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThermalLoadValue> thermalLoadValues;

    @Override
    public List<BigDecimal> getThermalLoadData() {
        // 按 datetime 排序后提取 irradiance 值
        return thermalLoadValues.stream()
                .sorted(Comparator.comparing(ThermalLoadValue::getDatetime))
                .map(ThermalLoadValue::getLoadValue)
                .collect(Collectors.toList());
    }
}