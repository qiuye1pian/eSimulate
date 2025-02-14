package org.core.model.environment.water;

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
@Table(name = "water_speed_scheme")
public class WaterSpeedScheme implements WaterSpeedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "waterSpeedScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaterSpeedValue> waterSpeedValues;

    @Override
    public int getDataLength() {
        return waterSpeedValues.size();
    }

    @Override
    public EnvironmentValue getEnvironmentValue(Integer timeIndex) {
        if (timeIndex < 0 || timeIndex >= waterSpeedValues.size()) {
            throw new IndexOutOfBoundsException("timeIndex 超出范围: " + timeIndex);
        }

        // 按 datetime 排序后返回第 timeIndex 个元素的 value
        return waterSpeedValues.stream()
                .sorted(Comparator.comparing(WaterSpeedValue::getDatetime))
                .collect(Collectors.toList())
                .get(timeIndex);
    }
}
