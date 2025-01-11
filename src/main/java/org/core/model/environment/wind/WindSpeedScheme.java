package org.core.model.environment.wind;

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
@Table(name = "wind_speed_scheme")
public class WindSpeedScheme implements WindSpeedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "windSpeedScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WindSpeedValue> windSpeedValues;

    public List<BigDecimal> getData(Integer timeIndex) {
        // 按 datetime 排序后提取 irradiance 值
        return windSpeedValues.stream()
                .sorted(Comparator.comparing(WindSpeedValue::getDatetime))
                .map(WindSpeedValue::getWindSpeed)
                .collect(Collectors.toList());
    }

    @Override
    public int getDataLength() {
        return windSpeedValues.size();
    }
}
