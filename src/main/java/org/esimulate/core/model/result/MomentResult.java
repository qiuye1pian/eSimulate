package org.esimulate.core.model.result;

import org.esimulate.core.pso.simulator.facade.result.energy.Energy;

import java.util.List;

public class MomentResult {

    List<Energy> momentFinalEnergy;

    public MomentResult(List<Energy> afterProvideList) {
        this.momentFinalEnergy = afterProvideList;
    }

}
