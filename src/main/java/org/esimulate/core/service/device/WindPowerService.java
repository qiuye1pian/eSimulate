package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.WindPowerModel;
import org.esimulate.core.pojo.WindPowerModelDto;
import org.esimulate.core.pojo.WindPowerPageQuery;
import org.esimulate.core.repository.WindPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
public class WindPowerService {

    @Autowired
    WindPowerRepository windPowerRepository;

    @Transactional(readOnly = true)
    public Page<WindPowerModel> findListByPage(WindPowerPageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return windPowerRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return windPowerRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }


    @Transactional
    public WindPowerModel addWindPowerModel(WindPowerModelDto windPowerModelDto) {
        Optional<WindPowerModel> existingModel = windPowerRepository.findByModelName(windPowerModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + windPowerModelDto.getModelName());
        }
        WindPowerModel windPowerModel = new WindPowerModel();
        windPowerModel.setModelName(windPowerModelDto.getModelName());
        windPowerModel.setP_r(windPowerModelDto.getP_r());
        windPowerModel.setV_in(windPowerModelDto.getV_in());
        windPowerModel.setV_n(windPowerModelDto.getV_n());
        windPowerModel.setV_out(windPowerModelDto.getV_out());
        return windPowerRepository.save(windPowerModel);
    }


    /**
     * 根据 ID 删除风机模型
     * @param id 风机模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<WindPowerModel> existingModel = windPowerRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        windPowerRepository.deleteById(id);
    }

}
