package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.GasBoilerModel;
import org.esimulate.core.pojo.model.GasBoilerModelDto;
import org.esimulate.core.pojo.model.GasBoilerPageQuery;
import org.esimulate.core.repository.GasBoilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

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
    
    @Transactional
    public GasBoilerModel addGasBoilerModel(GasBoilerModelDto gasBoilerModelDto) {
        Optional<GasBoilerModel> existingModel = gasBoilerRepository.findByModelName(gasBoilerModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + gasBoilerModelDto.getModelName());
        }

        GasBoilerModel gasBoilerModel = new GasBoilerModel(gasBoilerModelDto);

        return gasBoilerRepository.save(gasBoilerModel);
    }

    /**
     * 根据 ID 删除风机模型
     *
     * @param id 风机模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<GasBoilerModel> existingModel = gasBoilerRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        gasBoilerRepository.deleteById(id);
    }

    @Transactional
    public GasBoilerModel updateGasBoilerModel(GasBoilerModelDto gasBoilerModelDto) {
        Optional<GasBoilerModel> gasBoilerModelOptional = gasBoilerRepository.findById(gasBoilerModelDto.getId());
        if (!gasBoilerModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + gasBoilerModelDto.getId());
        }

        GasBoilerModel gasBoilerModel = gasBoilerModelOptional.get();
        gasBoilerModel.setModelName(gasBoilerModelDto.getModelName());
        gasBoilerModel.setEtaGB(gasBoilerModelDto.getEtaGB());
        gasBoilerModel.setGasEnergyDensity(gasBoilerModelDto.getGasEnergyDensity());
        gasBoilerModel.setCarbonEmissionFactor(gasBoilerModelDto.getCarbonEmissionFactor());
        gasBoilerModel.setCost(gasBoilerModelDto.getCost());
        gasBoilerModel.setPurchaseCost(gasBoilerModelDto.getPurchaseCost());
        gasBoilerModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return gasBoilerRepository.save(gasBoilerModel);
    }

    @Transactional(readOnly = true)
    public GasBoilerModel findById(Long id) {
        return gasBoilerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的燃气锅炉模型，ID: " + id));
    }

}
