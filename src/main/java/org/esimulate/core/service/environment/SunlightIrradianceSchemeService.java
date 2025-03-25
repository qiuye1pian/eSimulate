package org.esimulate.core.service.environment;

import lombok.NonNull;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceScheme;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceValue;
import org.esimulate.core.pojo.environment.SunlightIrradianceValueDto;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.repository.SunlightIrradianceSchemeRepository;
import org.esimulate.core.repository.SunlightIrradianceValueRepository;
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
public class SunlightIrradianceSchemeService {


    @Autowired
    private SunlightIrradianceSchemeRepository sunlightIrradianceSchemeRepository;

    @Autowired
    private SunlightIrradianceValueRepository sunlightIrradianceValueRepository;

    @Transactional(readOnly = true)
    public Page<SunlightIrradianceScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return sunlightIrradianceSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return sunlightIrradianceSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }


    @Transactional
    public SunlightIrradianceScheme addSunlightIrradianceScheme(String schemeName) {
        SunlightIrradianceScheme electricLoadScheme = new SunlightIrradianceScheme();
        electricLoadScheme.setSchemeName(schemeName);
        electricLoadScheme.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        sunlightIrradianceSchemeRepository.save(electricLoadScheme);
        return electricLoadScheme;
    }

    @Transactional
    public SunlightIrradianceScheme updateSunlightIrradianceScheme(Long id, List<String> lineList) {
        Optional<SunlightIrradianceScheme> optionalSunlightIrradianceScheme = sunlightIrradianceSchemeRepository.findById(id);
        if (!optionalSunlightIrradianceScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<SunlightIrradianceValue> sunlightIrradianceValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, SunlightIrradianceValueDto::new)
                .stream()
                .map(SunlightIrradianceValueDto::toSunlightIrradianceValue)
                .collect(Collectors.toList());

        SunlightIrradianceScheme SunlightIrradianceScheme = optionalSunlightIrradianceScheme.get();
        SunlightIrradianceScheme.setSunlightIrradianceValues(sunlightIrradianceValueList);
        sunlightIrradianceValueList.forEach(x -> x.setSunlightIrradianceScheme(SunlightIrradianceScheme));
        SunlightIrradianceScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return sunlightIrradianceSchemeRepository.save(SunlightIrradianceScheme);
    }

    @Transactional
    public void deleteSunlightIrradianceScheme(Long id) {
        sunlightIrradianceSchemeRepository.deleteById(id);
    }

    @Transactional
    public SunlightIrradianceScheme createScheme(String schemeName, List<String> lineList) {
        Optional<SunlightIrradianceScheme> existing = sunlightIrradianceSchemeRepository.findBySchemeName(schemeName);

        if (existing.isPresent()) {
            throw new IllegalArgumentException("方案名已存在: " + schemeName);
        }

        SunlightIrradianceScheme sunlightIrradianceScheme = sunlightIrradianceSchemeRepository.save(new SunlightIrradianceScheme(schemeName));

        List<SunlightIrradianceValue> sunlightIrradianceValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, SunlightIrradianceValueDto::new)
                .stream()
                .map(SunlightIrradianceValueDto::toSunlightIrradianceValue)
                .collect(Collectors.toList());

        sunlightIrradianceValueRepository.saveAll(sunlightIrradianceValueList);

        return sunlightIrradianceScheme;
    }

    @Transactional(readOnly = true)
    public List<SunlightIrradianceValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return sunlightIrradianceSchemeRepository.findById(id)
                .map(SunlightIrradianceScheme::getSunlightIrradianceValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(SunlightIrradianceValue::getDatetime))
                .collect(Collectors.toList());
    }
}
