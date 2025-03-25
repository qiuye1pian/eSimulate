package org.esimulate.core.service.environment;

import lombok.NonNull;
import org.esimulate.core.model.environment.temperature.TemperatureScheme;
import org.esimulate.core.model.environment.temperature.TemperatureValue;
import org.esimulate.core.pojo.environment.TemperatureValueDto;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.repository.TemperatureSchemeRepository;
import org.esimulate.core.repository.TemperatureValueRepository;
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
public class TemperatureSchemeService {

    @Autowired
    private TemperatureSchemeRepository temperatureSchemeRepository;

    @Autowired
    private TemperatureValueRepository temperatureValueRepository;

    @Transactional(readOnly = true)
    public Page<TemperatureScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return temperatureSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return temperatureSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }


    @Transactional
    public TemperatureScheme addTemperatureScheme(String schemeName) {
        TemperatureScheme temperatureScheme = new TemperatureScheme(schemeName);
        temperatureScheme.setSchemeName(schemeName);
        temperatureScheme.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        temperatureSchemeRepository.save(temperatureScheme);
        return temperatureScheme;
    }

    @Transactional
    public TemperatureScheme updateTemperatureScheme(Long id, List<String> lineList) {
        Optional<TemperatureScheme> optionalTemperatureScheme = temperatureSchemeRepository.findById(id);
        if (!optionalTemperatureScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<TemperatureValue> temperatureValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, TemperatureValueDto::new)
                .stream()
                .map(TemperatureValueDto::toTemperatureValue)
                .collect(Collectors.toList());

        TemperatureScheme TemperatureScheme = optionalTemperatureScheme.get();
        TemperatureScheme.setTemperatureValues(temperatureValueList);
        temperatureValueList.forEach(x -> x.setTemperatureScheme(TemperatureScheme));
        TemperatureScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return temperatureSchemeRepository.save(TemperatureScheme);
    }

    @Transactional
    public void deleteTemperatureScheme(Long id) {
        temperatureSchemeRepository.deleteById(id);
    }

    @Transactional
    public TemperatureScheme createScheme(String schemeName, List<String> lineList) {
        Optional<TemperatureScheme> existing = temperatureSchemeRepository.findBySchemeName(schemeName);

        if (existing.isPresent()) {
            throw new IllegalArgumentException("方案名已存在: " + schemeName);
        }

        TemperatureScheme temperatureScheme = temperatureSchemeRepository.save(new TemperatureScheme(schemeName));

        List<TemperatureValue> temperatureValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, TemperatureValueDto::new)
                .stream()
                .map(TemperatureValueDto::toTemperatureValue)
                .peek(x -> x.setTemperatureScheme(temperatureScheme))
                .collect(Collectors.toList());

        temperatureValueRepository.saveAll(temperatureValueList);

        return temperatureScheme;
    }

    @Transactional(readOnly = true)
    public List<TemperatureValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return temperatureSchemeRepository.findById(id)
                .map(TemperatureScheme::getTemperatureValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(TemperatureValue::getDatetime))
                .collect(Collectors.toList());
    }
}
