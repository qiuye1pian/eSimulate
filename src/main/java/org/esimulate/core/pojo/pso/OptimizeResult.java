package org.esimulate.core.pojo.pso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.particle.Position;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptimizeResult {

    List<SimulateSnapshot> simulateSnapshotList = new ArrayList<>();

    private Position globalBestPosition;

    private BigDecimal globalBestValue;

    public void addSimulateSnapshotList(List<SimulateSnapshot> simulateSnapshotList) {

        SimulateSnapshot theBestSimulateSnapshot = simulateSnapshotList.stream()
                .min(Comparator.comparing(SimulateSnapshot::getFitnessValue))
                .orElseThrow(() -> new RuntimeException("没有找到最优解"));

        if (globalBestValue.compareTo(theBestSimulateSnapshot.getFitnessValue()) >= 0) {
            this.globalBestValue = theBestSimulateSnapshot.getFitnessValue();
            this.globalBestPosition = theBestSimulateSnapshot.getCurrentPosition().clone();
        }

    }
}
