package org.esimulate.core.model.task;

import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.pso.SimulateSnapshot;
import org.esimulate.core.pso.particle.Coordinate;
import org.esimulate.core.pso.particle.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

class OptimizeTaskTest {

    @Test
    void setOptimizeResultUsesAnnualTotalCostTitle() {
        Position position = new Position();
        position.setCoordinateList(Arrays.asList(new Coordinate("风电", 1, 10, 3)));

        SimulateSnapshot snapshot = new SimulateSnapshot();
        snapshot.setParticleIndex(1);
        snapshot.setCurrentPosition(position);
        snapshot.setFitnessValue(new BigDecimal("2078643.26"));
        snapshot.setIsValid(true);
        snapshot.setMessage("");

        OptimizeResult optimizeResult = new OptimizeResult();
        optimizeResult.addSimulateSnapshotList(Arrays.asList(snapshot));

        OptimizeTask optimizeTask = new OptimizeTask();
        optimizeTask.setOptimizeResult(optimizeResult);

        Assertions.assertEquals(
                Arrays.asList("风电", "年度总成本", "约束"),
                optimizeTask.getPositionTitle());
    }
}
