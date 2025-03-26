package org.esimulate.core.model.load.electric;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.pso.simulator.facade.load.LoadValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.jetbrains.annotations.TestOnly;


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
    @JSONField(serialize = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private ElectricLoadScheme electricLoadScheme;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "load_value", nullable = false)
    private BigDecimal loadValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public Energy calculateDifference(List<Energy> produceList) {
        BigDecimal electricEnergyProduced = produceList.stream()
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // 用之前产出的电能 减去 当前负荷
        return new ElectricEnergy(electricEnergyProduced.subtract(this.loadValue));
    }

    @TestOnly
    public ElectricLoadValue(LocalDateTime datetime, BigDecimal loadValue) {
        this.datetime = datetime;
        this.loadValue = loadValue;
    }
}
