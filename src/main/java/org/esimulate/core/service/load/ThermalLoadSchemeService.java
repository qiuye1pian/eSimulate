package org.esimulate.core.service.load;

import lombok.NonNull;
import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.core.pojo.ElectricLoadValueDto;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.pojo.ThermalLoadSchemeDto;
import org.esimulate.core.pojo.ThermalLoadValueDto;
import org.esimulate.core.repository.ThermalLoadSchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ThermalLoadScheme updateThermalLoadScheme(Long id, List<String> thermalLoadValueListString) {
        Optional<ThermalLoadScheme> optionalThermalLoadScheme = thermalLoadSchemeRepository.findById(id);
        if (!optionalThermalLoadScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<ThermalLoadValue> thermalLoadValueList = ThermalLoadValueDto
                .convertByCsvContent(thermalLoadValueListString)
                .stream()
                .map(ThermalLoadValueDto::toThermalLoadValue)
                .collect(Collectors.toList());

        ThermalLoadScheme thermalLoadScheme = optionalThermalLoadScheme.get();
        thermalLoadScheme.setThermalLoadValues(thermalLoadValueList);
        thermalLoadValueList.forEach(x -> x.setThermalLoadScheme(thermalLoadScheme));
        thermalLoadScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return thermalLoadSchemeRepository.save(thermalLoadScheme);
    }

    @Transactional
    public void deleteThermalLoadScheme(Long id) {
        thermalLoadSchemeRepository.deleteById(id);
    }

    @Transactional
    public ThermalLoadScheme createScheme(@NonNull String schemeName, List<String> lineList) {
        List<ThermalLoadValue> thermalLoadValueList = ThermalLoadValueDto
                .convertByCsvContent(lineList)
                .stream()
                .map(ThermalLoadValueDto::toThermalLoadValue)
                .collect(Collectors.toList());

        return thermalLoadSchemeRepository.save(new ThermalLoadScheme(schemeName, thermalLoadValueList));
    }

    @Transactional(readOnly = true)
    public List<ThermalLoadValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return thermalLoadSchemeRepository.findById(id)
                .map(ThermalLoadScheme::getThermalLoadValues)
                .orElse(new ArrayList<>());
    }
}