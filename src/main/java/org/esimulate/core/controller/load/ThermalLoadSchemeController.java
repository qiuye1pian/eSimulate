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

    @GetMapping("/test")
    public Optional<ThermalLoadScheme> getSchemeByTest() {

        ThermalLoadScheme thermalLoadScheme = new ThermalLoadScheme("测试热负荷");
        ThermalLoadValue thermalLoadData1 = new ThermalLoadValue(thermalLoadScheme, LocalDateTime.parse("2024-03-09 14:00:00", FORMATTER), BigDecimal.valueOf(100));
        ThermalLoadValue thermalLoadData2 = new ThermalLoadValue(thermalLoadScheme, LocalDateTime.parse("2024-03-09 13:00:00", FORMATTER), BigDecimal.valueOf(200));
        thermalLoadScheme.addValue(thermalLoadData1);
        thermalLoadScheme.addValue(thermalLoadData2);

        return Optional.of(thermalLoadScheme);
    }

    @GetMapping("/testnull")
    public Optional<ThermalLoadScheme> getSchemeByTest2() {
        return Optional.empty();
    }

    @GetMapping("/teststring")
    public String getSchemeByTest3() {
        return "string value";
    }

    @GetMapping("/testtime")
    public LocalDateTime getSchemeByTest4() {
        return LocalDateTime.now();
    }
}