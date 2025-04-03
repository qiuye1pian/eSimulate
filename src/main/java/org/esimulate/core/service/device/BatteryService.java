package org.esimulate.core.service.device;

import org.esimulate.core.model.device.BatteryModel;
import org.esimulate.core.pojo.model.BatteryModelDto;
import org.esimulate.core.pojo.model.BatteryPageQuery;
import org.esimulate.core.repository.BatteryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class BatteryService {

    @Autowired
    BatteryRepository batteryRepository;

    @Transactional(readOnly = true)
    public Page<BatteryModel> findListByPage(BatteryPageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return batteryRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return batteryRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public BatteryModel addBatteryModel(BatteryModelDto batteryModelDto) {
        Optional<BatteryModel> existingModel = batteryRepository.findByModelName(batteryModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + batteryModelDto.getModelName());
        }

        BatteryModel batteryModel = new BatteryModel(batteryModelDto);

        return batteryRepository.save(batteryModel);
    }

    /**
     * 根据 ID 删除电池模型
     *
     * @param id 电池模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<BatteryModel> existingModel = batteryRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        batteryRepository.deleteById(id);
    }

    @Transactional
    public BatteryModel updateBatteryModel(BatteryModelDto batteryModelDto) {
        Optional<BatteryModel> batteryModelOptional = batteryRepository.findById(batteryModelDto.getId());
        if (!batteryModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + batteryModelDto.getId());
        }

        BatteryModel batteryModel = batteryModelOptional.get();
        batteryModel.setModelName(batteryModelDto.getModelName());
        batteryModel.setC_t(batteryModelDto.getCt());
        batteryModel.setE_ESS_t(batteryModelDto.getEESSt());
        batteryModel.setSOC_min(batteryModelDto.getSOCMin());
        batteryModel.setSOC_max(batteryModelDto.getSOCMax());
        batteryModel.setMu(batteryModelDto.getMu());
        batteryModel.setMaxChargePower(batteryModel.getMaxChargePower());
        batteryModel.setMaxDischargePower(batteryModel.getMaxDischargePower());
        batteryModel.setEtaHch(batteryModelDto.getEtaHch());
        batteryModel.setEtaHdis(batteryModelDto.getEtaHDis());
        batteryModel.setCarbonEmissionFactor(batteryModelDto.getCarbonEmissionFactor());
        batteryModel.setPurchaseCost(batteryModelDto.getPurchaseCost());
        batteryModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return batteryRepository.save(batteryModel);

    }

    @Transactional(readOnly = true)
    public BatteryModel findById(Long id) {
        return batteryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的电池模型，ID: " + id));
    }
}
