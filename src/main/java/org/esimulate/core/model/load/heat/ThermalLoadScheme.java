package org.esimulate.core.model.load.heat;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.load.LoadValue;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "thermalLoadScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThermalLoadValue> thermalLoadValues = new ArrayList<>();

    @Override
    public int getDataLength() {
        return thermalLoadValues.size();
    }

    @Override
    public LoadValue getLoadValue(Integer timeIndex) {
        return thermalLoadValues.get(timeIndex);
    }

    public void addValue(ThermalLoadValue loadValue) {
        thermalLoadValues.add(loadValue);
    }
}