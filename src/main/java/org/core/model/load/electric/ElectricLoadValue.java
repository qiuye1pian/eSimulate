package org.core.model.load.electric;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.core.model.result.energy.ElectricEnergy;
import org.core.model.result.energy.ThermalEnergy;
import org.core.pso.simulator.facade.load.LoadValue;
import org.core.pso.simulator.facade.result.energy.Energy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "electric_load_values")
public class ElectricLoadValue implements LoadValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private ElectricLoadScheme electricLoadScheme;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "load_value", nullable = false)
    private BigDecimal loadValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public Energy calculateDifference(List<Energy> produceList) {
        BigDecimal electricEnergyProduced = produceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 用当前负荷减去之前产出的电能
        return new ElectricEnergy(this.loadValue.subtract(electricEnergyProduced));
    }
}
