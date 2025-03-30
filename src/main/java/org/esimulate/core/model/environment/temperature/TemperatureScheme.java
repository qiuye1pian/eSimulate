package org.esimulate.core.model.environment.temperature;

import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "temperature_scheme")
public class TemperatureScheme implements TemperatureData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "temperatureScheme", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TemperatureValue> temperatureValues = new ArrayList<>();

    public TemperatureScheme(String schemeName) {
        this.schemeName = schemeName;
    }

    @Override
    public int getDataLength() {
        return temperatureValues.size();
    }

    @Override
    public EnvironmentValue getEnvironmentValue(Integer timeIndex) {
        if (timeIndex < 0 || timeIndex >= temperatureValues.size()) {
            throw new IndexOutOfBoundsException("timeIndex 超出范围: " + timeIndex);
        }

        // 按 datetime 排序后返回第 timeIndex 个元素的 value
        return temperatureValues.stream()
                .sorted(Comparator.comparing(TemperatureValue::getDatetime))
                .collect(Collectors.toList())
                .get(timeIndex);
    }
}
