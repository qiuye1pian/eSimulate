package org.esimulate.core.service.device;

import org.esimulate.core.model.device.ThermalPowerModel;
import org.esimulate.core.pojo.model.ThermalPowerModelDto;
import org.esimulate.core.pojo.model.ThermalPowerPageQuery;
import org.esimulate.core.repository.ThermalPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

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

    @Transactional
    public ThermalPowerModel addThermalPowerModel(ThermalPowerModelDto thermalPowerModelDto) {
        Optional<ThermalPowerModel> existingModel = thermalPowerRepository.findByModelName(thermalPowerModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + thermalPowerModelDto.getModelName());
        }

        ThermalPowerModel thermalPowerModel = new ThermalPowerModel(thermalPowerModelDto);

        return thermalPowerRepository.save(thermalPowerModel);
    }

    /**
     * 根据 ID 删除光热模型
     *
     * @param id 光热模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<ThermalPowerModel> existingModel = thermalPowerRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        thermalPowerRepository.deleteById(id);
    }

    @Transactional
    public ThermalPowerModel updateThermalPowerModel(ThermalPowerModelDto thermalPowerModelDto) {
        Optional<ThermalPowerModel> thermalPowerModelOptional = thermalPowerRepository.findById(thermalPowerModelDto.getId());
        if (!thermalPowerModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + thermalPowerModelDto.getId());
        }

        ThermalPowerModel thermalPowerModel = thermalPowerModelOptional.get();
        thermalPowerModel.setModelName(thermalPowerModelDto.getModelName());
        thermalPowerModel.setEtaSF(thermalPowerModelDto.getEtaSF());
        thermalPowerModel.setSSF(thermalPowerModelDto.getSSF());
        thermalPowerModel.setCarbonEmissionFactor(thermalPowerModelDto.getCarbonEmissionFactor());
        thermalPowerModel.setCost(thermalPowerModelDto.getCost());
        thermalPowerModel.setPurchaseCost(thermalPowerModelDto.getPurchaseCost());
        thermalPowerModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return thermalPowerRepository.save(thermalPowerModel);

    }

    @Transactional(readOnly = true)
    public ThermalPowerModel findById(Long id) {
        return thermalPowerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的光热模型，ID: " + id));
    }

}
