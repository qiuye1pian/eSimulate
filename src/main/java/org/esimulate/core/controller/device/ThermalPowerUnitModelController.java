package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.ThermalPowerUnitModel;
import org.esimulate.core.pojo.model.ThermalPowerUnitModelDto;
import org.esimulate.core.pojo.model.ThermalPowerUnitPageQuery;
import org.esimulate.core.service.device.ThermalPowerUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/thermal-power-unit")
@Log4j2
public class ThermalPowerUnitModelController {

    @Autowired
    private ThermalPowerUnitService thermalPowerUnitService;


    @PostMapping("/getListByPage")
    public Page<ThermalPowerUnitModel> findListByPage(@RequestBody ThermalPowerUnitPageQuery pageQuery) {
        return thermalPowerUnitService.findListByPage(pageQuery);
    }

    @PostMapping("/add")
    public ThermalPowerUnitModel addThermalPowerUnitModel(@RequestBody ThermalPowerUnitModelDto thermalPowerModelDto) {
        if (thermalPowerModelDto.getId() != null) {
            return thermalPowerUnitService.updateThermalPowerUnitModel(thermalPowerModelDto);
        }
        return thermalPowerUnitService.addThermalPowerUnitModel(thermalPowerModelDto);
    }

    @PostMapping("/delete")
    public String deleteThermalPowerUnitModel(@RequestBody ThermalPowerUnitModelDto thermalPowerModelDto) {
        thermalPowerUnitService.deleteById(thermalPowerModelDto.getId());
        return "光热模型删除成功";
    }
}
