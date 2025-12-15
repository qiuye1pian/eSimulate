package org.esimulate.core.model.environment.water;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.base.TimeSeriesData;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "waterSpeedScheme", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WaterSpeedValue> waterSpeedValues = new ArrayList<>();

    public WaterSpeedScheme(String schemeName) {
        this.schemeName = schemeName;
    }

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

    @Override
    public TimeSeriesData cutOffMoreThan(int i) {
        if (i <= 0) {
            // 保留 0 条
            this.waterSpeedValues.clear();
        } else {
            // 使用 Stream.limit 保留前 i 条，其余清除
            List<WaterSpeedValue> truncated = this.waterSpeedValues.stream()
                    .limit(i)
                    .collect(Collectors.toList());
            this.waterSpeedValues.clear();
            this.waterSpeedValues.addAll(truncated);
        }
        return this;
    }
}
