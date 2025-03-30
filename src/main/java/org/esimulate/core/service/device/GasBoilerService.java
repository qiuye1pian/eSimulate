package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.GasBoilerModel;
import org.esimulate.core.pojo.model.GasBoilerPageQuery;
import org.esimulate.core.repository.GasBoilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class GasBoilerService {

    @Autowired
    GasBoilerRepository gasBoilerRepository;

    @Transactional(readOnly = true)
    public Page<GasBoilerModel> findListByPage(GasBoilerPageQuery pageQuery) {
        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return gasBoilerRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return gasBoilerRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }


}
