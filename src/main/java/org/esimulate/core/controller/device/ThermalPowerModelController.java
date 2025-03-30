package org.esimulate.core.controller.device;

import lombok.extern.log4j.Log4j2;
import org.esimulate.core.model.device.ThermalPowerModel;
import org.esimulate.core.pojo.model.ThermalPowerPageQuery;
import org.esimulate.core.service.device.ThermalPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model/thermal-power")
@Log4j2
public class ThermalPowerModelController {

    @Autowired
    private ThermalPowerService thermalPowerService;


    @PostMapping("/getListByPage")
    public Page<ThermalPowerModel> findListByPage(@RequestBody ThermalPowerPageQuery pageQuery) {
        return thermalPowerService.findListByPage(pageQuery);
    }

//    @PostMapping("/add")
//    public ThermalPowerModel addThermalPowerModel(@RequestBody ThermalPowerModelDto thermalPowerModelDto) {
//        if (thermalPowerModelDto.getId() != null) {
//            return thermalPowerService.updateThermalPowerModel(thermalPowerModelDto);
//        }
//        return thermalPowerService.addThermalPowerModel(thermalPowerModelDto);
//    }

}
