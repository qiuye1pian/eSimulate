package org.esimulate.core.model.result;

import lombok.Data;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

@Data
public class MomentResult {

    List<Energy> curtailmentEnergyList;

    public MomentResult(List<Energy> afterProvideList) {
        this.curtailmentEnergyList = afterProvideList;
    }

}
