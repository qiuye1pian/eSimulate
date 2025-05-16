package org.esimulate.core.service.device;

import org.esimulate.core.model.device.PumpedStorageModel;
import org.esimulate.core.pojo.model.PumpedStorageModelDto;
import org.esimulate.core.pojo.model.PumpedStoragePageQuery;
import org.esimulate.core.repository.PumpedStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class PumpedStorageService {

    @Autowired
    PumpedStorageRepository pumpedStorageRepository;

    @Transactional(readOnly = true)
    public Page<PumpedStorageModel> findListByPage(PumpedStoragePageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return pumpedStorageRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return pumpedStorageRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public PumpedStorageModel addPumpedStorageModel(PumpedStorageModelDto pumpedStorageModelDto) {
        Optional<PumpedStorageModel> existingModel = pumpedStorageRepository.findByModelName(pumpedStorageModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + pumpedStorageModelDto.getModelName());
        }

        PumpedStorageModel pumpedStorageModel = new PumpedStorageModel(pumpedStorageModelDto);

        return pumpedStorageRepository.save(pumpedStorageModel);
    }

    /**
     * 根据 ID 删除火电站模型
     *
     * @param id 火电站模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<PumpedStorageModel> existingModel = pumpedStorageRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        pumpedStorageRepository.deleteById(id);
    }

    @Transactional
    public PumpedStorageModel updatePumpedStorageModel(PumpedStorageModelDto pumpedStorageModelDto) {
        Optional<PumpedStorageModel> pumpedStorageModelOptional = pumpedStorageRepository.findById(pumpedStorageModelDto.getId());
        if (!pumpedStorageModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + pumpedStorageModelDto.getId());
        }

        PumpedStorageModel pumpedStorageModel = pumpedStorageModelOptional.get();
        pumpedStorageModel.setModelName(pumpedStorageModelDto.getModelName());

        pumpedStorageModel.setPmax(pumpedStorageModelDto.getPmax());
        pumpedStorageModel.setEmax(pumpedStorageModelDto.getEmax());
        pumpedStorageModel.setEtaCh(pumpedStorageModelDto.getEtaCh());
        pumpedStorageModel.setEtaDis(pumpedStorageModelDto.getEtaDis());
        pumpedStorageModel.setLambda(pumpedStorageModelDto.getLambda());
        pumpedStorageModel.setStateOfCharge(pumpedStorageModelDto.getStateOfCharge());

        pumpedStorageModel.setCarbonEmissionFactor(pumpedStorageModelDto.getCarbonEmissionFactor());
        pumpedStorageModel.setCost(pumpedStorageModelDto.getCost());
        pumpedStorageModel.setPurchaseCost(pumpedStorageModelDto.getPurchaseCost());
        pumpedStorageModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return pumpedStorageRepository.save(pumpedStorageModel);
    }

    @Transactional(readOnly = true)
    public PumpedStorageModel findById(Long id) {
        return pumpedStorageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的光热模型，ID: " + id));
    }

}
