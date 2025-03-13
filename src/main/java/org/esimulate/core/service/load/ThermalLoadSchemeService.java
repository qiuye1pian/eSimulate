package org.esimulate.core.service.load;

import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.pojo.ThermalLoadSchemeDto;
import org.esimulate.core.repository.ThermalLoadSchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ThermalLoadSchemeService {

    @Autowired
    private ThermalLoadSchemeRepository thermalLoadSchemeRepository;

    @Transactional(readOnly = true)
    public Page<ThermalLoadScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return thermalLoadSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return thermalLoadSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }

    @Transactional
    public ThermalLoadScheme addThermalLoadScheme(ThermalLoadSchemeDto thermalLoadSchemeDto) {
        ThermalLoadScheme thermalLoadScheme = new ThermalLoadScheme();
        thermalLoadScheme.setSchemeName(thermalLoadSchemeDto.getName());
        thermalLoadScheme.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        thermalLoadSchemeRepository.save(thermalLoadScheme);
        return thermalLoadScheme;
    }

    @Transactional
    public ThermalLoadScheme updateThermalLoadScheme(ThermalLoadSchemeDto thermalLoadSchemeDto) {
        Optional<ThermalLoadScheme> optionalThermalLoadScheme = thermalLoadSchemeRepository.findById(thermalLoadSchemeDto.getId());
        if (optionalThermalLoadScheme.isPresent()) {
            ThermalLoadScheme thermalLoadScheme = optionalThermalLoadScheme.get();
            thermalLoadScheme.setSchemeName(thermalLoadSchemeDto.getName());
            thermalLoadSchemeRepository.save(thermalLoadScheme);

            return null;
        }
        return null;
    }

    @Transactional
    public void deleteThermalLoadScheme(Long id) {
        thermalLoadSchemeRepository.deleteById(id);
    }
}