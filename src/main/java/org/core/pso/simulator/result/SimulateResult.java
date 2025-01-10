package org.core.pso.simulator.result;


import lombok.Getter;

@Getter
public class SimulateResult {

    private final HeatBalanceResult heatBalanceResult;

    public SimulateResult(HeatBalanceResult heatBalanceResult) {
        this.heatBalanceResult = heatBalanceResult;
    }


}
