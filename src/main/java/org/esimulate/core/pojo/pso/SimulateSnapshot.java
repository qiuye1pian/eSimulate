package org.esimulate.core.pojo.pso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.particle.Position;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimulateSnapshot {

    Position currentPosition;

    BigDecimal fitnessValue;

    SimulateResult simulateResult;
}
