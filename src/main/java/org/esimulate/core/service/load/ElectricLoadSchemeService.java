package org.esimulate.core.service.load;

import lombok.NonNull;
import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.core.pojo.ElectricLoadValueDto;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.repository.ElectricLoadSchemeRepository;
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
public class ElectricLoadSchemeService {

    @Autowired
    private ElectricLoadSchemeRepository electricLoadSchemeRepository;

    @Transactional(readOnly = true)
    public Page<ElectricLoadScheme> getListByPage(LoadPageQuery pageQuery) {
        if (pageQuery.getSchemeName() == null || pageQuery.getSchemeName().trim().isEmpty()) {
            // ✅ 当 `SchemeName` 为空时，查询所有数据，但分页
            return electricLoadSchemeRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `SchemeName` 有值时，执行 `LIKE` 查询
        return electricLoadSchemeRepository.findBySchemeNameContaining(pageQuery.getSchemeName(), pageQuery.toPageable());
    }


    @Transactional
    public ElectricLoadScheme addElectricLoadScheme(String schemeName) {
        ElectricLoadScheme electricLoadScheme = new ElectricLoadScheme();
        electricLoadScheme.setSchemeName(schemeName);
        electricLoadScheme.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        electricLoadSchemeRepository.save(electricLoadScheme);
        return electricLoadScheme;
    }

    @Transactional
    public ElectricLoadScheme updateElectricLoadScheme(Long id, List<String> electricLoadValueListString) {
        Optional<ElectricLoadScheme> optionalElectricLoadScheme = electricLoadSchemeRepository.findById(id);
        if (!optionalElectricLoadScheme.isPresent()) {
            throw new IllegalArgumentException("负荷不存在，ID: " + id);
        }

        List<ElectricLoadValue> electricLoadValueList = ElectricLoadValueDto
                .convertByCsvContent(electricLoadValueListString)
                .stream()
                .map(ElectricLoadValueDto::toElectricLoadValue)
                .collect(Collectors.toList());

        ElectricLoadScheme ElectricLoadScheme = optionalElectricLoadScheme.get();
        ElectricLoadScheme.setElectricLoadValues(electricLoadValueList);
        electricLoadValueList.forEach(x -> x.setElectricLoadScheme(ElectricLoadScheme));
        ElectricLoadScheme.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return electricLoadSchemeRepository.save(ElectricLoadScheme);
    }

    @Transactional
    public void deleteElectricLoadScheme(Long id) {
        electricLoadSchemeRepository.deleteById(id);
    }

    @Transactional
    public ElectricLoadScheme createScheme(String schemeName, List<String> lineList) {

        List<ElectricLoadValue> electricLoadValueList = ElectricLoadValueDto
                .convertByCsvContent(lineList)
                .stream()
                .map(ElectricLoadValueDto::toElectricLoadValue)
                .collect(Collectors.toList());

        return electricLoadSchemeRepository.save(new ElectricLoadScheme(schemeName, electricLoadValueList));
    }

    @Transactional(readOnly = true)
    public List<ElectricLoadValue> getLoadValuesBySchemeId(@NonNull Long id) {
        return electricLoadSchemeRepository.findById(id)
                .map(ElectricLoadScheme::getElectricLoadValues)
                .orElse(new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(ElectricLoadValue::getDatetime))
                .collect(Collectors.toList());
    }
}
