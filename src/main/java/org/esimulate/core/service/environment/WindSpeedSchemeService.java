package org.esimulate.core.service.environment;

import lombok.NonNull;
import org.esimulate.core.model.environment.wind.WindSpeedScheme;
import org.esimulate.core.model.environment.wind.WindSpeedValue;
import org.esimulate.core.pojo.environment.WindSpeedValueDto;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.repository.WindSpeedSchemeRepository;
import org.esimulate.core.repository.WindSpeedValueRepository;
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
public class WindSpeedSchemeService {

    @Autowired
    private WindSpeedSchemeRepository windSpeedSchemeRepository;

    @Autowired
    private WindSpeedValueRepository windSpeedValueRepository;

    @Transactional(readOnly = true)
    public Page<WindSpeedScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return windSpeedSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return windSpeedSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }


    @Transactional
    public WindSpeedScheme addWindSpeedScheme(String schemeName) {
        WindSpeedScheme windSpeedScheme = new WindSpeedScheme();
        windSpeedScheme.setSchemeName(schemeName);
        windSpeedSchemeRepository.save(windSpeedScheme);
        return windSpeedScheme;
    }

    @Transactional
    public WindSpeedScheme updateWindSpeedScheme(Long id, List<String> lineList) {
        Optional<WindSpeedScheme> optionalWindSpeedScheme = windSpeedSchemeRepository.findById(id);
        if (!optionalWindSpeedScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<WindSpeedValue> windSpeedValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, WindSpeedValueDto::new)
                .stream()
                .map(WindSpeedValueDto::toWindSpeedValue)
                .collect(Collectors.toList());

        WindSpeedScheme WindSpeedScheme = optionalWindSpeedScheme.get();
        WindSpeedScheme.setWindSpeedValues(windSpeedValueList);
        windSpeedValueList.forEach(x -> x.setWindSpeedScheme(WindSpeedScheme));
        WindSpeedScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return windSpeedSchemeRepository.save(WindSpeedScheme);
    }

    @Transactional
    public void deleteWindSpeedScheme(Long id) {
        windSpeedSchemeRepository.deleteById(id);
    }

    @Transactional
    public WindSpeedScheme createScheme(String schemeName, List<String> lineList) {
        Optional<WindSpeedScheme> existing = windSpeedSchemeRepository.findBySchemeName(schemeName);

        if (existing.isPresent()) {
            throw new IllegalArgumentException("方案名已存在: " + schemeName);
        }

        WindSpeedScheme windSpeedScheme = windSpeedSchemeRepository.save(new WindSpeedScheme(schemeName));

        List<WindSpeedValue> windSpeedValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, WindSpeedValueDto::new)
                .stream()
                .map(WindSpeedValueDto::toWindSpeedValue)
                .peek(x -> x.setWindSpeedScheme(windSpeedScheme))
                .collect(Collectors.toList());

        windSpeedValueRepository.saveAll(windSpeedValueList);

        return windSpeedScheme;
    }

    @Transactional(readOnly = true)
    public List<WindSpeedValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return windSpeedSchemeRepository.findById(id)
                .map(WindSpeedScheme::getWindSpeedValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(WindSpeedValue::getDatetime))
                .collect(Collectors.toList());
    }
}
