package org.core.pso.simulator.result;


import lombok.Getter;

@Getter
public class SimulateResult {

    String message;

    SimulateResultType resultType;

    private SimulateResult(String message, SimulateResultType resultType) {
        this.message = message;
        this.resultType = resultType;
    }

    public static SimulateResult fail(String message) {
        return new SimulateResult(message, SimulateResultType.FAILED);
    }
}
