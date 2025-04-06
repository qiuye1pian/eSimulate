package org.esimulate.core.service.environment;

import lombok.NonNull;
import org.esimulate.core.model.environment.water.WaterSpeedScheme;
import org.esimulate.core.model.environment.water.WaterSpeedValue;
import org.esimulate.core.pojo.environment.WaterSpeedValueDto;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.repository.WaterSpeedSchemeRepository;
import org.esimulate.core.repository.WaterSpeedValueRepository;
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
public class WaterSpeedSchemeService {

    @Autowired
    private WaterSpeedSchemeRepository waterSpeedSchemeRepository;

    @Autowired
    private WaterSpeedValueRepository waterSpeedValueRepository;

    @Transactional(readOnly = true)
    public Page<WaterSpeedScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return waterSpeedSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return waterSpeedSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }


    @Transactional
    public WaterSpeedScheme addWaterSpeedScheme(String schemeName) {
        WaterSpeedScheme waterSpeedScheme = new WaterSpeedScheme();
        waterSpeedScheme.setSchemeName(schemeName);
        waterSpeedSchemeRepository.save(waterSpeedScheme);
        return waterSpeedScheme;
    }

    @Transactional
    public WaterSpeedScheme updateWaterSpeedScheme(Long id, List<String> lineList) {
        Optional<WaterSpeedScheme> optionalWaterSpeedScheme = waterSpeedSchemeRepository.findById(id);
        if (!optionalWaterSpeedScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<WaterSpeedValue> waterSpeedValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, WaterSpeedValueDto::new)
                .stream()
                .map(WaterSpeedValueDto::toWaterSpeedValue)
                .collect(Collectors.toList());

        WaterSpeedScheme WaterSpeedScheme = optionalWaterSpeedScheme.get();
        WaterSpeedScheme.setWaterSpeedValues(waterSpeedValueList);
        waterSpeedValueList.forEach(x -> x.setWaterSpeedScheme(WaterSpeedScheme));
        WaterSpeedScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return waterSpeedSchemeRepository.save(WaterSpeedScheme);
    }

    @Transactional
    public void deleteWaterSpeedScheme(Long id) {
        waterSpeedSchemeRepository.deleteById(id);
    }

    @Transactional
    public WaterSpeedScheme createScheme(String schemeName, List<String> lineList) {
        Optional<WaterSpeedScheme> existing = waterSpeedSchemeRepository.findBySchemeName(schemeName);

        if (existing.isPresent()) {
            throw new IllegalArgumentException("方案名已存在: " + schemeName);
        }

        WaterSpeedScheme waterSpeedScheme = waterSpeedSchemeRepository.save(new WaterSpeedScheme(schemeName));

        List<WaterSpeedValue> waterSpeedValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, WaterSpeedValueDto::new)
                .stream()
                .map(WaterSpeedValueDto::toWaterSpeedValue)
                .peek(x -> x.setWaterSpeedScheme(waterSpeedScheme))
                .collect(Collectors.toList());

        waterSpeedValueRepository.saveAll(waterSpeedValueList);

        return waterSpeedScheme;
    }

    @Transactional(readOnly = true)
    public List<WaterSpeedValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return waterSpeedSchemeRepository.findById(id)
                .map(WaterSpeedScheme::getWaterSpeedValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(WaterSpeedValue::getDatetime))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WaterSpeedScheme findWithValuesById(Long id) {
        return waterSpeedSchemeRepository.findWithValuesById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的水流数据，ID: " + id));
    }
}
