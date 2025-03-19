package org.esimulate.core.service.load;

import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.repository.ElectricLoadSchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.DoubleStream;

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


}
