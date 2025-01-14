package org.core.model.result;

import org.core.pso.simulator.facade.result.MomentResultFacade;
import org.core.pso.simulator.facade.result.energy.Energy;

import java.math.BigDecimal;
import java.util.List;


public class MomentResult implements MomentResultFacade {

    List<Energy> momentFinalEnergy;

    public MomentResult(List<Energy> afterProvideList) {
        this.momentFinalEnergy = afterProvideList;
    }

    @Override
    public Boolean isUnqualified() {
        return momentFinalEnergy.stream().anyMatch(x -> x.getValue().compareTo(BigDecimal.ZERO) < 0);
    }
}
