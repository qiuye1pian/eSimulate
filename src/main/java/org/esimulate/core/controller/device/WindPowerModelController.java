package org.esimulate.core.controller.device;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/wind-power")
public class WindPowerModelController {

    @PostMapping("/get")
    public String getWindPowerModel() {
        return "Wind Power Model";
    }
}
