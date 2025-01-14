package org.core.model.environment.wind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.core.pso.simulator.facade.environment.EnvironmentValue;

import javax.persistence.*;
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

    @Override
    public int getDataLength() {
        return windSpeedValues.size();
    }

    @Override
    public EnvironmentValue getEnvironmentValue(Integer timeIndex) {
        if (timeIndex < 0 || timeIndex >= windSpeedValues.size()) {
            throw new IndexOutOfBoundsException("timeIndex 超出范围: " + timeIndex);
        }

        // 按 datetime 排序后返回第 timeIndex 个元素的 value
        return windSpeedValues.stream()
                .sorted(Comparator.comparing(WindSpeedValue::getDatetime))
                .collect(Collectors.toList())
                .get(timeIndex);
    }
}
