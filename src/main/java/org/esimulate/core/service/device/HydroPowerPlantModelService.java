package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.HydroPowerPlantModel;
import org.esimulate.core.pojo.model.HydroPowerPlantModelDto;
import org.esimulate.core.pojo.model.HydroPowerPlantPageQuery;
import org.esimulate.core.repository.HydroPowerPlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
public class HydroPowerPlantModelService {

    @Autowired
    HydroPowerPlantRepository hydroPowerPlantRepository;

    @Transactional(readOnly = true)
    public Page<HydroPowerPlantModel> findListByPage(HydroPowerPlantPageQuery pageQuery) {
        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return hydroPowerPlantRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return hydroPowerPlantRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public HydroPowerPlantModel addHydroPowerPlantModel(HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        Optional<HydroPowerPlantModel> existingModel = hydroPowerPlantRepository.findByModelName(hydroPowerPlantModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + hydroPowerPlantModelDto.getModelName());
        }

        HydroPowerPlantModel hydroPowerPlantModel = new HydroPowerPlantModel(hydroPowerPlantModelDto);

        return hydroPowerPlantRepository.save(hydroPowerPlantModel);
    }

    /**
     * 根据 ID 删除风机模型
     *
     * @param id 风机模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<HydroPowerPlantModel> existingModel = hydroPowerPlantRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        hydroPowerPlantRepository.deleteById(id);
    }

    @Transactional
    public HydroPowerPlantModel updateHydroPowerPlantModel(HydroPowerPlantModelDto hydroPowerPlantModelDto) {
        Optional<HydroPowerPlantModel> hydroPowerPlantModelOptional = hydroPowerPlantRepository.findById(hydroPowerPlantModelDto.getId());
        if (!hydroPowerPlantModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + hydroPowerPlantModelDto.getId());
        }

        HydroPowerPlantModel hydroPowerPlantModel = hydroPowerPlantModelOptional.get();

        hydroPowerPlantModel.modifyByDto(hydroPowerPlantModelDto);

        return hydroPowerPlantRepository.save(hydroPowerPlantModel);
    }

    @Transactional(readOnly = true)
    public HydroPowerPlantModel findById(Long id) {
        return hydroPowerPlantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的小水电模型，ID: " + id));
    }
}
