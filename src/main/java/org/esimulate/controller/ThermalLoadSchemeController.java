package org.esimulate.controller;

import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.service.load.ThermalLoadSchemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/thermal-load-schemes")
public class ThermalLoadSchemeController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ThermalLoadSchemeService service;

    public ThermalLoadSchemeController(ThermalLoadSchemeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ThermalLoadScheme> getAllSchemes() {
        return service.getAllSchemes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThermalLoadScheme> getSchemeById(@PathVariable Long id) {
        Optional<ThermalLoadScheme> scheme = service.getSchemeById(id);
        return scheme.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ThermalLoadScheme createScheme(@RequestBody ThermalLoadScheme scheme) {
        return service.saveScheme(scheme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheme(@PathVariable Long id) {
        service.deleteScheme(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<ThermalLoadScheme> getSchemeByTest() {

        ThermalLoadScheme thermalLoadScheme = new ThermalLoadScheme();
        ThermalLoadValue thermalLoadData1 = new ThermalLoadValue(thermalLoadScheme, LocalDateTime.parse("2024-03-09 14:00:00", FORMATTER), BigDecimal.valueOf(100));
        ThermalLoadValue thermalLoadData2 = new ThermalLoadValue(thermalLoadScheme, LocalDateTime.parse("2024-03-09 13:00:00", FORMATTER), BigDecimal.valueOf(200));
        thermalLoadScheme.addValue(thermalLoadData1);
        thermalLoadScheme.addValue(thermalLoadData2);

        Optional<ThermalLoadScheme> scheme = Optional.of(thermalLoadScheme);
        return scheme.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}