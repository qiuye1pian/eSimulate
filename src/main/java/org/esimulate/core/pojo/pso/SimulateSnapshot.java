package org.esimulate.core.pojo.pso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.particle.Position2;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimulateSnapshot {

    Position2 currentPosition;

    BigDecimal fitnessValue;

    SimulateResult simulateResult;
}
