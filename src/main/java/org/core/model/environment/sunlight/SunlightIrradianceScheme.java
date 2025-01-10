package org.core.model.environment.sunlight;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
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
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "sunlightIrradianceScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SunlightIrradianceValue> sunlightIrradianceValues;

    @Override
    public List<BigDecimal> getIrradianceData() {
        // 按 datetime 排序后提取 irradiance 值
        return sunlightIrradianceValues.stream()
                .sorted(Comparator.comparing(SunlightIrradianceValue::getDatetime))
                .map(SunlightIrradianceValue::getIrradiance)
                .collect(Collectors.toList());
    }

    public SunlightIrradianceScheme(List<SunlightIrradianceValue> values) {
        this.sunlightIrradianceValues = values;
    }

}
