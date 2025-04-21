package org.esimulate.core.model.load.heat;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.load.LoadValue;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "thermal_load_scheme")
public class ThermalLoadScheme implements ThermalLoadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "thermalLoadScheme", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ThermalLoadValue> thermalLoadValues = new ArrayList<>();

    public ThermalLoadScheme(String schemeName) {
        this.schemeName = schemeName;
    }

    @Override
    public int getDataLength() {
        return thermalLoadValues.size();
    }

    @Override
    public LoadValue getLoadValue(Integer timeIndex) {
        return thermalLoadValues.get(timeIndex);
    }

    @Override
    public List<LocalDateTime> getDatetimeList() {
        return thermalLoadValues.stream().map(ThermalLoadValue::getDatetime).collect(Collectors.toList());
    }

    @Override
    public String getLoadName() {
        return this.schemeName;
    }

    @Override
    public List<BigDecimal> getLoadValueList() {
        return thermalLoadValues.stream().map(ThermalLoadValue::getLoadValue).collect(Collectors.toList());
    }
}