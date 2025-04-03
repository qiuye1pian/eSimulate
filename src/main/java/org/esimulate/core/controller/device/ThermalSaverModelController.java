package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.ThermalSaverModel;
import org.esimulate.core.pojo.model.ThermalSaverModelDto;
import org.esimulate.core.pojo.model.ThermalSaverPageQuery;
import org.esimulate.core.service.device.ThermalSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/thermal-saver")
@Log4j2
public class ThermalSaverModelController {

    @Autowired
    private ThermalSaverService thermalSaverService;

    @PostMapping("/getListByPage")
    public Page<ThermalSaverModelDto> findListByPage(@RequestBody ThermalSaverPageQuery pageQuery) {
        return thermalSaverService.findListByPage(pageQuery).map(ThermalSaverModelDto::new);
    }

    /**
     * 新增电池模型
     *
     * @param thermalSaverModelDto 传入 JSON 数据
     * @return 返回新增的模型
     */
    @PostMapping("/add")
    public ThermalSaverModel addThermalSaverModel(@RequestBody ThermalSaverModelDto thermalSaverModelDto) {
        if (thermalSaverModelDto.getId() != null) {
            return thermalSaverService.updateThermalSaverModel(thermalSaverModelDto);
        }
        return thermalSaverService.addThermalSaverModel(thermalSaverModelDto);
    }


    /**
     * 删除电池模型
     *
     * @param thermalSaverModelDto 前端传递的 JSON，包含 `id`
     * @return 操作结果
     */
    @PostMapping("/delete")
    public String deleteThermalSaverModel(@RequestBody ThermalSaverModelDto thermalSaverModelDto) {
        thermalSaverService.deleteById(thermalSaverModelDto.getId());
        return "电池模型删除成功";
    }
}
