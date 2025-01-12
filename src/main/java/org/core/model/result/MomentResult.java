package org.core.model.result;

import org.core.pso.simulator.facade.result.ResultFacade;
import org.core.pso.simulator.facade.result.carbon.Carbon;
import org.core.pso.simulator.facade.result.energy.Electricity;
import org.core.pso.simulator.facade.result.energy.Energy;

public class MomentResult implements ResultFacade {

    Electricity electricity;


    @Override
    public Energy getEnergy() {
        return null;
    }

    @Override
    public Carbon getCarbon() {
        return null;
    }
}
