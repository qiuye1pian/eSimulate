package org.esimulate.core.controller.simulate;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.application.SimulateApplication;
import org.esimulate.core.pojo.simulate.SimulateConfigDto;
import org.esimulate.core.pso.simulator.result.SimulateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/simulate/simulator")
public class SimulateController {

    @Autowired
    SimulateApplication simulateApplication;

    @PostMapping("/do")
    public SimulateResult doSimulate(SimulateConfigDto simulateConfigDto) {
        return simulateApplication.doSimulate(simulateConfigDto);
    }
}
