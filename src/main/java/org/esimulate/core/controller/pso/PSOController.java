package org.esimulate.core.controller.pso;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.application.PsoApplication;
import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/optimize/pso")
public class PSOController {

    @Autowired
    PsoApplication psoApplication;

    @PostMapping("/do")
    public OptimizeResult doOptimize(@RequestBody PsoConfig psoConfig) {
        return psoApplication.doPso(psoConfig);
    }

}
