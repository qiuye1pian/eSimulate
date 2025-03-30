package org.esimulate.core.service.device;

import org.esimulate.core.model.device.ThermalPowerModel;
import org.esimulate.core.pojo.model.ThermalPowerPageQuery;
import org.esimulate.core.repository.ThermalPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThermalPowerService {

    @Autowired
    ThermalPowerRepository thermalPowerRepository;

    @Transactional(readOnly = true)
    public Page<ThermalPowerModel> findListByPage(ThermalPowerPageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return thermalPowerRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return thermalPowerRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

}
