package org.esimulate.core.service.device;

import org.esimulate.core.model.device.ThermalSaverModel;
import org.esimulate.core.pojo.model.ThermalSaverModelDto;
import org.esimulate.core.pojo.model.ThermalSaverPageQuery;
import org.esimulate.core.repository.ThermalSaverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ThermalSaverService {

    @Autowired
    ThermalSaverRepository thermalSaverRepository;

    @Transactional(readOnly = true)
    public Page<ThermalSaverModel> findListByPage(ThermalSaverPageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return thermalSaverRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return thermalSaverRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public ThermalSaverModel addThermalSaverModel(ThermalSaverModelDto thermalSaverModelDto) {
        Optional<ThermalSaverModel> existingModel = thermalSaverRepository.findByModelName(thermalSaverModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + thermalSaverModelDto.getModelName());
        }

        ThermalSaverModel thermalSaverModel = new ThermalSaverModel(thermalSaverModelDto);

        return thermalSaverRepository.save(thermalSaverModel);
    }

    /**
     * 根据 ID 删除电池模型
     *
     * @param id 电池模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<ThermalSaverModel> existingModel = thermalSaverRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        thermalSaverRepository.deleteById(id);
    }

    @Transactional
    public ThermalSaverModel updateThermalSaverModel(ThermalSaverModelDto thermalSaverModelDto) {
        Optional<ThermalSaverModel> thermalSaverModelOptional = thermalSaverRepository.findById(thermalSaverModelDto.getId());
        if (!thermalSaverModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + thermalSaverModelDto.getId());
        }

        ThermalSaverModel thermalSaverModel = thermalSaverModelOptional.get();
        thermalSaverModel.setModelName(thermalSaverModelDto.getModelName());
        thermalSaverModel.setModelName(thermalSaverModelDto.getModelName());
        thermalSaverModel.setTotalStorageCapacity(thermalSaverModelDto.getTotalStorageCapacity());
        thermalSaverModel.setCurrentStorage(thermalSaverModelDto.getCurrentStorage());
        thermalSaverModel.setChargingEfficiency(thermalSaverModelDto.getChargingEfficiency());
        thermalSaverModel.setDischargingEfficiency(thermalSaverModelDto.getDischargingEfficiency());
        thermalSaverModel.setThermalLossRate(thermalSaverModelDto.getThermalLossRate());
        thermalSaverModel.setCarbonEmissionFactor(thermalSaverModelDto.getCarbonEmissionFactor());
        thermalSaverModel.setPurchaseCost(thermalSaverModelDto.getPurchaseCost());
        thermalSaverModel.setCarbonEmissionFactor(thermalSaverModelDto.getCarbonEmissionFactor());
        thermalSaverModel.setPurchaseCost(thermalSaverModelDto.getPurchaseCost());
        thermalSaverModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return thermalSaverRepository.save(thermalSaverModel);

    }

    @Transactional(readOnly = true)
    public ThermalSaverModel findById(Long id) {
        return thermalSaverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的储热模型，ID: " + id));
    }
}
