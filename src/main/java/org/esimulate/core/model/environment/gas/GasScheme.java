package org.esimulate.core.model.environment.gas;

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
@Table(name = "gas_scheme")
public class GasScheme implements GasData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "gasScheme", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GasValue> gasValues = new ArrayList<>();

    public GasScheme(String schemeName) {
        this.schemeName = schemeName;
        this.createdAt = new Timestamp(System.currentTimeMillis());
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
}
