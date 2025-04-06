package org.esimulate.core.service.load;

import lombok.NonNull;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.pojo.load.ThermalLoadSchemeDto;
import org.esimulate.core.pojo.load.ThermalLoadValueDto;
import org.esimulate.core.repository.ThermalLoadSchemeRepository;
import org.esimulate.core.repository.ThermalLoadValueRepository;
import org.esimulate.util.TimeValueCsvConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThermalLoadSchemeService {

    @Autowired
    private ThermalLoadSchemeRepository thermalLoadSchemeRepository;

    @Autowired
    private ThermalLoadValueRepository thermalLoadValueRepository;

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
        thermalLoadSchemeRepository.save(thermalLoadScheme);
        return thermalLoadScheme;
    }

    @Transactional
    public ThermalLoadScheme updateThermalLoadScheme(Long id, List<String> thermalLoadValueListString) {
        Optional<ThermalLoadScheme> optionalThermalLoadScheme = thermalLoadSchemeRepository.findById(id);
        if (!optionalThermalLoadScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<ThermalLoadValue> thermalLoadValueList = TimeValueCsvConverter
                .convertByCsvContent(thermalLoadValueListString, ThermalLoadValueDto::new)
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

        Optional<ThermalLoadScheme> existing = thermalLoadSchemeRepository.findBySchemeName(schemeName);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("方案名已存在: " + schemeName);
        }

        ThermalLoadScheme thermalLoadScheme = thermalLoadSchemeRepository.save(new ThermalLoadScheme(schemeName));

        List<ThermalLoadValue> thermalLoadValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, ThermalLoadValueDto::new)
                .stream()
                .map(ThermalLoadValueDto::toThermalLoadValue)
                .peek(x -> x.setThermalLoadScheme(thermalLoadScheme))
                .collect(Collectors.toList());

        thermalLoadValueRepository.saveAll(thermalLoadValueList);

        return thermalLoadScheme;
    }

    @Transactional(readOnly = true)
    public List<ThermalLoadValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return thermalLoadSchemeRepository.findById(id)
                .map(ThermalLoadScheme::getThermalLoadValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(ThermalLoadValue::getDatetime))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ThermalLoadScheme findWithValuesById(Long id) {
        return thermalLoadSchemeRepository.findWithValuesById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的热负荷方案，ID: " + id));
    }
}