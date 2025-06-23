package org.esimulate.core.pojo.simulate.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimulateResult {

    String message;

    SimulateResultType resultType;

    List<Indication> indicationList;

    StackedChartDto electricStackedChartDto;

    StackedChartDto thermalStackedChartDto;

    private SimulateResult(String message, SimulateResultType resultType) {
        this.message = message;
        this.resultType = resultType;
    }

    public static SimulateResult fail(String message) {
        return new SimulateResult(message, SimulateResultType.FAILED);
    }

}
