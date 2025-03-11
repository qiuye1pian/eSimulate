package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.SolarPowerModel;
import org.esimulate.core.pojo.ModelPageQuery;
import org.esimulate.core.pojo.SolarPowerModelDto;
import org.esimulate.core.pojo.SolarPowerPageQuery;
import org.esimulate.core.pojo.WindPowerPageQuery;
import org.esimulate.core.repository.SolarPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
public class SolarPowerService {

    @Autowired
    SolarPowerRepository solarPowerRepository;

    @Transactional(readOnly = true)
    public Page<SolarPowerModel> findListByPage(SolarPowerPageQuery pageQuery) {
        return solarPowerRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public SolarPowerModel addSolarPowerModel(SolarPowerModelDto solarPowerModelDto) {
        Optional<SolarPowerModel> existingModel = solarPowerRepository.findByModelName(solarPowerModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + solarPowerModelDto.getModelName());
        }
        SolarPowerModel solarPowerModel = new SolarPowerModel();
        solarPowerModel.setModelName(solarPowerModelDto.getModelName());
        solarPowerModel.setP_pvN(solarPowerModelDto.getP_pvN());
        solarPowerModel.setT_e(solarPowerModelDto.getT_e());
        solarPowerModel.setT_ref(solarPowerModelDto.getT_ref());
        solarPowerModel.setG_ref(solarPowerModelDto.getG_ref());

        return solarPowerRepository.save(solarPowerModel);
    }


    /**
     * 根据 ID 删除模型
     * @param id 模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<SolarPowerModel> existingModel = solarPowerRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        solarPowerRepository.deleteById(id);
    }

}
