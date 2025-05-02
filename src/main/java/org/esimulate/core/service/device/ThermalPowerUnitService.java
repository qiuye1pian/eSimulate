package org.esimulate.core.service.device;

import org.esimulate.core.model.device.ThermalPowerUnitModel;
import org.esimulate.core.pojo.model.ThermalPowerUnitModelDto;
import org.esimulate.core.pojo.model.ThermalPowerUnitPageQuery;
import org.esimulate.core.repository.ThermalPowerUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ThermalPowerUnitService {

    @Autowired
    ThermalPowerUnitRepository thermalPowerUnitRepository;

    @Transactional(readOnly = true)
    public Page<ThermalPowerUnitModel> findListByPage(ThermalPowerUnitPageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return thermalPowerUnitRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return thermalPowerUnitRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public ThermalPowerUnitModel addThermalPowerUnitModel(ThermalPowerUnitModelDto thermalPowerUnitModelDto) {
        Optional<ThermalPowerUnitModel> existingModel = thermalPowerUnitRepository.findByModelName(thermalPowerUnitModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + thermalPowerUnitModelDto.getModelName());
        }

        ThermalPowerUnitModel thermalPowerUnitModel = new ThermalPowerUnitModel(thermalPowerUnitModelDto);

        return thermalPowerUnitRepository.save(thermalPowerUnitModel);
    }

    /**
     * 根据 ID 删除火电站模型
     *
     * @param id 火电站模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<ThermalPowerUnitModel> existingModel = thermalPowerUnitRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        thermalPowerUnitRepository.deleteById(id);
    }

    @Transactional
    public ThermalPowerUnitModel updateThermalPowerUnitModel(ThermalPowerUnitModelDto thermalPowerUnitModelDto) {
        Optional<ThermalPowerUnitModel> thermalPowerUnitModelOptional = thermalPowerUnitRepository.findById(thermalPowerUnitModelDto.getId());
        if (!thermalPowerUnitModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + thermalPowerUnitModelDto.getId());
        }

        ThermalPowerUnitModel thermalPowerUnitModel = thermalPowerUnitModelOptional.get();
        thermalPowerUnitModel.setModelName(thermalPowerUnitModelDto.getModelName());
        thermalPowerUnitModel.setMaxPower(thermalPowerUnitModelDto.getMaxPower());
        thermalPowerUnitModel.setMinPower(thermalPowerUnitModelDto.getMinPower());
        thermalPowerUnitModel.setStartupCost(thermalPowerUnitModelDto.getStartupCost());
        thermalPowerUnitModel.setA(thermalPowerUnitModelDto.getA());
        thermalPowerUnitModel.setB(thermalPowerUnitModelDto.getB());
        thermalPowerUnitModel.setC(thermalPowerUnitModelDto.getC());
        thermalPowerUnitModel.setAuxiliaryRate(thermalPowerUnitModelDto.getAuxiliaryRate());
        thermalPowerUnitModel.setEmissionRate(thermalPowerUnitModelDto.getEmissionRate());
        thermalPowerUnitModel.setMinStartupTime(thermalPowerUnitModelDto.getMinStartupTime());
        thermalPowerUnitModel.setMinShutdownTime(thermalPowerUnitModelDto.getMinShutdownTime());
        thermalPowerUnitModel.setRunningStatus(thermalPowerUnitModelDto.getRunningStatus());

        thermalPowerUnitModel.setCarbonEmissionFactor(thermalPowerUnitModelDto.getCarbonEmissionFactor());
        thermalPowerUnitModel.setCost(thermalPowerUnitModelDto.getCost());
        thermalPowerUnitModel.setPurchaseCost(thermalPowerUnitModelDto.getPurchaseCost());
        thermalPowerUnitModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return thermalPowerUnitRepository.save(thermalPowerUnitModel);
    }

    @Transactional(readOnly = true)
    public ThermalPowerUnitModel findById(Long id) {
        return thermalPowerUnitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的光热模型，ID: " + id));
    }

}
