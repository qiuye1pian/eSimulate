package org.esimulate.core.service.load;


import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.repository.ThermalLoadSchemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThermalLoadSchemeService {

    private final ThermalLoadSchemeRepository repository;

    public ThermalLoadSchemeService(ThermalLoadSchemeRepository repository) {
        this.repository = repository;
    }

    public List<ThermalLoadScheme> getAllSchemes() {
        return repository.findAll();
    }

    public Optional<ThermalLoadScheme> getSchemeById(Long id) {
        return repository.findById(id);
    }

    public ThermalLoadScheme saveScheme(ThermalLoadScheme scheme) {
        return repository.save(scheme);
    }

    public void deleteScheme(Long id) {
        repository.deleteById(id);
    }
}