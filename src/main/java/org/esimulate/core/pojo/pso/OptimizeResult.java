package org.esimulate.core.pojo.pso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.pso.particle.Position;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptimizeResult {

    List<SimulateSnapshot> simulateSnapshotList = new ArrayList<>();

    private Position globalBestPosition;

    private BigDecimal globalBestValue = BigDecimal.valueOf(Double.MAX_VALUE);

    public void addSimulateSnapshotList(List<SimulateSnapshot> simulateSnapshotList) {
        simulateSnapshotList.stream()
                .filter(SimulateSnapshot::getIsValid)
                .min(Comparator.comparing(SimulateSnapshot::getFitnessValue))
                .ifPresent(theBestSimulateSnapshot -> {
                    if (globalBestValue.compareTo(theBestSimulateSnapshot.getFitnessValue()) >= 0) {
                        this.globalBestValue = theBestSimulateSnapshot.getFitnessValue();
                        this.globalBestPosition = theBestSimulateSnapshot.getCurrentPosition().clone();
                        log.info("global updated: globalBestPosition{}, globalBestValue:{}",
                                globalBestPosition.getCoordinateValueList(), globalBestValue);
                    }
                });

        this.simulateSnapshotList.addAll(simulateSnapshotList);
    }

    public List<SimulateSnapshot> getSimulateSnapshotList() {
        return this.simulateSnapshotList.stream()
                .sorted(Comparator.comparing(simulateSnapshot ->
                        String.format("%d, %f", simulateSnapshot.particleIndex, simulateSnapshot.getFitnessValue())))
                .collect(Collectors.toList());
    }
}
