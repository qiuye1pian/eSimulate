package org.core.model.load.electric;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.core.pso.simulator.TimeSeriesValue;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "load_scheme")
public class LoadScheme implements ElectricLoadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "loadScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoadValue> loadValues;

    @Override
    public int getDataLength() {
        return loadValues.size();
    }


}
