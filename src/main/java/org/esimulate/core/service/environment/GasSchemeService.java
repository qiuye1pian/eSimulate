package org.esimulate.core.service.environment;

import lombok.NonNull;
import org.esimulate.core.model.environment.gas.GasScheme;
import org.esimulate.core.model.environment.gas.GasValue;
import org.esimulate.core.pojo.environment.GasValueDto;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.repository.GasSchemeRepository;
import org.esimulate.core.repository.GasValueRepository;
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
public class GasSchemeService {

    @Autowired
    private GasSchemeRepository gasSchemeRepository;

    @Autowired
    private GasValueRepository gasValueRepository;

    @Transactional(readOnly = true)
    public Page<GasScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return gasSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return gasSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }


    @Transactional
    public GasScheme addGasScheme(String schemeName) {
        GasScheme gasScheme = new GasScheme();
        gasScheme.setSchemeName(schemeName);
        gasSchemeRepository.save(gasScheme);
        return gasScheme;
    }

    @Transactional
    public GasScheme updateGasScheme(Long id, List<String> lineList) {
        Optional<GasScheme> optionalGasScheme = gasSchemeRepository.findById(id);
        if (!optionalGasScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<GasValue> gasValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, GasValueDto::new)
                .stream()
                .map(GasValueDto::toGasValue)
                .collect(Collectors.toList());

        GasScheme GasScheme = optionalGasScheme.get();
        GasScheme.setGasValues(gasValueList);
        gasValueList.forEach(x -> x.setGasScheme(GasScheme));
        GasScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return gasSchemeRepository.save(GasScheme);
    }

    @Transactional
    public void deleteGasScheme(Long id) {
        gasSchemeRepository.deleteById(id);
    }

    @Transactional
    public GasScheme createScheme(String schemeName, List<String> lineList) {
        Optional<GasScheme> existing = gasSchemeRepository.findBySchemeName(schemeName);

        if (existing.isPresent()) {
            throw new IllegalArgumentException("方案名已存在: " + schemeName);
        }

        GasScheme gasScheme = gasSchemeRepository.save(new GasScheme(schemeName));

        List<GasValue> gasValueList = TimeValueCsvConverter
                .convertByCsvContent(lineList, GasValueDto::new)
                .stream()
                .map(GasValueDto::toGasValue)
                .peek(x -> x.setGasScheme(gasScheme))
                .collect(Collectors.toList());

        gasValueRepository.saveAll(gasValueList);

        return gasScheme;
    }

    @Transactional(readOnly = true)
    public List<GasValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return gasSchemeRepository.findById(id)
                .map(GasScheme::getGasValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(GasValue::getDatetime))
                .collect(Collectors.toList());
    }
    
}
