package org.esimulate.core.service.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.GridModel;
import org.esimulate.core.pojo.model.GridModelDto;
import org.esimulate.core.pojo.model.GridPageQuery;
import org.esimulate.core.repository.GridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Log4j2
@Service
public class GridService {

    @Autowired
    GridRepository gridRepository;

    @Transactional(readOnly = true)
    public Page<GridModel> findListByPage(GridPageQuery pageQuery) {
        if (pageQuery.getModelName() == null || pageQuery.getModelName().trim().isEmpty()) {
            // ✅ 当 `modelName` 为空时，查询所有数据，但分页
            return gridRepository.findAll(pageQuery.toPageable());
        }

        // ✅ 当 `modelName` 有值时，执行 `LIKE` 查询
        return gridRepository.findByModelNameContaining(pageQuery.getModelName(), pageQuery.toPageable());
    }

    @Transactional
    public GridModel addGridModel(GridModelDto gridModelDto) {
        Optional<GridModel> existingModel = gridRepository.findByModelName(gridModelDto.getModelName());
        if (existingModel.isPresent()) {
            throw new IllegalArgumentException("模型名称已存在: " + gridModelDto.getModelName());
        }

        GridModel gridModel = new GridModel(gridModelDto);

        return gridRepository.save(gridModel);
    }

    /**
     * 根据 ID 删除风机模型
     *
     * @param id 风机模型 ID
     */
    @Transactional
    public void deleteById(Long id) {
        Optional<GridModel> existingModel = gridRepository.findById(id);
        if (!existingModel.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + id);
        }
        gridRepository.deleteById(id);
    }

    @Transactional
    public GridModel updateGridModel(GridModelDto gridModelDto) {
        Optional<GridModel> gridModelOptional = gridRepository.findById(gridModelDto.getId());
        if (!gridModelOptional.isPresent()) {
            throw new IllegalArgumentException("模型不存在，ID: " + gridModelDto.getId());
        }

        GridModel gridModel = gridModelOptional.get();
        gridModel.setModelName(gridModelDto.getModelName());
        gridModel.setGridPrice(gridModelDto.getGridPrice());
        gridModel.setCarbonEmissionFactor(gridModelDto.getCarbonEmissionFactor());
        gridModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return gridRepository.save(gridModel);
    }

    @Transactional(readOnly = true)
    public GridModel findById(Long id) {
        return gridRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的燃气锅炉模型，ID: " + id));
    }


}
