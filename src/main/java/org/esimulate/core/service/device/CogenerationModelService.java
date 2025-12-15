package org.esimulate.core.service.device;

import org.esimulate.core.model.device.CogenerationModel;
import org.esimulate.core.pojo.model.CogenerationModelDto;
import org.esimulate.core.pojo.model.CogenerationPageQuery;
import org.esimulate.core.repository.CogenerationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class CogenerationModelService {

    @Autowired
    CogenerationRepository cogenerationRepository;

    @Transactional(readOnly = true)
    public Page<CogenerationModel> findListByPage(CogenerationPageQuery pageQuery) {

        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return cogenerationRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return cogenerationRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public CogenerationModel addCogenerationModel(CogenerationModelDto cogenerationModelDto) {
        Optional<CogenerationModel> existingModel = cogenerationRepository.findByModelName(cogenerationModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + cogenerationModelDto.getModelName());
        }

        CogenerationModel cogenerationModel = new CogenerationModel(cogenerationModelDto);

        return cogenerationRepository.save(cogenerationModel);
    }

    /**
     * 根据 ID 删除热电联产模型
     *
     * @param id 热电联产模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<CogenerationModel> existingModel = cogenerationRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        cogenerationRepository.deleteById(id);
    }

    @Transactional
    public CogenerationModel updateCogenerationModel(CogenerationModelDto cogenerationModelDto) {
        Optional<CogenerationModel> cogenerationModelOptional = cogenerationRepository.findById(cogenerationModelDto.getId());
        if (!cogenerationModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + cogenerationModelDto.getId());
        }

        CogenerationModel cogenerationModel = cogenerationModelOptional.get();
        cogenerationModel.setModelName(cogenerationModelDto.getModelName());

        cogenerationModel.setPMin(cogenerationModelDto.getPMin());
        cogenerationModel.setPMax(cogenerationModelDto.getPMax());
        cogenerationModel.setRampUpRate(cogenerationModelDto.getRampUpRate());
        cogenerationModel.setRampDownRate(cogenerationModelDto.getRampDownRate());
        cogenerationModel.setEtaElectric(cogenerationModelDto.getEtaElectric());
        cogenerationModel.setEtaLoss(cogenerationModelDto.getEtaLoss());
        cogenerationModel.setCOP(cogenerationModelDto.getCOP());
        cogenerationModel.setFlueGasRecoveryRate(cogenerationModelDto.getFlueGasRecoveryRate());
        cogenerationModel.setA(cogenerationModelDto.getA());
        cogenerationModel.setB(cogenerationModelDto.getB());
        cogenerationModel.setC(cogenerationModelDto.getC());
        cogenerationModel.setCv(cogenerationModelDto.getCv());

        cogenerationModel.setCarbonEmissionFactor(cogenerationModelDto.getCarbonEmissionFactor());
        cogenerationModel.setCost(cogenerationModelDto.getCost());
        cogenerationModel.setPurchaseCost(cogenerationModelDto.getPurchaseCost());
        cogenerationModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return cogenerationRepository.save(cogenerationModel);
    }

    @Transactional(readOnly = true)
    public CogenerationModel findById(Long id) {
        return cogenerationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的热点联产模型，ID: " + id));
    }

}
