package org.esimulate.core.controller.load;

import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.esimulate.util.DateTimeUtil.FORMATTER;

@RestController
@RequestMapping("/load/thermal-load-schemes")
public class ThermalLoadSchemeController {

    @Autowired
    private ThermalLoadSchemeService service;


    @PostMapping("/findList")
    public List<ThermalLoadScheme> findList() {
        return service.getAllSchemes();
    }

    @PostMapping
    public ThermalLoadScheme createScheme(@RequestBody ThermalLoadScheme scheme) {
        return service.saveScheme(scheme);
    }

    @PostMapping("/delete")
    public String deleteScheme(@RequestBody ThermalLoadScheme scheme) {
        service.deleteScheme(scheme.getId());
        return "deleted";
    }

}