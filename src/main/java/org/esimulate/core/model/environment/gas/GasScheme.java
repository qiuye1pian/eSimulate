package org.esimulate.core.model.environment.gas;

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
@Table(name = "gas_scheme")
public class GasScheme implements GasData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "gasScheme", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GasValue> gasValues = new ArrayList<>();

    public GasScheme(String schemeName) {
        this.schemeName = schemeName;
    }

    @Override
    public int getDataLength() {
        return gasValues.size();
    }

    @Override
    public EnvironmentValue getEnvironmentValue(Integer timeIndex) {
        if (timeIndex < 0 || timeIndex >= gasValues.size()) {
            throw new IndexOutOfBoundsException("timeIndex 超出范围: " + timeIndex);
        }

        // 按 datetime 排序后返回第 timeIndex 个元素的 value
        return gasValues.stream()
                .sorted(Comparator.comparing(GasValue::getDatetime))
                .collect(Collectors.toList())
                .get(timeIndex);
    }

    @Override
    public TimeSeriesData cutOffMoreThan(int i) {
        if (i <= 0) {
            // 保留 0 条
            this.gasValues.clear();
        } else {
            // 使用 Stream.limit 保留前 i 条，其余清除
            List<GasValue> truncated = this.gasValues.stream()
                    .limit(i)
                    .collect(Collectors.toList());
            this.gasValues.clear();
            this.gasValues.addAll(truncated);
        }
        return this;
    }
}
