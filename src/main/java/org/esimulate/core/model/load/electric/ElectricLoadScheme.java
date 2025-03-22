package org.esimulate.core.model.load.electric;

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
@Table(name = "electric_load_scheme")
public class ElectricLoadScheme implements ElectricLoadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "electricLoadScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElectricLoadValue> electricLoadValues = new ArrayList<>();

    public ElectricLoadScheme(String schemeName, List<ElectricLoadValue> electricLoadValueList) {
        this.schemeName = schemeName;
        this.electricLoadValues = electricLoadValueList;
        this.electricLoadValues.forEach(electricLoadValue -> electricLoadValue.setElectricLoadScheme(this));
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public int getDataLength() {
        return electricLoadValues.size();
    }

    @Override
    public LoadValue getLoadValue(Integer timeIndex) {
        return electricLoadValues.get(timeIndex);
    }

    public void addValue(ElectricLoadValue loadValue) {
        this.electricLoadValues.add(loadValue);
    }

}
